package com.example.mylink_10.util;

import android.util.Log;

import com.example.mylink_10.pojo.RMethod;
import com.example.mylink_10.pojo.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequestUtil {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS).build();


    /**
     * 发送HTTP请求
     *
     * @param method 请求方法
     * @param user   请求数据
     * @param url    请求地址
     * @return 响应数据
     */
    public String sendHttpRequest(String method, User user, String url) throws IOException {
        String result = null;
        switch (method) {
            case RMethod.SyncGet:
                result = syncGet(url);
            case RMethod.SyncPost:
                result = syncPost(url, user);
        }
        return result;
    }

    private String syncGet(String url) throws IOException {
        String result = null;
        Request request = new Request.Builder().url(url).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            result = response.body().string();
        }
        return result;
    }

    private String syncPost(String url, User user) throws IOException {
        String result = null;
        //    private static String response = "";
        Gson mGson = new Gson();
        String json = mGson.toJson(user);
        Log.d("JSON", json);
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response1 = okHttpClient.newCall(request).execute();
        result = response1.body().string();
        Log.d("OUT", result);
        return result;
    }
}

