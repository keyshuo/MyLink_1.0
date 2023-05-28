package com.example.mylink_10;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mylink_10.activity.CommunityBaseAdapter;
import com.example.mylink_10.activity.CommunityMSG;
import com.example.mylink_10.activity.CommunityRemarksActivity;
import com.example.mylink_10.activity.MyCommentActivity;
import com.example.mylink_10.activity.PostTextActivity;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link community#newInstance} factory method to
 * create an instance of this fragment.
 */
public class community extends Fragment implements AdapterView.OnItemClickListener {

    private String username;
    private ListView lv_community;
    private Activity parentActicity;
    private List<CommunityMSG> MSGList;
    private ImageButton bt_menu;
    SwipeRefreshLayout refresh_community;
    public community() {
        // Required empty public constructor
    }
    public static community newInstance() {
        community fragment = new community();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refresh_community = view.findViewById(R.id.refresh_community);
        refresh_community.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 执行刷新操作
                RefrashDate();
                refresh_community.setRefreshing(false);
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        parentActicity = getActivity();
        lv_community = view.findViewById(R.id.cm_ls_com_item);
        lv_community.setOnItemClickListener(community.this);
        // 初始化按钮
        bt_menu = view.findViewById(R.id.bt_menu);
        // 设置按钮点击事件
        bt_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建Intent对象，指定要跳转的目标Activity
                Intent intent = new Intent(getActivity(), PostTextActivity.class);
                updateUsername();
                //启动菜单
                showPopupMenu();
            }
        });
        return view;
    }

    private void updateUsername()
    {
        username = getValuesUtil.getStrValue(parentActicity,"username");
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(getActivity(), bt_menu);
        popupMenu.getMenuInflater().inflate(R.menu.community_selection, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(!username.isEmpty())
                {
                    switch (item.getItemId()) {
                        case R.id.option1:
                            openNewActivity1();
                            return true;
                        case R.id.option2:
                            openNewActivity2();
                            return true;
                        default:
                            return false;
                    }
                }else{
                    Toast.makeText(parentActicity, "您还未登录，登录后开放此功能", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
        popupMenu.show();
    }
    private void openNewActivity1() {
        // 打开新界面1的操作
        Intent intent = new Intent(getActivity(), PostTextActivity.class);
        startActivity(intent);
    }
    private void openNewActivity2() {
        // 打开新界面2的操作
        Intent intent = new Intent(getActivity(), MyCommentActivity.class);
        startActivity(intent);
    }
    private void GetAllMassage()
    {
        try {
            URL url = new URL("http://1.15.76.132:8080/comment/getComment?page=1");
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
                    CommunityMSG.setNameArray(username);
                    CommunityMSG.setContentArray(content);
                    CommunityMSG.setTimeArray(time);
                } else {
                    // 处理请求错误
                    String error = responseJson.optString("error");
                    parentActicity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(parentActicity, "请求错误：" + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refresh_community.setRefreshing(true);
        username = getValuesUtil.getStrValue(parentActicity,"username");
        Log.d("community",username);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RefrashDate();
                // 刷新完成后，结束下拉刷新
                refresh_community.setRefreshing(false);
            }
        }, 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), CommunityRemarksActivity.class);
        intent.putExtra("CommunityMSG",  MSGList.get(position));
        startActivity(intent);
    }
    private void RefrashDate()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run(){
                GetAllMassage();
            }
        });
        thread.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 在这里更新你的UI或执行其他操作
        MSGList = CommunityMSG.getDefaultList();
        //构建适配器
        CommunityBaseAdapter adapter =  new CommunityBaseAdapter(parentActicity,MSGList);
        lv_community.setAdapter(adapter);
    }
}