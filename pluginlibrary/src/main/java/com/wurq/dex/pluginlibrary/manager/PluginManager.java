package com.wurq.dex.pluginlibrary.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;

import com.wurq.dex.pluginlibrary.plugin.BasePluginActivity;
import com.wurq.dex.pluginlibrary.plugin.ProxyActivity;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

/**
 * Created by wurongqiu on 17/2/21.
 */
public class PluginManager {

    public static final int START_RESULT_SUCCESS = 0;

    public static final int START_RESULT_NO_PKG = 1;

    public static final int START_RESULT_NO_CLASS = 2;

    public static final int START_RESULT_TYPE_ERROR = 3;
    private static PluginManager sInstance;
    private Context mContext;
    private final HashMap<String, PluginPackage> mPackagesHolder = new HashMap<String, PluginPackage>();

    private PluginManager(Context context) {
        mContext = context.getApplicationContext();
//        mNativeLibDir = mContext.getDir("pluginlib", Context.MODE_PRIVATE).getAbsolutePath();
    }

    public static PluginManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (PluginManager.class) {
                if (sInstance == null) {
                    sInstance = new PluginManager(context);
                }
            }
        }

        return sInstance;
    }


    public int startPluginActivity(Context context, DIntent dlIntent) {
        return startPluginActivityForResult(context, dlIntent, -1);
    }

    /**
     * @param context
     * @param dIntent
     * @param requestCode
     * @return One of below: {@link #START_RESULT_SUCCESS}
     *         {@link #START_RESULT_NO_PKG} {@link #START_RESULT_NO_CLASS}
     *         {@link #START_RESULT_TYPE_ERROR}
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public int startPluginActivityForResult(Context context, DIntent dIntent, int requestCode) {
//        if (mFrom == DLConstants.FROM_INTERNAL)
//        {
//            dIntent.setClassName(context, dIntent.getPluginClass());
//            performStartActivityForResult(context, dIntent, requestCode);
//            return PluginManager.START_RESULT_SUCCESS;
//        }

        String packageName = dIntent.getPluginPackage();
        if (TextUtils.isEmpty(packageName)) {
            throw new NullPointerException("disallow null packageName.");
        }

        PluginPackage pluginPackage = mPackagesHolder.get(packageName);
        if (pluginPackage == null) {
            return START_RESULT_NO_PKG;
        }

        final String className = getPluginActivityFullPath(dIntent, pluginPackage);
        Class<?> clazz = loadPluginClass(pluginPackage.classLoader, className);
        if (clazz == null) {
            return START_RESULT_NO_CLASS;
        }

        // get the proxy activity class, the proxy activity will launch the
        // plugin activity.
        Class<? extends Activity> activityClass = getProxyActivityClass(clazz);
        if (activityClass == null) {
            return START_RESULT_TYPE_ERROR;
        }
//
//        // put extra data
        dIntent.putExtra(LibConstants.EXTRA_CLASS, className);
        dIntent.putExtra(LibConstants.EXTRA_PACKAGE, packageName);
        dIntent.setClass(mContext, activityClass);
        performStartActivityForResult(context, dIntent, requestCode);

        return START_RESULT_SUCCESS;
    }


    private void performStartActivityForResult(Context context, DIntent dIntent, int requestCode) {
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(dIntent, requestCode);
        } else {
            context.startActivity(dIntent);
        }
    }

    private Class<?> loadPluginClass(ClassLoader classLoader, String className) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return clazz;
    }

    public PluginPackage getPackage(String packageName) {
        return mPackagesHolder.get(packageName);
    }

    private String getPluginActivityFullPath(DIntent dIntent, PluginPackage pluginPackage) {
        String className = dIntent.getPluginClass();
        className = (className == null ? pluginPackage.defaultActivity : className);
        if (className.startsWith(".")) {
            className = dIntent.getPluginPackage() + className;
        }
        return className;
    }

    public PluginPackage loadApk(String dexPath) {
        // when loadApk is called by host apk, we assume that plugin is invoked by host.
        return loadApk(dexPath, true);
    }

    /**
     * @param dexPath plugin path
     * @return
     */
    public PluginPackage loadApk(final String dexPath,boolean i) {
        
        PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(dexPath,
                PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        if (packageInfo == null) {
            return null;
        }

        PluginPackage pluginPackage = preparePluginEnv(packageInfo, dexPath);

        return pluginPackage;
    }

    public PluginPackage preparePluginEnv(PackageInfo packageInfo, String dexPath) {
        DexClassLoader dexClassLoader = createDexClassLoader(dexPath);
        AssetManager assetManager = createAssetManager(dexPath);
        Resources resources = createResources(assetManager);
        // create pluginPackage
        PluginPackage pluginPackage = new PluginPackage(dexClassLoader, resources, packageInfo);
        mPackagesHolder.put(packageInfo.packageName, pluginPackage);
        return pluginPackage;
    }

    private String dexOutputPath;

    private DexClassLoader createDexClassLoader(String dexPath) {
        File dexOutputDir = mContext.getDir("dex", Context.MODE_PRIVATE);
        dexOutputPath = dexOutputDir.getAbsolutePath();
        DexClassLoader loader = new DexClassLoader(dexPath, dexOutputPath,null, mContext.getClassLoader());
        return loader;
    }

    private AssetManager createAssetManager(String dexPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, dexPath);
            return assetManager;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    private Resources createResources(AssetManager assetManager) {
        Resources superRes = mContext.getResources();
        Resources resources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        return resources;
    }

    /**
     * get the proxy activity class, the proxy activity will delegate the plugin
     * activity
     *
     * @param clazz
     *            target activity's class
     * @return
     */
    private Class<? extends Activity> getProxyActivityClass(Class<?> clazz) {
        Class<? extends Activity> activityClass = null;
        if (BasePluginActivity.class.isAssignableFrom(clazz)) {
            activityClass = ProxyActivity.class;
        } else
        {
//            if (BasePluginFragmentActivity.class.isAssignableFrom(clazz)) {
//                activityClass = DLProxyFragmentActivity.class;
//            }
        }

        return activityClass;
    }

}
