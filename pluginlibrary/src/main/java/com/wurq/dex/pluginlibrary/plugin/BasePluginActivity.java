package com.wurq.dex.pluginlibrary.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wurq.dex.pluginlibrary.manager.DIntent;
import com.wurq.dex.pluginlibrary.manager.LibConstants;
import com.wurq.dex.pluginlibrary.manager.PluginManager;
import com.wurq.dex.pluginlibrary.manager.PluginPackage;

/**
 * Created by wurongqiu on 17/3/1.
 */
public class BasePluginActivity extends Activity implements PluginImp{

    private static final String TAG = "DLBasePluginActivity";

    /**
     */
    protected Activity mProxyActivity;

    /**
     * 可以当作this来使用
     */
    protected Activity that;
    protected PluginManager mPluginManager;
    protected PluginPackage mPluginPackage;

    protected int mFrom = LibConstants.FROM_INTERNAL;

    @Override
    public void attach(Activity proxyActivity, PluginPackage pluginPackage) {
        Log.d(TAG, " proxyActivity= " + proxyActivity);
        mProxyActivity = (Activity) proxyActivity;
        that = mProxyActivity;
        mPluginPackage = pluginPackage;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mFrom = savedInstanceState.getInt(LibConstants.FROM, LibConstants.FROM_INTERNAL);
        }
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.onCreate(savedInstanceState);
            mProxyActivity = this;
            that = mProxyActivity;
        }

        mPluginManager = PluginManager.getInstance(that);
    }

    @Override
    public void setContentView(View view) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.setContentView(view);
        } else {
            mProxyActivity.setContentView(view);
        }
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.setContentView(view, params);
        } else {
            mProxyActivity.setContentView(view, params);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.setContentView(layoutResID);
        } else {
            mProxyActivity.setContentView(layoutResID);
        }
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.addContentView(view, params);
        } else {
            mProxyActivity.addContentView(view, params);
        }
    }

    @Override
    public View findViewById(int id) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return super.findViewById(id);
        } else {
            return mProxyActivity.findViewById(id);
        }
    }

    @Override
    public Intent getIntent() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return super.getIntent();
        } else {
            return mProxyActivity.getIntent();
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return super.getClassLoader();
        } else {
            return mProxyActivity.getClassLoader();
        }
    }

    @Override
    public Resources getResources() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return super.getResources();
        } else {
            return mProxyActivity.getResources();
        }
    }

    @Override
    public String getPackageName() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return super.getPackageName();
        } else {
            return mPluginPackage.packageName;
        }
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return super.getLayoutInflater();
        } else {
            return mProxyActivity.getLayoutInflater();
        }
    }

    @Override
    public MenuInflater getMenuInflater() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return super.getMenuInflater();
        } else {
            return mProxyActivity.getMenuInflater();
        }
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return super.getSharedPreferences(name, mode);
        } else {
            return mProxyActivity.getSharedPreferences(name, mode);
        }
    }

    @Override
    public Context getApplicationContext() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return super.getApplicationContext();
        } else {
            return mProxyActivity.getApplicationContext();
        }
    }

    @Override
    public WindowManager getWindowManager() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return super.getWindowManager();
        } else {
            return mProxyActivity.getWindowManager();
        }
    }

    @Override
    public Window getWindow() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return super.getWindow();
        } else {
            return mProxyActivity.getWindow();
        }
    }

    @Override
    public Object getSystemService(String name) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return super.getSystemService(name);
        } else {
            return mProxyActivity.getSystemService(name);
        }
    }

    @Override
    public void finish() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.finish();
        } else {
            mProxyActivity.finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.onStart();
        }
    }

    @Override
    public void onRestart() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.onRestart();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.onSaveInstanceState(outState);
        }
    }

    public void onNewIntent(Intent intent) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.onNewIntent(intent);
        }
    }

    @Override
    public void onResume() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.onPause();
        }
    }

    @Override
    public void onStop() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.onStop();
        }
    }

    @Override
    public void onDestroy() {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.onDestroy();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return super.onKeyUp(keyCode, event);
        }
        return false;
    }

    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.onWindowAttributesChanged(params);
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            super.onWindowFocusChanged(hasFocus);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return super.onCreateOptionsMenu(menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (mFrom == LibConstants.FROM_INTERNAL) {
            return onOptionsItemSelected(item);
        }
        return false;
    }

    /**
     * @param dIntent
     */
    public int startPluginActivity(DIntent dIntent) {
        return startPluginActivityForResult(dIntent, -1);
    }

    /**
     * @param dIntent
     */
    public int startPluginActivityForResult(DIntent dIntent, int requestCode) {
        if (mFrom == LibConstants.FROM_EXTERNAL) {
            if (dIntent.getPluginPackage() == null) {
                dIntent.setPluginPackage(mPluginPackage.packageName);
            }
        }
        return mPluginManager.startPluginActivityForResult(that, dIntent, requestCode);
    }

//    public int startPluginService(DIntent dlIntent) {
//        if (mFrom == LibConstants.FROM_EXTERNAL) {
//            if (dlIntent.getPluginPackage() == null) {
//                dlIntent.setPluginPackage(mPluginPackage.packageName);
//            }
//        }
//        return mPluginManager.startPluginService(that, dlIntent);
//    }

//    public int stopPluginService(DIntent dlIntent) {
//        if (mFrom == LibConstants.FROM_EXTERNAL) {
//            if (dlIntent.getPluginPackage() == null) {
//                dlIntent.setPluginPackage(mPluginPackage.packageName);
//            }
//        }
//        return mPluginManager.stopPluginService(that, dlIntent);
//    }

//    public int bindPluginService(DIntent dIntent, ServiceConnection conn, int flags) {
//        if (mFrom == LibConstants.FROM_EXTERNAL) {
//            if (dIntent.getPluginPackage() == null) {
//                dIntent.setPluginPackage(mPluginPackage.packageName);
//            }
//        }
//        return mPluginManager.bindPluginService(that, dIntent, conn, flags);
//    }

//    public int unBindPluginService(DIntent dlIntent, ServiceConnection conn) {
//        if (mFrom == LibConstants.FROM_EXTERNAL) {
//            if (dlIntent.getPluginPackage() == null)
//                dlIntent.setPluginPackage(mPluginPackage.packageName);
//        }
//        return mPluginManager.unBindPluginService(that, dlIntent, conn);
//    }
}
