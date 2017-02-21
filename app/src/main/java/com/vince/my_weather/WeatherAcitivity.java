package com.vince.my_weather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        weatherCode = getIntent().getStringExtra("weatherCode");

        String address = "https://free-api.heweather.com/v5/weather?city=" + weatherCode + "&key=bc0418b57b2d4918819d3974ac1285d9";
        queryFormServer(address);

    }

    public void showWeatherInfo(Weather weather) {//显示天气信息
        title_city.setText(weather.basic.cityName);
        update_time.setText(weather.basic.update.updateTime.split(" ")[1]);
        tmp_text.setText(weather.now.temperature+"°C");
        weather_info_text.setText(weather.now.weatherMessage.info);
        if (weather.aqi != null) {//有的城市天气没有AQI指数
            aqi_text.setText(weather.aqi.city.aqi);
            pm25_text.setText(weather.aqi.city.pm25);
        }

        comfort_text.setText("舒适度："+weather.suggestion.comfort.ComfortSuggestion);
        carwash_text.setText("洗车建议："+weather.suggestion.carWash.CarWashSuggestion);
        sport_text.setText("运动建议："+weather.suggestion.sport.SportSuggestion);

        forecast_layout.removeAllViews();//先把所有的子项清除
        for (Forecast forecast:weather.forecastList) {
            //这里的forecast_layout是指forecast.xml文件里的LinearLayout布局
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecast_layout,false);//加载子项布局
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

    public void queryFormServer(String address) {
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String weatherContent = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(weatherContent);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeatherInfo(weather);
                    }
                });
            }
        });
    }

}
