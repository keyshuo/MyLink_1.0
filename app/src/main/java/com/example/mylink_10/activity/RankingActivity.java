package com.example.mylink_10.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylink_10.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RankingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        new HttpRequestTask().execute();
        String[] ranking = {"李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",};
        //ListView相关
        ArrayAdapter rankingAdapter = new ArrayAdapter(this,R.layout.item_selector,ranking);
        ListView lv_ranking = findViewById(R.id.lv_ranking);
        lv_ranking.setAdapter(rankingAdapter);
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://1.15.76.132:8080/rank/my/getRankLow");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    Gson mGson = new Gson();
                    Log.d("Ranking",response.toString());
                    return response.toString();
                } else {
                    return "GET request failed. Response Code: " + responseCode;
//                    return "登录失败，请检查账号密码或网络后重试";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // 处理网络请求的结果
            Toast.makeText(RankingActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }
}