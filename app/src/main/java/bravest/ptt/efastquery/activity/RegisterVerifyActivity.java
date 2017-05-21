package bravest.ptt.efastquery.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.w3c.dom.Text;

import java.net.URI;
import java.net.URL;

import bravest.ptt.androidlib.activity.BaseActivity;
import bravest.ptt.androidlib.utils.DialogUtils;
import bravest.ptt.androidlib.utils.ToastUtils;
import bravest.ptt.androidlib.utils.plog.PLog;
import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.entity.SmsCodeEntity;
import bravest.ptt.efastquery.entity.User;
import bravest.ptt.efastquery.utils.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterVerifyActivity extends BaseActivity {

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
                String uri = data.getStringExtra(PROFILE_URL);
                PLog.log("uri = " + uri);
                mIsProfileDone = true;
                if (mIsMale) {
                    mMaleImageView.setImageBitmap(
                            Utils.getBitmapFromUri(mContext, Uri.parse(uri)));
                } else {
                    mFemaleImageView.setImageBitmap(
                            Utils.getBitmapFromUri(mContext, Uri.parse(uri)));
                }

//              使用Glide会导致有图片缓存,why?
//                Glide.with(mActivity)
//                        .load(Uri.parse(uri))
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .into(mMaleImageView);
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
        ProgressDialog dialog = Utils.newFullScreenProgressDialog(mContext);
        PLog.log("handleRegisterClick");
        //第一步，验证验证码，用户名，头像是否选择
        String code = mVerifyCodeEditor.getText().toString();
        String name = mUserNameEditor.getText().toString();
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

        //第二步，验证验证码是否正确，用户名是否有人已经使用
        dialog.show();


        //第三步，验证码用户名正确，注册用户(手机号，密码，用户名)，上传头像

        //第四步，更新用户头像地址URL
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
