package com.example.RecordCall;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        TabHost tabHost = getTabHost();

        TabHost.TabSpec specRecord = tabHost.newTabSpec("record");
        View view1 = LayoutInflater.from(this).inflate(R.layout.tab_record, null);
        specRecord.setIndicator(view1);
        specRecord.setContent(new Intent().setClass(this, ListRecordActivity.class));
        tabHost.addTab(specRecord);

        TabHost.TabSpec specSetting = tabHost.newTabSpec("setting");
        View view2 = LayoutInflater.from(this).inflate(R.layout.tab_setting, null);
        specSetting.setIndicator(view2);
        specSetting.setContent(new Intent().setClass(this, SettingActivity.class));
        tabHost.addTab(specSetting);
        tabHost.setCurrentTab(0);
    }
}
