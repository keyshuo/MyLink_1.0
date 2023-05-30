package com.example.mylink_10.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mylink_10.R;
import com.example.mylink_10.util.ToastUtil;
import com.example.mylink_10.util.getValuesUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CommunityRemarksActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String username;
    ListView lv_remarks;
    TextView tv_landlord_name;
    TextView tv_landlord_time;
    TextView tv_landlord_content;
    EditText ed_comments;
    List<CommunityMSG> MSGList;
    CommunityMSG ownerMSG;
    SwipeRefreshLayout refresh_remarks;
    boolean isSuccess;

    // 在Activity中定义回调接口
    public interface MyCallback {
        void onBooleanReceived(boolean myBoolean);
    }

    // 在Activity中实现回调接口方法
    private MyCallback mCallback = new MyCallback() {
        @Override
        public void onBooleanReceived(boolean myBoolean) {
            // 使用参数执行逻辑
            isSuccess = myBoolean;
            Log.d("CommunityRemarksActivity isSuccess", String.valueOf(isSuccess));
        }
    };

    // 在线程中调用回调接口方法传递参数到Activity


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定XML布局文件
        setContentView(R.layout.activity_remarks);
        //绑定控件
        tv_landlord_name = findViewById(R.id.cm_Landlord_name);
        tv_landlord_time = findViewById(R.id.cm_Landlord_time);
        tv_landlord_content = findViewById(R.id.cm_Landlord_content);
        ed_comments = findViewById(R.id.et_comments);
        //创建消息弹窗

        ed_comments.setOnClickListener(v -> showBottomSheetDialog(ed_comments.getText().toString()));
        lv_remarks = findViewById(R.id.ls_user);
        lv_remarks.setOnItemClickListener(CommunityRemarksActivity.this);
        ownerMSG = (CommunityMSG) getIntent().getSerializableExtra("CommunityMSG");
        tv_landlord_name.setText(ownerMSG.getName());
        tv_landlord_time.setText(ownerMSG.getTime());
        tv_landlord_content.setText(ownerMSG.getMsg());
        refresh_remarks = findViewById(R.id.refresh_remarks);
        refresh_remarks.setOnRefreshListener(() -> {
            RefrashDate();
            refresh_remarks.setRefreshing(false);
        });
        refresh_remarks.setRefreshing(true);
        new Handler().postDelayed(() -> {
            RefrashDate();
            // 刷新完成后，结束下拉刷新
            refresh_remarks.setRefreshing(false);
        }, 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, CommunityRemarksActivity.class);
        intent.putExtra("CommunityMSG",  MSGList.get(position));
        startActivity(intent);
    }

    // 弹出底部对话框
    private void showBottomSheetDialog(String str_text) {
        //绑定dialog及其控件
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_remark, null);
        EditText dialog_inputEditText = dialogView.findViewById(R.id.commentEditText);
        Button dialog_postButton = dialogView.findViewById(R.id.postButton);
        //创建dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        //保存之前输入的内容
        if(str_text != null)
        {
            dialog_inputEditText.setText(str_text);
        }
        //设置在Dialog底部显示软键盘
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        bottomSheetDialog.setCancelable(true);// 设置对话框可取消
        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.setOnShowListener(dialogInterface -> {
            dialog_inputEditText.setSelection(dialog_inputEditText.getText().length()); // 将光标移到文本末尾
            dialog_inputEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(dialog_inputEditText, InputMethodManager.SHOW_IMPLICIT);

        });
        // 设置对话框的取消监听器
        bottomSheetDialog.setOnCancelListener(dialog -> {
            // 在这里处理点击对话框以外的空白处的取消事件
            // 可以执行特定功能或关闭对话框等操作
            ed_comments.setText(dialog_inputEditText.getText().toString()); // 将输入的文字保存到 EditText 中
            bottomSheetDialog.dismiss(); // 关闭底部对话框
        });

        //实现发布按钮的功能
        dialog_postButton.setOnClickListener(v -> {
            isSuccess = false;
            String inputText = dialog_inputEditText.getText().toString();
            updateUsername();
            if (inputText.isEmpty()) {
                Toast.makeText(CommunityRemarksActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
            } else {
                if(!username.isEmpty())
                {
                    // 获取当前时间
                    String currentTime = getCurrentTime(); // 自定义方法获取当前时间
                    // 将文本内容和当前时间上传至服务器
                    //改写uploadDataToServer（）；
                    Log.d("showBottomSheetDialog","uploadDataToServer");
                    uploadDataToServer(inputText,currentTime);
                    Log.d("CommunityRemarksActivity", String.valueOf(isSuccess));
                    if(isSuccess)
                    {
                        //发送成功清除数据
                        ed_comments.setText(null);
                        // 关闭底部对话框
                        bottomSheetDialog.dismiss();
                        //刷新数据
                        refresh_remarks.setRefreshing(true);
                        new Handler().postDelayed(() -> {
                            RefrashDate();
                            // 刷新完成后，结束下拉刷新
                            refresh_remarks.setRefreshing(false);
                        }, 0);
                    }else{
                        Log.d("showBottomSheetDialog",tv_landlord_name.getText().toString());
                        Toast toast = new Toast(getApplicationContext());
                        // 创建一个包含文本的TextView作为自定义视图
                        TextView textView = new TextView(getApplicationContext());
                        textView.setText("发送失败,请检查网络！");
                        // 将TextView设置为Toast的视图
                        toast.setView(textView);
                        // 设置Toast的位置为屏幕中心
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        // 显示Toast
                        toast.show();
                    }
                }else{
                    Toast toast = new Toast(getApplicationContext());
                    // 创建一个包含文本的TextView作为自定义视图
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText("您还未登录，请登录后使用该功能");
                    // 将TextView设置为Toast的视图
                    toast.setView(textView);
                    // 设置Toast的位置为屏幕中心
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    // 显示Toast
                    toast.show();
                }
            }
        });
        bottomSheetDialog.show();
    }
    private void uploadDataToServer(String userInput, String currentTime) {

        // 创建线程来执行网络请求（请勿在主线程中执行网络请求）
        new Thread(() -> {
            try {
                //Log.d("uploadDataToServer", "running");
                // 构建请求参数
                // 创建连接对象
                Log.d("showBottomSheetDialog", userInput);
                Log.d("showBottomSheetDialog",currentTime);
                Log.d("showBottomSheetDialog",tv_landlord_time.getText().toString());
                Log.d("showBottomSheetDialog",tv_landlord_name.getText().toString());

                String urlString = "http://1.15.76.132:8080/comment/my/createCommentIndex?"+
                        "comment="+ URLEncoder.encode(userInput, "UTF-8")+
                        "&time="+ URLEncoder.encode(currentTime, "UTF-8")+
                        "&date_index=" + URLEncoder.encode(tv_landlord_time.getText().toString(),"UTF-8")+
                        "&username=" + URLEncoder.encode(tv_landlord_name.getText().toString(),"UTF-8");
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 设置请求方法为 GET
                //conn.setRequestMethod("GET");
                String token = getValuesUtil.getStrValue(CommunityRemarksActivity.this,"token");
                //Log.d("PostTextActivity_uploadDataToServer()_token",token);
                conn.setRequestProperty("Authorization", token);
                // 发起请求并获取响应
                int responseCode = conn.getResponseCode();
                Log.d("Remarks uploadDataToServer", String.valueOf((responseCode)));
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    //true
                    // 请求成功处理响应结果
                    conn.disconnect();
                    runOnUiThread(() -> {
                        Toast.makeText(CommunityRemarksActivity.this, "发表成功", Toast.LENGTH_SHORT).show();
                    });

                    mCallback.onBooleanReceived(true);
                } else {
                    // 请求失败处理错误信息
                    conn.disconnect();
                    runOnUiThread(() -> {
                        //Toast.makeText(CommunityRemarksActivity.this, "发表失败", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    //Toast.makeText(CommunityRemarksActivity.this, "系统错误,发表失败", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
        //mHandler.sendMessage((Message) (mHandler.obtainMessage().obj = false));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date currentTime = new Date(System.currentTimeMillis());
        return sdf.format(currentTime);
    }
    private void RefrashDate()
    {
        Thread thread = new Thread(() -> GetAllRemarks());
        thread.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        MSGList = CommunityMSG.getRMDefaultList();
        //构建适配器
        CommunityBaseAdapter adapter =  new CommunityBaseAdapter(CommunityRemarksActivity.this,MSGList);
        lv_remarks.setAdapter(adapter);
    }

    private void GetAllRemarks()
    {
        try {
            URL url = new URL("http://1.15.76.132:8080/comment/getCommentIndex?page=1"+
                    "&username="+ownerMSG.getName()+
                    "&time="+ownerMSG.getTime());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)  {
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
                    // 解析服务器响应中的音乐列表
                    JSONArray MSGArray = responseJson.getJSONArray("data");
                    Log.d("GetAllMassage MSGArray",MSGArray.toString());
                    ArrayList<String> username = new ArrayList<>();
                    ArrayList<String> content = new ArrayList<>();
                    ArrayList<String> time = new ArrayList<>();
                    for (int i = 0; i < MSGArray.length(); i++) {
                        username.add(MSGArray.getJSONObject(i).getString("Username"));
                        content.add(MSGArray.getJSONObject(i).getString("Content"));
                        time.add(MSGArray.getJSONObject(i).getString("Time"));
                    }
                    CommunityMSG.setRm_nameArray(username);
                    CommunityMSG.setRm_contentArray(content);
                    CommunityMSG.setRm_timeArray(time);
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
    private void updateUsername()
    {
        username = getValuesUtil.getStrValue(this,"username");
        Log.d("Remarks username",username.toString());
    }
}