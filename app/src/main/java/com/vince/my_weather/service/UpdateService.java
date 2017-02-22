package com.vince.my_weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.vince.my_weather.db.Weather;
import com.vince.my_weather.util.HttpUtil;
import com.vince.my_weather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UpdateService extends Service {
    private SharedPreferences spf;

    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        spf = PreferenceManager.getDefaultSharedPreferences(this);
        updatePicture();
        updateWeatherInfo();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int time = 8*60*60*1000;//设置更新时间为8小时
        long triggerAtime = SystemClock.elapsedRealtime() + time;

        Intent i = new Intent(UpdateService.this,UpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);

        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    public void updateWeatherInfo() {
        String weatherContent = spf.getString("weather", null);
        if (weatherContent != null) {//记得要判断是否为空
            Weather weather = Utility.handleWeatherResponse(weatherContent);
            String weatherCode = weather.basic.weatherCode;
            String address = "https://free-api.heweather.com/v5/weather?city=" + weatherCode + "&key=bc0418b57b2d4918819d3974ac1285d9";
            HttpUtil.sendOkHttpRequest(address, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String mResponse = response.body().string();
                    Weather weather1 = Utility.handleWeatherResponse(mResponse);
                    if(weather1!=null&&weather1.status.equals("ok")){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(UpdateService.this).edit();
                        editor.putString("weather",mResponse);//获取最新的天气信息，存到本地去
                        editor.apply();
                    }
                }
            });
        }
    }

    public void updatePicture() {
        HttpUtil.sendOkHttpRequest("http://guolin.tech/api/bing_pic", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String pictureText = response.body().string();
                SharedPreferences.Editor editor = spf.edit();
                editor.putString("imgCache",pictureText);
                editor.apply();

            }
        });

    }
}
