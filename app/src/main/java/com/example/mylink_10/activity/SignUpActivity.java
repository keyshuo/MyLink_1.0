package com.example.mylink_10.activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylink_10.R;
import com.example.mylink_10.pojo.User;
import com.example.mylink_10.util.ToastUtil;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static String u = "http://localhost:8080/user";
    private static String bai = "https://www.baidu.com";
    private static String registerUrl = "http://uwna3a.natappfree.cc/user";
    private static String userJson;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViewById(R.id.btn_signup_sign).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        EditText et_account_sign = findViewById(R.id.et_account_sign);
        EditText et_username_sign = findViewById(R.id.et_username_sign);
        EditText et_password_sign = findViewById(R.id.et_password_sign);
        EditText et_confirmPwd_sign = findViewById(R.id.et_confirmPwd_sign);
        String account = et_account_sign.getText().toString();
        String username = et_username_sign.getText().toString();
        String password = et_password_sign.getText().toString();
        String confirmPwd = et_confirmPwd_sign.getText().toString();
        boolean emptyCondition = "".equals(account) || "".equals(username) || "".equals(password) || "".equals(confirmPwd);
        boolean pwdDifference = !password.equals(confirmPwd);
        if (emptyCondition) {
            ToastUtil.show(this, "请将所有信息填写完全！");
        } else {
            if (account.length() != 11) {
                ToastUtil.show(this, "账号长度过长或过短，请检查后重新输入！");
                return;
            }
            if (password.length() < 6 || password.length() > 16) {
                ToastUtil.show(this, "密码长度过短或过长，请检查后重新输入！");
                return;
            }
            if (pwdDifference) {
                ToastUtil.show(this, "两次密码不同，请检查后重新输入！");
            } else {
                User user = new User();
                user.setAccount(account);
                user.setUsername(username);
                user.setPassword(password);
                //将user转换为json字符串
                userJson = new GsonBuilder().disableHtmlEscaping().create().toJson(user);
                Log.d("JSON",userJson);
                new HttpRequestTask().execute();
            }
        }
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://1.15.76.132:8080/register");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                //允许输出并写入json数据
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");
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
                    return response.toString();
                } else {
                    return "GET request failed. Response Code: " + responseCode;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // 处理网络请求的结果
            Toast.makeText(SignUpActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }
}