package bravest.ptt.efastquery;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import bravest.ptt.efastquery.data.wordbook.DocBuilder;
import bravest.ptt.efastquery.data.wordbook.WordBook;
import bravest.ptt.efastquery.data.wordbook.XmlBuilder;
import bravest.ptt.efastquery.data.wordbook.XmlParser;
import bravest.ptt.efastquery.fragment.BaseFragment;
import bravest.ptt.efastquery.fragment.ExportFragment;
import bravest.ptt.efastquery.fragment.FavoriteFragment;
import bravest.ptt.efastquery.fragment.FileManagerFragment;
import bravest.ptt.efastquery.fragment.ImportFragment;
import bravest.ptt.efastquery.fragment.MainFragment;
import bravest.ptt.efastquery.provider.EFastQueryDbUtils;
import bravest.ptt.efastquery.provider.FavoriteManager;
import bravest.ptt.efastquery.utils.PLog;
import bravest.ptt.efastquery.utils.Utils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "MainActivity";

    public static final int REQUEST_CODE = 100;

    private MainService mMainService;

    private NavigationView mNavigationView;
    private Toolbar mToolbar;

    private FragmentManager mFragmentManager;
    private HashMap<Integer, BaseFragment> mFragmentMap;
    private MainFragment mMainFragment;
    private ExportFragment mExportFragment;
    private ImportFragment mImportFragment;
    private FavoriteFragment mFavoriteFragment;
    private FileManagerFragment mFileManagerFragment;

    private BaseFragment mCurrentFragment;

    private Context mContext;

    private String mGroup;

    private ServiceConnection mMainConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected");
            mMainService = ((MainService.MainBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected");
            mMainService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bindService();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //navigation View
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_home);
        mNavigationView.setItemTextColor(this.getResources().getColorStateList(R.color.nav_home_selector));

        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        //Init fragment
        mFragmentManager = getSupportFragmentManager();
        mFragmentMap = new HashMap<>();
        mMainFragment = new MainFragment();
        //mFileManagerFragment = new FileManagerFragment();
        mExportFragment = new ExportFragment();
        mImportFragment = new ImportFragment();
        mFavoriteFragment = new FavoriteFragment();
        mFragmentMap.put(R.id.nav_home, mMainFragment);
        mFragmentMap.put(R.id.nav_export, mExportFragment);
        mFragmentMap.put(R.id.nav_import, mImportFragment);
        mFragmentMap.put(R.id.nav_favorite_book, mFavoriteFragment);

        //set default fragment
        initFragments(R.id.nav_home);

        //CheckPermission
        checkPermissions();

        /*
        //Creates a ColorStateList from an XML document using given a set of Resources
        //and a Resource.Theme.

        //The resource defined in 'color' subdirectory which in resource directory.

        //If you want show original pictures color, you can use 'null' as the param for setItemIconTintList

        ColorStateList colorStateList = null;
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            if (Build.VERSION.SDK_INT < 23) {
                colorStateList = ColorStateList.createFromXml(getResources(),parser);
            } else {
                colorStateList = ColorStateList.createFromXml(getResources(),parser,null);
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        mNavigationView.setItemIconTintList(null);

        mGroup = getSelectGroup();
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean allGranted = Utils.requestPermissions(this, REQUEST_CODE, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            });

            if (allGranted) {
//                XmlBuilder xmlBuilder = XmlBuilder.getInstance();
//                ArrayList<WordBook> data = new ArrayList<>();
//                xmlBuilder.domCreateXML(data,null);
            }
            return allGranted;
        }
        return true;
    }

    private void initFragments(int id) {
        BaseFragment current = mFragmentMap.get(id);
        BaseFragment old = mCurrentFragment;
        if (current == null) {
            return;
        }

        //One fragment transaction @method commit can not be used twice, it will cause "commit already called".
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.content_view, current);
        mCurrentFragment = current;
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.getHexagonPoints(163.25f, 37f, 126.25f);
    }

    private void bindService() {
        Intent intent = new Intent(this, MainService.class);
        bindService(intent, mMainConnection, BIND_AUTO_CREATE);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != 100) return;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                continue;
            }
            if (TextUtils.equals(permissions[i], Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || TextUtils.equals(permissions[i], Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.card_close:
            case R.id.card_close_tv:
                hideFloatingWindow();
                break;
            case R.id.card_open:
            case R.id.card_open_tv:
                showFloatingWindow();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mCurrentFragment instanceof MainFragment) {
            menu.findItem(R.id.action_settings).setVisible(false);
            menu.findItem(R.id.action_search).setVisible(true);
            menu.findItem(R.id.action_group).setVisible(false);
        } else if (mCurrentFragment instanceof ExportFragment) {
            menu.findItem(R.id.action_settings).setVisible(false);
            menu.findItem(R.id.action_search).setVisible(false);
            menu.findItem(R.id.action_group).setVisible(true);
        } else if (mCurrentFragment instanceof ImportFragment) {
            menu.findItem(R.id.action_settings).setVisible(false);
            menu.findItem(R.id.action_search).setVisible(false);
            menu.findItem(R.id.action_group).setVisible(true);
        } else if (mCurrentFragment instanceof FavoriteFragment) {
            menu.findItem(R.id.action_settings).setVisible(true);
            menu.findItem(R.id.action_search).setVisible(true);
            menu.findItem(R.id.action_group).setVisible(false);
        }
//        SearchView view = (SearchView) MenuItemCompat.getActionView(item);
//        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener(){
//
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem menuItem) {
//                return true;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
//                return true;
//            }
//        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_group:
                createGroupSelectDialog();
                break;
            case R.id.action_search:
                break;
            case R.id.action_settings:
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getSelectGroup() {
        if (mGroup == null) {
            mGroup = this.getString(R.string.group_default);
        }
        return mGroup;
    }

    //Show groups
    private void createGroupSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (mCurrentFragment instanceof ExportFragment) {
            builder.setTitle(R.string.export_select_group);
        } else if (mCurrentFragment instanceof ImportFragment) {
            builder.setTitle(R.string.import_select_group)
                    .setNeutralButton(R.string.file_manager_create_group, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Create new group
                            addGroupDialog();
                        }
                    });
        }

        final String[] groups = FavoriteManager.getGroup(this);
        final String[] chooseString = new String[1];

        int i;
        for (i = 0; i < groups.length; i++) {
            String group = groups[i];
            if (TextUtils.equals(mGroup, group)) {
                break;
            }
        }

        builder.setSingleChoiceItems(groups, i, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                chooseString[0] = groups[i];
            }
        }).setPositiveButton(R.string.file_manager_select_group, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mGroup = chooseString[0];
                //[FIX BUG] when user don't click item and directly click select,
                // it will cause NULL POINTER EXCEPTION
                if (mGroup == null) {
                    mGroup = mContext.getString(R.string.group_default);
                }
                PLog.log(mGroup);
            }
        }).setNegativeButton(R.string.file_manager_cancel, null)
                .show();
    }

    public void addGroupDialog() {
        final int DP_MARGIN = 16;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LinearLayout layout = new LinearLayout(this);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setOrientation(LinearLayout.VERTICAL);
        params.setMargins(Utils.dp2px(this, DP_MARGIN), 0, Utils.dp2px(this, DP_MARGIN), 0);
        final EditText editText = new EditText(this);
        layout.addView(editText, params);

        builder
                .setTitle(R.string.file_manager_create_group)
                .setView(layout)
                .setPositiveButton(R.string.file_manager_create_group, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FavoriteManager.createGroup(mContext, editText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.file_manager_cancel, null)
                .show();
        Utils.popSoftInput(mContext, editText);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        initFragments(id);
        invalidateOptionsMenu();
        switch (id) {
            case R.id.nav_open:
                showFloatingWindow();
                break;
            case R.id.nav_close:
                hideFloatingWindow();
                break;
            case R.id.nav_night_mode:
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                recreate();
                break;
            case R.id.nav_home:
                setStatusBarToolBarHeader(
                        R.string.home,
                        R.color.home_red_500_4_toolbar,
                        R.color.home_red_600_4_status_bar,
                        R.color.nav_home_selector);
                break;
            case R.id.nav_export:
                if (!checkPermissions()) {
                    mNavigationView.setCheckedItem(R.id.nav_home);
                    break;
                }
                setStatusBarToolBarHeader(
                        R.string.export_file,
                        R.color.export_orange_500_4_toolbar,
                        R.color.export_orange_600_4_status_bar,
                        R.color.nav_export_selector);
                break;
            case R.id.nav_import:
                if (!checkPermissions()) {
                    mNavigationView.setCheckedItem(R.id.nav_home);
                    break;
                }
                setStatusBarToolBarHeader(
                        R.string.import_file,
                        R.color.import_blue_500_4_toolbar,
                        R.color.import_blue_600_4_status_bar,
                        R.color.nav_import_selector);
                break;
            case R.id.nav_favorite_book:
                setStatusBarToolBarHeader(
                        R.string.favorite_book,
                        R.color.favorite_purple_500_4_toolbar,
                        R.color.favorite_purple_600_4_status_bar,
                        R.color.nav_favorite_selector);
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void back2Home() {
        mNavigationView.setCheckedItem(R.id.nav_home);
        initFragments(R.id.nav_home);
        invalidateOptionsMenu();
        setStatusBarToolBarHeader(
                R.string.home,
                R.color.home_red_500_4_toolbar,
                R.color.home_red_600_4_status_bar,
                R.color.nav_home_selector);
    }

    private void setStatusBarToolBarHeader(int titleString, int mainColor, int statusBarColor, int itemColor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        //Toolbar
        mToolbar.setTitle(getString(titleString));
        mToolbar.setBackgroundColor(getResources().getColor(mainColor));
        //Status bar color
        Utils.setWindowStatusBarColor(this, statusBarColor);
        //Head color
        mNavigationView.getHeaderView(0).setBackgroundColor(getResources().getColor(mainColor));
        //navigation item color
        if (Build.VERSION.SDK_INT < 23) {
            mNavigationView.setItemTextColor(this.getResources().getColorStateList(itemColor));
        } else {
            mNavigationView.setItemTextColor(this.getResources().getColorStateList(itemColor, null));
        }
    }

    private void showFloatingWindow() {
        if (mMainService != null) {
            mMainService.showFloatingWindow();
        }
    }

    private void hideFloatingWindow() {
        if (mMainService != null) {
            mMainService.hideFloatingWindow();
        }
    }

//    private void showFileChooser() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("text/plain");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        try {
//            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file_import)), REQUEST_CODE);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(this, getString(R.string.please_install_file_manager), Toast.LENGTH_SHORT).show();
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode != REQUEST_CODE) {
//            return;
//        }
//        if (resultCode == RESULT_OK) {
//            Uri uri = data.getData();
//            String filePath = "/storage/emulated/0/efastquery/xml/default_1487050240222.xml";
//            if (filePath == null || !filePath.endsWith("xml")) {
//                Toast.makeText(this, getString(R.string.just_support_xml), Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Log.d(TAG, "onActivityResult: filepath = " + filePath);
//
//            ArrayList<WordBook> words = XmlParser.getInstance().parseXml(filePath);
//            Log.d(TAG, "onActivityResult: words size = " + words.size());
//            for (WordBook w : words) {
//                Log.d(TAG, "onActivityResult: word = " + w.getWord() + ", phonetic = " + w.getPhonetic());
//            }
//        }
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mMainConnection);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                if (mCurrentFragment == null) {
                    return super.onKeyDown(keyCode, event);
                }
                if (mCurrentFragment.isRootPanel()) {
                    if (!(mCurrentFragment instanceof MainFragment)) {
                        back2Home();
                        return true;
                    } else {
                        finish();
                    }
                } else {
                    return mCurrentFragment.onKeyDown(keyCode, event);
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
