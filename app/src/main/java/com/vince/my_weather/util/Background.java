package com.vince.my_weather.util;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Administrator on 2017/3/18.
 */

public interface Background {
    @GET("{address}")
    Call<ResponseBody> getPicture(@Path("address") String address);
}
