package com.example.mylink_10.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mylink_10.R;
import com.example.mylink_10.util.ToastUtil;
import com.example.mylink_10.util.getValuesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyCommentActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    TextView tv_username;
    ListView lv_mypost;
    SwipeRefreshLayout refresh_mypost;
    List<CommunityMSG> MSGList;
    CommunityMSG ownerMSG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycomment);
        tv_username = findViewById(R.id.tv_username);
        String username = getValuesUtil.getStrValue(this,"username");
        Log.d("MyComment",username);
        tv_username.setText(username);
        lv_mypost = findViewById(R.id.lv_mypost);
        lv_mypost.setOnItemClickListener( MyCommentActivity.this);
        refresh_mypost = findViewById(R.id.refresh_mypost);
        refresh_mypost.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefrashDate();
                refresh_mypost.setRefreshing(false);
            }
        });
        refresh_mypost.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RefrashDate();
                // 刷新完成后，结束下拉刷新
                refresh_mypost.setRefreshing(false);
            }
        }, 0);
    }

    private void RefrashDate()
    {
        Thread thread = new Thread(() -> GetAllMyComments());
        thread.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        MSGList = CommunityMSG.getMyDefaultList(tv_username.getText().toString());
        //Log.d("RefrashDate", String.valueOf(MSGList.size()));
        //构建适配器
        lv_mypost.setAdapter(new CommunityBaseAdapter(MyCommentActivity.this,MSGList));

    }
    private void GetAllMyComments()
    {
        try {
            URL url = new URL("http://1.15.76.132:8080/comment/my/getMyComment?page=1");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            String token = getValuesUtil.getStrValue(this,"token");
            connection.setRequestProperty("Authorization",token);
            int responseCode = connection.getResponseCode();
            Log.d("GetAllMyComments", String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK)  {
                Log.d("GetAllMyComments ","success");
                InputStream inputStream = connection.getInputStream();
                StringBuilder responseBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();
                String serverResponse = responseBuilder.toString();
                JSONObject responseJson = new JSONObject(serverResponse);
                int code = responseJson.getInt("code");

                if (code == 200) {
                    Log.d("GetAllMyComments code", String.valueOf(code));
                    // 解析服务器响应中的音乐列表
                    JSONArray MSGArray = responseJson.getJSONArray("data");
                    //Log.d("GetAllMyComments MSGArray",MSGArray.toString());
                    ArrayList<String> content = new ArrayList<>();
                    ArrayList<String> time = new ArrayList<>();
                    for (int i = 0; i < MSGArray.length(); i++) {
                        content.add(MSGArray.getJSONObject(i).getString("Content"));
                        time.add(MSGArray.getJSONObject(i).getString("Time"));
                    }
                    CommunityMSG.setMypost_contentArray(content);
                    CommunityMSG.setMypost_timeArray(time);
                    //Log.d("GetAllMyComments",content.toString());
                } else {
                    // 处理请求错误
                    String error = responseJson.optString("error");
                    ToastUtil.show(this, error);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, CommunityRemarksActivity.class);
        intent.putExtra("CommunityMSG",  MSGList.get(position));
        startActivity(intent);
    }
}