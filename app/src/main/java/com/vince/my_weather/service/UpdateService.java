package com.vince.my_weather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.vince.my_weather.R;
import com.vince.my_weather.WeatherAcitivity;
import com.vince.my_weather.db.Forecast;
import com.vince.my_weather.db.Weather;
import com.vince.my_weather.util.HttpUtil;
import com.vince.my_weather.util.Utility;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UpdateService extends Service {
    private SharedPreferences spf;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public int flag;//计数变量
    public Notification notification;

    public mBinder mBinder = new mBinder();


    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("me", "解绑");//要看到前台
        startForeground(1,notification);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d("me", "重新绑定");
        stopForeground(true);
        super.onRebind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("me", "初次绑定");
        stopForeground(true);
        return mBinder;
    }

    public class mBinder extends Binder {

    }




    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        spf = PreferenceManager.getDefaultSharedPreferences(this);
        updatePicture();
        updateWeatherInfo();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int time = 8 * 60 * 60 * 1000;//设置更新时间为8小时
        long triggerAtime = SystemClock.elapsedRealtime() + time;

        Intent i = new Intent(getApplicationContext(), UpdateService.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, i, 0);

        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtime, pi);
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
                    Intent intent = new Intent(UpdateService.this, WeatherAcitivity.class);
                    PendingIntent pi = PendingIntent.getActivity(UpdateService.this, 0, intent, 0);
                    RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
                    remoteViews.setTextViewText(R.id.notification_now_ptime, weather1.basic.update.updateTime.split(" ")[1]+"发布");
                    remoteViews.setTextViewText(R.id.notification_now_city, weather1.basic.cityName);
                    remoteViews.setTextViewText(R.id.notification_now_tmp, weather1.now.temperature + "°C");
                    remoteViews.setTextViewText(R.id.notification_now_info, weather1.now.weatherMessage.info);
                    remoteViews.setTextViewText(R.id.notification_now_aqi, weather1.aqi.city.qlty);
                    flag = 0;
                    try {
                        for (Forecast forecast : weather1.forecastList) {
                            if (forecast.date.equals(sdf.format(new Date()))) {
                                remoteViews.setTextViewText(R.id.notification_now_max, forecast.temperature.max + "°C");
                                remoteViews.setTextViewText(R.id.notification_now_min, " "+forecast.temperature.min + "°C");
                            }
                            if (flag == 1) {
                                remoteViews.setTextViewText(R.id.notification_forecast_date, forecast.date.split("-")[1]+"-"+forecast.date.split("-")[2]);
                                remoteViews.setTextViewText(R.id.notification_forecast_max, forecast.temperature.max+ "°C");
                                remoteViews.setTextViewText(R.id.notification_forecast_min, forecast.temperature.min+ "°C");
                                remoteViews.setTextViewText(R.id.notification_forecast_info, forecast.weatherMessage.info);
                                remoteViews.setImageViewBitmap(R.id.notification_forecast_png, Glide.with(getApplicationContext())
                                        .load("http://files.heweather.com/cond_icon/" + forecast.weatherMessage.png + ".png")
                                        .asBitmap().into(40, 40).get());
                            } else if (flag == 2) {
                                remoteViews.setTextViewText(R.id.notification_forecast_date1, forecast.date.split("-")[1]+"-"+forecast.date.split("-")[2]);
                                remoteViews.setTextViewText(R.id.notification_forecast_max1, forecast.temperature.max+ "°C");
                                remoteViews.setTextViewText(R.id.notification_forecast_min1, forecast.temperature.min+ "°C");
                                remoteViews.setTextViewText(R.id.notification_forecast_info1, forecast.weatherMessage.info);
                                remoteViews.setImageViewBitmap(R.id.notification_forecast_png1, Glide.with(getApplicationContext())
                                        .load("http://files.heweather.com/cond_icon/" + forecast.weatherMessage.png + ".png")
                                        .asBitmap().into(40, 40).get());
                            } else if (flag == 3) {
                                remoteViews.setTextViewText(R.id.notification_forecast_date2, forecast.date.split("-")[1]+"-"+forecast.date.split("-")[2]);
                                remoteViews.setTextViewText(R.id.notification_forecast_max2, forecast.temperature.max+ "°C");
                                remoteViews.setTextViewText(R.id.notification_forecast_min2, forecast.temperature.min+ "°C");
                                remoteViews.setTextViewText(R.id.notification_forecast_info2, forecast.weatherMessage.info);
                                remoteViews.setImageViewBitmap(R.id.notification_forecast_png2, Glide.with(getApplicationContext())
                                        .load("http://files.heweather.com/cond_icon/" + forecast.weatherMessage.png + ".png")
                                        .asBitmap().into(40, 40).get());
                            }
                            flag++;
                        }
                        remoteViews.setImageViewBitmap(R.id.notification_now_png,Glide.with(getApplicationContext())
                                .load("http://files.heweather.com/cond_icon/" + weather1.now.weatherMessage.code + ".png")
                                .asBitmap().into(40, 40).get());//加载当天的天气图标

                        notification = new NotificationCompat.Builder(UpdateService.this).setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.drawable.logo)
                                .setLargeIcon(Glide.with(getApplicationContext())
                                        .load("http://files.heweather.com/cond_icon/" + weather1.now.weatherMessage.code + ".png")
                                        .asBitmap().into(40, 40).get())
                                .setContentTitle(weather1.now.temperature + "°C")
                                .setContentText(weather1.basic.cityName)
                                .build();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        notification.bigContentView = remoteViews;//设置大图
                    }
                    startForeground(1, notification);//开启前台天气服务

                    if (weather1 != null && weather1.status.equals("ok")) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(UpdateService.this).edit();
                        editor.putString("weather", mResponse);//获取最新的天气信息，存到本地去
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
                editor.putString("imgCache", pictureText);
                editor.apply();

            }
        });

    }

    @Override
    public void onDestroy() {
        Log.d("me","onDestroy");
        super.onDestroy();
    }

}
