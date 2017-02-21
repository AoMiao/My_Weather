package com.vince.my_weather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.vince.my_weather.db.Forecast;
import com.vince.my_weather.db.Weather;
import com.vince.my_weather.util.HttpUtil;
import com.vince.my_weather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/20.
 */

public class WeatherAcitivity extends AppCompatActivity {
    private String weatherCode;
    private View weather_scrollview;

    private TextView title_city;
    private TextView update_time;

    private TextView tmp_text;
    private TextView weather_info_text;

    private LinearLayout forecast_layout;

    private TextView aqi_text;
    private TextView pm25_text;

    private TextView comfort_text;
    private TextView carwash_text;
    private TextView sport_text;

    private ImageView bing_pic_img;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }*/
        if(Build.VERSION.SDK_INT>=21){//android5.0以上把布局充满整个手机屏幕,并把状态栏设置成透明
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.weather_layout);
        weather_scrollview = findViewById(R.id.weather_scrollview);
        title_city = (TextView) findViewById(R.id.title_city);
        update_time = (TextView) findViewById(R.id.update_time);
        tmp_text = (TextView) findViewById(R.id.tmp_text);
        weather_info_text = (TextView) findViewById(R.id.weather_info_text);
        forecast_layout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqi_text = (TextView) findViewById(R.id.aqi_text);
        pm25_text = (TextView) findViewById(R.id.pm25_text);
        comfort_text = (TextView) findViewById(R.id.comfort_text);
        carwash_text = (TextView) findViewById(R.id.carwash_text);
        sport_text = (TextView) findViewById(R.id.sport_text);
        bing_pic_img = (ImageView) findViewById(R.id.bing_pic_img);//每日一图实例
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCache = spf.getString("weather", null);//获取缓存信息
        String imgCache = spf.getString("imgCache",null);
        if (weatherCache != null) {
            Weather weather = Utility.handleWeatherResponse(weatherCache);
            showWeatherInfo(weather);
        }else {
            weatherCode = getIntent().getStringExtra("weatherCode");
            String address = "https://free-api.heweather.com/v5/weather?city=" + weatherCode + "&key=bc0418b57b2d4918819d3974ac1285d9";
            queryFormServer(address);
        }

        if(imgCache!=null){//有缓存的话就直接用缓存载图，没有就去访问网络
            Glide.with(WeatherAcitivity.this).load(imgCache).into(bing_pic_img);
        }else {
            setBackgroundImg();
        }

    }

    public void showWeatherInfo(Weather weather) {//显示天气信息
        title_city.setText(weather.basic.cityName);
        update_time.setText(weather.basic.update.updateTime.split(" ")[1]);
        tmp_text.setText(weather.now.temperature + "°C");
        weather_info_text.setText(weather.now.weatherMessage.info);
        if (weather.aqi != null) {//有的城市天气没有AQI指数
            aqi_text.setText(weather.aqi.city.aqi);
            pm25_text.setText(weather.aqi.city.pm25);
        }

        comfort_text.setText("舒适度：" + weather.suggestion.comfort.ComfortSuggestion);
        carwash_text.setText("洗车建议：" + weather.suggestion.carWash.CarWashSuggestion);
        sport_text.setText("运动建议：" + weather.suggestion.sport.SportSuggestion);

        forecast_layout.removeAllViews();//先把所有的子项清除
        for (Forecast forecast : weather.forecastList) {
            //这里的forecast_layout是指forecast.xml文件里的LinearLayout布局
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecast_layout, false);//加载子项布局
            TextView forecast_date_text = (TextView) view.findViewById(R.id.date_text);
            TextView forecast_info_text = (TextView) view.findViewById(R.id.info_text);
            TextView forecast_max_text = (TextView) view.findViewById(R.id.max_text);
            TextView forecast_min_text = (TextView) view.findViewById(R.id.min_text);
            forecast_date_text.setText(forecast.date);
            forecast_info_text.setText(forecast.weatherMessage.info);
            forecast_max_text.setText(forecast.temperature.max);
            forecast_min_text.setText(forecast.temperature.min);
            forecast_layout.addView(view);
        }
    }

    public void queryFormServer(String address) {//调用的时候都会获得最新的天气信息和每日一图
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(WeatherAcitivity.this,"加载天气失败",Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String weatherContent = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(weatherContent);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherAcitivity.this).edit();
                            editor.putString("weather", weatherContent);//缓存功能
                            editor.apply();
                            showWeatherInfo(weather);
                        }
                    }
                });
            }
        });
        setBackgroundImg();
    }

    public void setBackgroundImg(){
        HttpUtil.sendOkHttpRequest("http://guolin.tech/api/bing_pic", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String imgCache = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherAcitivity.this).edit();
                editor.putString("imgCache", imgCache);//缓存功能
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {//记得回到主线才能修改UI
                        Glide.with(WeatherAcitivity.this).load(imgCache).into(bing_pic_img);
                    }
                });
            }
        });
    }
}
