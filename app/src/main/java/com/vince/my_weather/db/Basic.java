package com.vince.my_weather.db;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/2/20.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherCode;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
