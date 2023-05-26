package com.example.mylink_10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylink_10.pojo.Result;
import com.example.mylink_10.pojo.User;
import com.example.mylink_10.util.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private String userJson;
    private static final String u = "https://www.baidu.com";
    private static final String loginUrl = "http://1.15.76.132:8080/login";
    private static SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("option-config", Context.MODE_PRIVATE);
        findViewById(R.id.btn_login_in).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        EditText et_account_login = findViewById(R.id.et_account_login);
        EditText et_pwd_login = findViewById(R.id.et_pwd_login);
        String account = et_account_login.getText().toString();
        String password = et_pwd_login.getText().toString();
        boolean emptyCondition = "".equals(account) || "".equals(password);
        if (emptyCondition) {
            ToastUtil.show(this, "请将所有信息填写完全！");
        } else {
            if (account.length() < 11) {
                ToastUtil.show(this, "账号长度不足，请检查后重新输入！");
                return;
            }
            if (password.length() < 6 || password.length() > 16) {
                ToastUtil.show(this, "密码长度过短或过长，请检查后重新输入！");
            } else {
                User user = new User();
                user.setAccount("614481987");
                user.setPassword("wx15015990723");
                //将user转换为json字符串
                userJson = new GsonBuilder().disableHtmlEscaping().create().toJson(user);
                new HttpRequestTask().execute();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                String token = sharedPreferences.getString("token", "");
                if (!"".equals(token)) {
                    startActivity(new Intent(LoginActivity.this,MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        }

    }

    private class HttpRequestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://1.15.76.132:8080/login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                //允许输出并写入json数据
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                osw.write(userJson);
                osw.flush();
                osw.close();

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
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString("token",new Gson().fromJson(response.toString(), Result.class).getData());
                    edit.apply();
                    return "登录成功";
                } else {
//                    return "GET request failed. Response Code: " + responseCode;
                    return "登录失败，请检查账号密码或网络后重试";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // 处理网络请求的结果
            Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }
}