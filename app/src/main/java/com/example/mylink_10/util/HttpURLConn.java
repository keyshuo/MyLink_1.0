package com.example.mylink_10.util;

import android.util.Log;

import com.example.mylink_10.pojo.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConn {
    private static StringBuilder response = new StringBuilder();
    private static HttpURLConnection connection;
    private static BufferedReader reader = null;
    private static Gson mGson;

    public static String send(String method, User user, String url) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    //获取HttpURLConnection实例：这时候我们需要new出一个对象，然后传入百度的网络地址，调用openConnect（）方法
                    URL u = new URL(url);
                    connection = (HttpURLConnection) u.openConnection();
                    //需要从服务器获取数据get，提交数据给服务器post
                    connection.setRequestMethod(method);
                    //设置连接超时、读取超时的毫秒数
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    if ("POST".equals(method)) {
                        //待POST的数据
                        String d = "";
                        if (mGson == null) {
                            mGson = new GsonBuilder().disableHtmlEscaping().create();
                        }
                        try {
                            d = mGson.toJson(user);
                        } catch (Exception e) {
                            Log.getStackTraceString(e);
                        }
                        //允许输出
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                        OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                        osw.write(d);
                        osw.flush();
                        osw.close();
                    }
                    //获取服务器返回的输入流
                    InputStream inputStream = connection.getInputStream();
                    //对获取的输入流进行读取
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    reader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //已经获取到了数据，我们需要关闭连接,close()是用来释放连接所占用的资源
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
        return response.toString();
    }
}
