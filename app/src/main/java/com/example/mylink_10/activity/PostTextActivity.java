package com.example.mylink_10.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylink_10.R;
import com.example.mylink_10.util.getValuesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PostTextActivity extends AppCompatActivity {

    private EditText et_post_text;
    private Button btn_post_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_text);

        et_post_text = findViewById(R.id.et_post_text);
        btn_post_text = findViewById(R.id.btn_post_text);

        Log.d("PostTextActivity","onCreating");

        et_post_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 在文本变化前执行的操作
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 在文本变化时执行的操作
                if (s.length() > 0) {
                    // 当文本框内有输入时，将按钮背景设置为绿色
                    btn_post_text.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                } else {
                    // 当文本框内无输入时，将按钮背景恢复默认状态
                    btn_post_text.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                // 在文本变化后执行的操作
            }
        });

        btn_post_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = et_post_text.getText().toString();
                if (userInput.isEmpty()) {
                    Toast.makeText(PostTextActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                } else {
                    // 获取当前时间
                    String currentTime = getCurrentTime(); // 自定义方法获取当前时间
                    Log.d("onClick","onClick");
                    // 将文本内容和当前时间上传至服务器

                    Log.d("uploadDataToServer", "userInput: " + userInput);
                    Log.d("uploadDataToServer", "currentTime: " + currentTime);

                    uploadDataToServer(userInput, currentTime);

                    //Toast.makeText(PostTextActivity.this, "发布成功", Toast.LENGTH_SHORT).show();

                    finish(); // 结束当前Activity
                }
            }
        });
    }

    private void uploadDataToServer(String userInput, String currentTime) {
        // 创建线程来执行网络请求（请勿在主线程中执行网络请求）
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("uploadDataToServer", "running");

                    // 构建请求参数
                    StringBuilder params = new StringBuilder();
                    params.append("content=").append(URLEncoder.encode(userInput, "UTF-8"));
                    params.append("&time=").append(URLEncoder.encode(currentTime, "UTF-8"));

                    // 创建连接对象

                    String urlString = "http://1.15.76.132:8080/comment/my/createComment?comment=" + URLEncoder.encode(userInput, "UTF-8") + "&time=" + URLEncoder.encode(currentTime, "UTF-8");
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // 设置请求方法为 GET
                    conn.setRequestMethod("GET");
                    String token = getValuesUtil.getStrValue(PostTextActivity.this,"token");
                    Log.d("PostTextActivity_uploadDataToServer()_token",token);
                    conn.setRequestProperty("Authorization", token);

                    // 发起请求并获取响应
                    int responseCode = conn.getResponseCode();
                    Log.d("uploadDataToServer", String.valueOf((responseCode)));
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // 请求成功处理响应结果
                        InputStream inputStream = conn.getInputStream();
                        // 处理输入流...
                        conn.disconnect();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PostTextActivity.this, "发表成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // 请求失败处理错误信息
                        conn.disconnect();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PostTextActivity.this, "发表失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PostTextActivity.this, "上传出错", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date currentTime = new Date(System.currentTimeMillis());
        return sdf.format(currentTime);
    }
}



