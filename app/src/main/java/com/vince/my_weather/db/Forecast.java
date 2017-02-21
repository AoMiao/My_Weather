package com.vince.my_weather.db;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/2/20.
 */

public class Forecast {
    public String date;
    @SerializedName("cond")
    public WeatherMessage weatherMessage;
    @SerializedName("tmp")
    public Temperature temperature;

    public class WeatherMessage {
        @SerializedName("txt_d")
        public String info;
    }

    public class Temperature {
        public String max;
        public String min;
    }
}
