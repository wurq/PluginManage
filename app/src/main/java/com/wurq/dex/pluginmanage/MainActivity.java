package com.wurq.dex.pluginmanage;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wurq.dex.pluginlibrary.UTIL.Util;
import com.wurq.dex.pluginlibrary.manager.DIntent;
import com.wurq.dex.pluginlibrary.manager.PluginManager;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<PluginItem> mPluginItems = new ArrayList<PluginItem>();
    private PluginAdapter mPluginAdapter;

    private ListView mListView;
    private TextView mNoPluginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
//        mPluginAdapter = new PluginAdapter();
//        mListView = (ListView) findViewById(R.id.plugin_list);
//        mNoPluginTextView = (TextView)findViewById(R.id.noplugin);

        Button btn= (Button) findViewById(R.id.select_plugin);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
            }
        });

//        Log.d("codecraeer", "getFilesDir = " + getFilesDir());
//        Log.i("codecraeer", "getExternalFilesDir = " + getExternalFilesDir("exter_test").getAbsolutePath());
//        Log.i("codecraeer", "getDownloadCacheDirectory = " + Environment.getDownloadCacheDirectory().getAbsolutePath());
//        Log.i("codecraeer", "getDataDirectory = " + Environment.getDataDirectory().getAbsolutePath());
//        Log.i("codecraeer", "getExternalStorageDirectory = " + Environment.getExternalStorageDirectory().getAbsolutePath());
//        Log.i("codecraeer", "getExternalStoragePublicDirectory = " + Environment.getExternalStoragePublicDirectory("pub_test"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            PluginItem item = new PluginItem();
//            File plugin = new File(uri);
            item.pluginPath = uri.toString();//plugin.getAbsolutePath();
            item.packageInfo = Util.getPackageInfo(this, item.pluginPath);
            if (item.packageInfo.activities != null && item.packageInfo.activities.length > 0)
            {
                item.launcherActivityName = item.packageInfo.activities[0].name;
            }

        }
    }

    private void initData() {

        boolean b = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
//        String pluginFolder = Environment.getExternalStorageDirectory() + "/host";
//
//        PackageInfo packageInfo = Util.getPackageInfo(this,
//                Environment.getExternalStorageDirectory() + "/host/plugina-debug-unaligned.apk");
        String pluginFolder = "file:///storage/emulated/0";//Environment.getExternalStorageDirectory().getAbsolutePath() ;
        File file = new File(Environment.getExternalStorageDirectory().getPath());
        if(file.isDirectory()) {
            int i=0;
        }
        File[] plugins = file.listFiles();
        if (plugins == null || plugins.length == 0) {
            mNoPluginTextView.setVisibility(View.VISIBLE);
            return;
        }
//        file:///storage/emulated/0
        for (File plugin : plugins) {
            PluginItem item = new PluginItem();
            item.pluginPath = plugin.getAbsolutePath();
            item.packageInfo = Util.getPackageInfo(this, item.pluginPath);
            if (item.packageInfo.activities != null && item.packageInfo.activities.length > 0)
            {
                item.launcherActivityName = item.packageInfo.activities[0].name;
            }

            mPluginItems.add(item);
        }

        mListView.setAdapter(mPluginAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                PluginItem item = mPluginItems.get(position);
                PluginManager pluginManager = PluginManager.getInstance(MainActivity.this);
                pluginManager.startPluginActivity(MainActivity.this, new DIntent(item.packageInfo.packageName));
            }
        });
        mPluginAdapter.notifyDataSetChanged();
    }


    private class PluginAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public PluginAdapter() {
            mInflater = MainActivity.this.getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mPluginItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mPluginItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.plugin_item, parent, false);
                holder = new ViewHolder();
                holder.appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
                holder.appName = (TextView) convertView.findViewById(R.id.app_name);
                holder.apkName = (TextView) convertView.findViewById(R.id.apk_name);
                holder.packageName = (TextView) convertView.findViewById(R.id.package_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            PluginItem item = mPluginItems.get(position);
            PackageInfo packageInfo = item.packageInfo;
            holder.appIcon.setImageDrawable(Util.getAppIcon(MainActivity.this, item.pluginPath));
            holder.appName.setText(Util.getAppLabel(MainActivity.this, item.pluginPath));
            holder.apkName.setText(item.pluginPath.substring(item.pluginPath.lastIndexOf(File.separatorChar) + 1));
            holder.packageName.setText(packageInfo.applicationInfo.packageName + "\n" + item.launcherActivityName + "\n" );
            return convertView;
        }
    }

    private static class ViewHolder {
        public ImageView appIcon;
        public TextView appName;
        public TextView apkName;
        public TextView packageName;
    }

    public static class PluginItem {
        public PackageInfo packageInfo;
        public String pluginPath;
        public String launcherActivityName;

        public PluginItem() {
        }
    }

}
