package com.wurq.dex.pluginlibrary.manager;

import android.content.Intent;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by wurongqiu on 17/2/23.
 */
public class DIntent  extends Intent {

    private String mPluginPackage;
    private String mPluginClass;

    public DIntent() {
        super();
    }

    public DIntent(String pluginPackage) {
        super();
        this.mPluginPackage = pluginPackage;
    }

    public DIntent(String pluginPackage, String pluginClass) {
        super();
        this.mPluginPackage = pluginPackage;
        this.mPluginClass = pluginClass;
    }

    public DIntent(String pluginPackage, Class<?> clazz) {
        super();
        this.mPluginPackage = pluginPackage;
        this.mPluginClass = clazz.getName();
    }

    public String getPluginPackage() {
        return mPluginPackage;
    }

    public void setPluginPackage(String pluginPackage) {
        this.mPluginPackage = pluginPackage;
    }

    public String getPluginClass() {
        return mPluginClass;
    }

    public void setPluginClass(String pluginClass) {
        this.mPluginClass = pluginClass;
    }

    public void setPluginClass(Class<?> clazz) {
        this.mPluginClass = clazz.getName();
    }

    @Override
    public Intent putExtra(String name, Parcelable value) {
        setupExtraClassLoader(value);
        return super.putExtra(name, value);
    }

    @Override
    public Intent putExtra(String name, Serializable value) {
        setupExtraClassLoader(value);
        return super.putExtra(name, value);
    }

    private void setupExtraClassLoader(Object value) {
        ClassLoader pluginLoader = value.getClass().getClassLoader();
//        Configs.sPluginClassloader = pluginLoader;
        setExtrasClassLoader(pluginLoader);
    }
}
