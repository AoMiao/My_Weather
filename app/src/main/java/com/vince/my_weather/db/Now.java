package com.vince.my_weather.db;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/2/20.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public WeatherMessage weatherMessage;

    public class WeatherMessage{
        @SerializedName("txt")
        public String info;
        public String code;
    }
}
