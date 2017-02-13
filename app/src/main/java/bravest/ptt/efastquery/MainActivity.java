package bravest.ptt.efastquery;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.ColorRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;

import bravest.ptt.efastquery.view.ESearchFloatButton;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "MainActivity";

    private MainService mMainService;

    private NavigationView mNavigationView;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bindService();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

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
    }

    private void bindService() {
        Intent intent = new Intent(this, MainService.class);
        bindService(intent, mMainConnection, BIND_AUTO_CREATE);
        startService(intent);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        @ColorRes
        int colorId = R.color.nav_default;

        switch (id) {
            case R.id.nav_open:
                showFloatingWindow();
                break;
            case R.id.nav_close:
                hideFloatingWindow();
                break;
            case R.id.nav_home:
                colorId = R.color.nav_home_selector;
                break;
            case R.id.nav_export:
                colorId = R.color.nav_export_selector;
                break;
            case R.id.nav_import:
                colorId = R.color.nav_import_selector;
                break;
            case R.id.nav_favorite_book:
                colorId = R.color.nav_favorite_selector;
                break;
            default:
                break;
        }
        if (Build.VERSION.SDK_INT < 23) {
            mNavigationView.setItemTextColor(this.getResources().getColorStateList(colorId));
        } else {
            mNavigationView.setItemTextColor(this.getResources().getColorStateList(colorId, null));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
}
