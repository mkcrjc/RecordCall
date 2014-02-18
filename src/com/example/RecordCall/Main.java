package com.example.RecordCall;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

/**
 * Created with IntelliJ IDEA.
 * User: Trung
 * Date: 2/18/14
 * Time: 12:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class Main extends TabActivity
{

    public static final int MEDIA_MOUNTED = 0;
    public static final int MEDIA_MOUNTED_READ_ONLY = 1;
    public static final int NO_MEDIA = 2;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        TabHost tabHost = getTabHost();

        TabHost.TabSpec specRecord = tabHost.newTabSpec("record");
        View view1 = LayoutInflater.from(this).inflate(R.layout.tab_record, null);
        final ImageView ivRecord = (ImageView)view1.findViewById(R.id.ivIcon);
        specRecord.setIndicator(view1);
        specRecord.setContent(new Intent().setClass(this, ListRecordActivity.class));
        tabHost.addTab(specRecord);

        TabHost.TabSpec specSetting = tabHost.newTabSpec("setting");
        View view2 = LayoutInflater.from(this).inflate(R.layout.tab_setting, null);
        final ImageView ivSetting = (ImageView)view2.findViewById(R.id.ivIconSetting);
        specSetting.setIndicator(view2);
        specSetting.setContent(new Intent().setClass(this, SettingActivity.class));
        tabHost.addTab(specSetting);
        tabHost.setCurrentTab(0);
        ivRecord.setImageResource(R.drawable.record_selector);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener()
        {
            @Override
            public void onTabChanged(String tabId)
            {
                if(tabId.equals("record"))
                {
                    ivRecord.setImageResource(R.drawable.record_selector);
                    ivSetting.setImageResource(R.drawable.setting);
                }else if(tabId.equals("setting"))
                {
                    ivSetting.setImageResource(R.drawable.setting_selctor);
                    ivRecord.setImageResource(R.drawable.record);
                }
            }
        });
    }

    public static int updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return MEDIA_MOUNTED;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return MEDIA_MOUNTED_READ_ONLY;
        } else {
            return NO_MEDIA;
        }

    }
}
