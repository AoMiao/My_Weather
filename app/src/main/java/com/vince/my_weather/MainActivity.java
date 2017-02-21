package com.vince.my_weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vince.my_weather.db.Weather;
import com.vince.my_weather.util.Utility;

import org.litepal.tablemanager.Connector;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCache = spf.getString("weather", null);//获取缓存信息
        if (weatherCache != null) {//有缓存数据直接跳转天气界面
            Intent intent = new Intent(MainActivity.this,WeatherAcitivity.class);
            startActivity(intent);
            finish();
        }
    }
}
