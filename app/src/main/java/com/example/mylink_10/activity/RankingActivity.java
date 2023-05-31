package com.example.mylink_10.activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylink_10.R;
import com.example.mylink_10.pojo.AddressUrl;
import com.example.mylink_10.pojo.RankingResult;
import com.example.mylink_10.pojo.ScorePojo;
import com.example.mylink_10.util.ThemeUtil;
import com.example.mylink_10.util.getValuesUtil;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class RankingActivity extends AppCompatActivity {

    private List<ScorePojo> data;
    private String[] strings = {};
    private ListView lv_ranking;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        new HttpRequestTask().execute();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String[] ss = Arrays.toString(data.toArray()).replace("[", "")
                .replace("]", "")
                .split(",");
        strings = ss.clone();
        Log.d("strings", Arrays.toString(strings));
        //ListView相关
        lv_ranking = findViewById(R.id.lv_ranking);
        lv_ranking.setAdapter(new ArrayAdapter(this, R.layout.item_selector, strings));
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(AddressUrl.url + "/rank/my/getRankLow");
                switch (getValuesUtil.getIntValue(RankingActivity.this,"dif")) {
                    case 0:
                        url = new URL(AddressUrl.url + "/rank/my/getRankLow");
                        break;
                    case 1:
                        url = new URL(AddressUrl.url + "/rank/my/getRankMedium");
                        break;
                    case 2:
                        url = new URL(AddressUrl.url + "/rank/my/getRankHigh");
                        break;
                }

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
                    String res = response.toString();
                    Gson mGson = new Gson();
                    RankingResult rankingResult = mGson.fromJson(res, RankingResult.class);
                    data = rankingResult.getData();
                    Log.d("RankingResult", Arrays.toString(strings));
                    return "数据获取成功";
                } else {
//                    return "GET request failed. Response Code: " + responseCode;
                    return "数据获取失败，请检查网络后重试";
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