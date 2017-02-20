package com.vince.my_weather.db;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/2/20.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort{
        @SerializedName("txt")
        public String ComfortSuggestion;
    }

    public class CarWash{
        @SerializedName("txt")
        public String CarWashSuggestion;
    }
    public class Sport{
        @SerializedName("txt")
        public String SportSuggestion;
    }
}
