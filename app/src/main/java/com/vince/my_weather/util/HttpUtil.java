package com.vince.my_weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/2/18.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);//enqueue里面封装了子线程以及调用callback
    }

    public static void retrofit(String address, Callback<ResponseBody> callback){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(address)
                .build();
        Background background = retrofit.create(Background.class);
        Call<ResponseBody> call = background.getPicture("bing_pic");
        call.enqueue(callback);
    }
}
