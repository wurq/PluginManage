package com.wurq.dex.pluginlibrary.plugin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.wurq.dex.pluginlibrary.manager.LibConstants;
import com.wurq.dex.pluginlibrary.manager.PluginManager;
import com.wurq.dex.pluginlibrary.manager.PluginPackage;

import java.lang.reflect.Constructor;

/**
 * Created by wurongqiu on 17/3/1.
 */
public class ProxyImp {
    private static final String TAG = "DLProxyImpl";

    private String mClass;
    private String mPackageName;

    private PluginPackage mPluginPackage;
    private PluginManager mPluginManager;

    private AssetManager mAssetManager;
    private Resources mResources;
    private Resources.Theme mTheme;

    private ActivityInfo mActivityInfo;
    private Activity mProxyActivity;
    protected PluginImp mPluginActivity;
    public ClassLoader mPluginClassLoader;

    public ProxyImp(Activity activity) {
        mProxyActivity = activity;
    }

    private void initializeActivityInfo() {
        PackageInfo packageInfo = mPluginPackage.packageInfo;
        if ((packageInfo.activities != null) && (packageInfo.activities.length > 0)) {
            if (mClass == null) {
                mClass = packageInfo.activities[0].name;
            }

            //Finals 修复主题BUG
            int defaultTheme = packageInfo.applicationInfo.theme;
            for (ActivityInfo a : packageInfo.activities) {
                if (a.name.equals(mClass)) {
                    mActivityInfo = a;
                    // Finals ADD 修复主题没有配置的时候插件异常
                    if (mActivityInfo.theme == 0) {
                        if (defaultTheme != 0) {
                            mActivityInfo.theme = defaultTheme;
                        } else {
                            if (Build.VERSION.SDK_INT >= 14) {
                                mActivityInfo.theme = android.R.style.Theme_DeviceDefault;
                            } else {
                                mActivityInfo.theme = android.R.style.Theme;
                            }
                        }
                    }
                }
            }

        }
    }

    private void handleActivityInfo() {
        Log.d(TAG, "handleActivityInfo, theme=" + mActivityInfo.theme);
        if (mActivityInfo.theme > 0) {
            mProxyActivity.setTheme(mActivityInfo.theme);
        }
        Resources.Theme superTheme = mProxyActivity.getTheme();
        mTheme = mResources.newTheme();
        mTheme.setTo(superTheme);
        // Finals适配三星以及部分加载XML出现异常BUG
        try {
            mTheme.applyStyle(mActivityInfo.theme, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: handle mActivityInfo.launchMode here in the future.
    }

    public void onCreate(Intent intent) {

        // set the extra's class loader
        intent.setExtrasClassLoader(LibConstants.class.getClassLoader());

        mPackageName = intent.getStringExtra(LibConstants.EXTRA_PACKAGE);
        mClass = intent.getStringExtra(LibConstants.EXTRA_CLASS);
        Log.d(TAG, "mClass=" + mClass + " mPackageName=" + mPackageName);

        mPluginManager = PluginManager.getInstance(mProxyActivity);
        mPluginPackage = mPluginManager.getPackage(mPackageName);
        mAssetManager = mPluginPackage.assetManager;
        mResources = mPluginPackage.resources;

        initializeActivityInfo();
        handleActivityInfo();
        launchTargetActivity();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    protected void launchTargetActivity() {
        try {
            Class<?> localClass = getClassLoader().loadClass(mClass);
            Constructor<?> localConstructor = localClass.getConstructor(new Class[] {});
            Object instance = localConstructor.newInstance(new Object[] {});
            mPluginActivity = (PluginImp) instance;
            ((AttachImp) mProxyActivity).attach(mPluginActivity, mPluginManager);

            mPluginActivity.attach(mProxyActivity, mPluginPackage);

            Bundle bundle = new Bundle();
            bundle.putInt(LibConstants.FROM, LibConstants.FROM_EXTERNAL);
            mPluginActivity.onCreate(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ClassLoader getClassLoader() {
        return mPluginPackage.classLoader;
    }

    public AssetManager getAssets() {
        return mAssetManager;
    }

    public Resources getResources() {
        return mResources;
    }

    public Resources.Theme getTheme() {
        return mTheme;
    }

    public PluginImp getRemoteActivity() {
        return mPluginActivity;
    }
}
