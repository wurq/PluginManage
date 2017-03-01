package com.wurq.dex.plugina;

import android.os.Bundle;

import com.wurq.dex.pluginlibrary.plugin.BasePluginActivity;

public class MainActivity extends BasePluginActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
