package com.wurq.dex.pluginlibrary.plugin;

import com.wurq.dex.pluginlibrary.manager.PluginManager;

/**
 * Created by wurongqiu on 17/3/1.
 */
public interface AttachImp {
    /**
     * @param proxyActivity a instance of Plugin,
     *
     * @param pluginManager DLPluginManager instance, manager the plugins
     */
    public void attach(PluginImp proxyActivity, PluginManager pluginManager);
}
