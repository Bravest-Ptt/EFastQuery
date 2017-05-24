package bravest.ptt.efastquery.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.w3c.dom.Text;

import java.net.URI;
import java.net.URL;
import java.util.UUID;

import bravest.ptt.androidlib.activity.BaseActivity;
import bravest.ptt.androidlib.net.RemoteService;
import bravest.ptt.androidlib.net.RequestParam;
import bravest.ptt.androidlib.utils.DialogUtils;
import bravest.ptt.androidlib.utils.PreferencesUtils;
import bravest.ptt.androidlib.utils.ToastUtils;
import bravest.ptt.androidlib.utils.bmob.BmobConstants;
import bravest.ptt.androidlib.utils.plog.PLog;
import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.entity.ProfileEntity;
import bravest.ptt.efastquery.entity.SmsCodeEntity;
import bravest.ptt.efastquery.entity.User;
import bravest.ptt.efastquery.net.AbstractRequestCallback;
import bravest.ptt.efastquery.utils.API;
import bravest.ptt.efastquery.utils.UserUtils;
import bravest.ptt.efastquery.utils.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterVerifyActivity extends BaseActivity {

    private static final String TAG = "RegisterVerifyActivity";

    public static final int REQUEST_CODE_CLIP = 1001;

    public static final int REQUEST_CODE_GALLERY = 1002;

    public static final String PROFILE_URL = "profile_url";

    private EditText mVerifyCodeEditor;

    private EditText mUserNameEditor;

    private CircleImageView mMaleImageView;

    private CircleImageView mFemaleImageView;

    private TextView mRegister;

    private TextView mRegisterAlready;

    private EditText mSexProfileEditor;

    private boolean mIsMale = true;

    //用户是不是已经完成了头像设置
    private boolean mIsProfileDone = false;

    private SmsCodeEntity mSmsCodeEntity;

    private String mPassword;

    private ProgressDialog mDialog;

    private String mProfilePath;

    @Override
    protected void initVariables() {
        Intent intent = getIntent();
        if (intent != null) {
            mSmsCodeEntity = (SmsCodeEntity) intent.getExtras().get(SmsCodeEntity.getName());
            PLog.log(mSmsCodeEntity);
            mPassword = intent.getStringExtra(User.PASSWORD);
            PLog.log(mPassword);
        }
    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_register_verify);
        mVerifyCodeEditor = (EditText) findViewById(R.id.verification_editor);
        mUserNameEditor = (EditText) findViewById(R.id.username_editor);
        mMaleImageView = (CircleImageView) findViewById(R.id.male_profile_image);
        mFemaleImageView = (CircleImageView) findViewById(R.id.female_profile_image);
        mRegister = (TextView) findViewById(R.id.register);
        mRegisterAlready = (TextView) findViewById(R.id.register_already);
        mSexProfileEditor = (EditText) findViewById(R.id.sex_profile_editor);

        Utils.popSoftInput(mContext, mVerifyCodeEditor);
        mSexProfileEditor.setHint(R.string.verify_sex_profile);

        mMaleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleMaleImageClick();
            }
        });

        mMaleImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                handleMaleImageLongClick();
                return true;
            }
        });

        mFemaleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFemaleImageClick();
            }
        });

        mFemaleImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                handleFemaleImageLongClick();
                return true;
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegisterClick();
            }
        });

        mRegisterAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegisterAlreadyClick();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PLog.log("REQUEST_CODE = " + requestCode);
        if (data == null) {
            PLog.log("data is null");
            return;
        }
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                PLog.log("url = " + uri);
                Intent intent = new Intent(mActivity, ClipImageActivity.class);
                intent.putExtra(PROFILE_URL, uri.toString());
                startActivityForResult(intent, REQUEST_CODE_CLIP);
            } else if (resultCode == RESULT_CANCELED) {
                mIsProfileDone = false;
                PLog.log("profile canceled");
            }
        } else if (requestCode == REQUEST_CODE_CLIP) {
            if (resultCode == RESULT_OK) {
                String file = data.getStringExtra(PROFILE_URL);
                mProfilePath = file;
                PLog.log("file = " + file);
                mIsProfileDone = true;
                ImageView profile;
                if (mIsMale) {
                    profile = mMaleImageView;
//                    mMaleImageView.setImageBitmap(
//                            Utils.getBitmapFromUri(mContext, Uri.parse(uri)));
                } else {
                    profile = mFemaleImageView;
                }

                //使用Glide会导致有图片缓存,why? 因为开启了内存缓存
                Glide.with(mActivity)
                        .load(file)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(profile);
            } else if (resultCode == RESULT_CANCELED) {
                mIsProfileDone = false;
                PLog.log("profile canceled");
            }
        }
    }

    private void handleMaleImageClick() {
        PLog.log("handleMaleImageClick");
        mFemaleImageView.setVisibility(View.GONE);
        mSexProfileEditor.setHint(R.string.verify_sex_rechoose);
        mIsMale = true;
        chooseProfileFromGallery();
    }

    private void handleMaleImageLongClick() {
        mFemaleImageView.setVisibility(View.VISIBLE);
        mSexProfileEditor.setHint(R.string.verify_sex_profile);
        mIsProfileDone = false;
        mMaleImageView.setImageResource(R.mipmap.male);
        PLog.log("handleMaleImageLongClick");
    }

    private void handleFemaleImageClick() {
        PLog.log("handleFemaleImageClick");
        mMaleImageView.setVisibility(View.GONE);
        mSexProfileEditor.setHint(R.string.verify_sex_rechoose);
        mIsMale = false;
        chooseProfileFromGallery();
    }

    private void handleFemaleImageLongClick() {
        mMaleImageView.setVisibility(View.VISIBLE);
        mSexProfileEditor.setHint(R.string.verify_sex_profile);
        mFemaleImageView.setImageResource(R.mipmap.female);
        mIsProfileDone = false;
        PLog.log("handleFemaleImageLongClick");
    }

    private void handleRegisterClick() {
        mDialog = Utils.newFullScreenProgressDialog(mContext);
        PLog.log("handleRegisterClick");
        //第一步，验证验证码，用户名，头像是否选择
        final String code = mVerifyCodeEditor.getText().toString();
        final String name = mUserNameEditor.getText().toString();
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showToast(mContext, getString(R.string.verify_please_input_code));
            return;
        }
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showToast(mContext, getString(R.string.verify_please_input_name));
            return;
        }
        if (!mIsProfileDone) {
            ToastUtils.showToast(mContext, getString(R.string.verify_please_choose_profile));
            return;
        }

        //第二步，验证用户名是否被使用
        //为什么不验证验证码是否正确是因为，一旦验证正确，这时候验证码就是失效。
        // 但是用户名被使用是不允许的。
        mDialog.show();
//        if (mSmsCodeEntity == null) {
//            PLog.d(TAG, "mSmsEntity is null, error!");
//            ToastUtils.showToast(mContext, "Unknown Error Happened");
//            mDialog.dismiss();
//            return;
//        }
//
//        PLog.log(mSmsCodeEntity);
//        mSmsCodeEntity = new SmsCodeEntity();
//        mSmsCodeEntity.setMobilePhoneNumber("13057538162");
//        PLog.log(JSON.toJSONString(mSmsCodeEntity));
//        User user = new User();
//        user.setMobilePhoneNumber("13057538162");
//        user.setObjectId("1234");
//        user.setUsername("nishisishu");
//        ProfileEntity profileEntity = new ProfileEntity();
//        profileEntity.setType("File");
//        profileEntity.setCdn("helol");
//        profileEntity.setFilename("heanf");
//        profileEntity.setUrl("jada");
//        user.setProfile(profileEntity);
//        PLog.log(JSON.toJSONString(user));


        User user = new User();
        user.setUsername(name);
        RemoteService.getInstance().invoke(
                mActivity,
                API.IS_USER_NAME_USED,
                new RequestParam(null, JSON.toJSONString(user)),
                new AbstractRequestCallback(mContext) {
                    @Override
                    public void onSuccess(String content) {
                        if (!TextUtils.isEmpty(content) &&
                                content.contains(User.USERNAME)) {
                            ToastUtils.showToast(mContext,
                                    getString(R.string.verify_user_name_used));
                            mDialog.dismiss();
                        } else {
                            //第三步，上传用户信息并验证验证码是否正确
                            //registerUserAndUploadProfile();
                            uploadProfile();
                        }
                    }

                    @Override
                    public void onFail(String errorMessage) {
                        super.onFail(errorMessage);
                        mDialog.dismiss();
                    }
                }
        );

        //第四步，注册用户(手机号，密码，用户名)，上传头像

        //第五步，更新服务器用户头像地址URL

        //第六步，将用户信息保存在本地
    }

    //第三步，验证验证码是否正确
    private void verifySmsCode() {
        RequestParam param = new RequestParam(JSON.toJSONString(mSmsCodeEntity));
        param.setObjectId(mVerifyCodeEditor.getText().toString());
        RemoteService.getInstance().invoke(
                mActivity,
                API.VERIFY_SMS_CODE,
                param,
                new AbstractRequestCallback(mContext) {
                    @Override
                    public void onSuccess(String content) {
                        super.onSuccess(content);
                        SmsCodeEntity entity = JSON.parseObject(content, SmsCodeEntity.class);
                        if (entity != null && TextUtils.equals(BmobConstants.OK, entity.getMsg())) {
                            //第四步，注册用户到系统中
                            registerUserAndUploadProfile();
                        } else {
                            PLog.log("verify sms code error, onsuccess but body is not ok");
                            mDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFail(String errorMessage) {
                        super.onFail(errorMessage);
                        mDialog.dismiss();
                    }
                }
        );
    }

    //第三步，注册用户(手机号，密码，用户名)
    private void registerUserAndUploadProfile() {
        Log.d(TAG, "registerUserAndUploadProfile: step 4");
        final User user = new User();
        user.setUsername(mUserNameEditor.getText().toString());
        user.setMobilePhoneNumber(mSmsCodeEntity.getMobilePhoneNumber());
        user.setSmsCode(mVerifyCodeEditor.getText().toString());
        user.setPassword(mPassword);

        RequestParam param = new RequestParam(JSON.toJSONString(user));
        RemoteService.getInstance().invoke(
                mActivity,
                API.REGISTER,
                param,
                new AbstractRequestCallback(mContext) {
                    @Override
                    public void onSuccess(String content) {
                        super.onSuccess(content);
                        Log.d(TAG, "onSuccess: content = " + content);
                        User resultUser = JSON.parseObject(content, User.class);
                        if (resultUser != null) {
                            //保存用户信息
                            Log.d(TAG, "onSuccess: step 5");
                            storeUserInfo(resultUser);
                            //第五步，上传头像
                            uploadProfile();
                        } else {
                            ToastUtils.showToast(mContext,
                                    getString(R.string.register_failed));
                            mDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFail(String errorMessage) {
                        super.onFail(errorMessage);
                        mDialog.dismiss();
                    }
                }
        );
    }

    //保存用户信息
    private void storeUserInfo(User user) {
        //store token
        String token = user.getSessionToken();
        PLog.d(TAG, token);
        PreferencesUtils.putString(mContext,
                BmobConstants.PREF_USER, BmobConstants.PREF_KEY_USER, token);

        //store user
        String userString = JSON.toJSONString(user);
        PLog.d(TAG, userString);
        PreferencesUtils.putString(mContext,
                BmobConstants.PREF_USER, BmobConstants.PREF_KEY_USER, userString);
    }

    //第五步，上传头像
    //TODO 压缩用户头像，然后再上传
    private void uploadProfile() {
        //1、上传头像文件到服务器，并处理返回的头像json数据
        //2、将头像实体关联到服务器对应用户的数据中
        Log.d(TAG, "uploadProfile: step 5");
        RequestParam param = new RequestParam(mProfilePath.substring(
                mProfilePath.lastIndexOf('/') + 1), mProfilePath);
        RemoteService.getInstance().invoke(
                mActivity,
                API.UPLOAD_JPG_IMAGE,
                param,
                new AbstractRequestCallback(mContext) {
                    @Override
                    public void onSuccess(String content) {
                        super.onSuccess(content);
                        Log.d(TAG, "onSuccess: content step 5 = " + content);
                        ProfileEntity entity = JSON.parseObject(content, ProfileEntity.class);
                        Log.d(TAG, "onSuccess: entity = " + entity);
                    }

                    @Override
                    public void onFail(String errorMessage) {
                        super.onFail(errorMessage);
                        mDialog.dismiss();
                    }
                }
        );

    }

    private void handleRegisterAlreadyClick() {
        PLog.log("handleRegisterAlreadyClick");
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void chooseProfileFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }
}
