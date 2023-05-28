package com.example.mylink_10;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mylink_10.activity.LoginActivity;
import com.example.mylink_10.activity.OptionActivity;
import com.example.mylink_10.activity.RankingActivity;
import com.example.mylink_10.activity.SignUpActivity;
import com.example.mylink_10.util.getValuesUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link myMessage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class myMessage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public myMessage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment myMessage.
     */
    // TODO: Rename and change types and number of parameters
    public static myMessage newInstance(String param1, String param2) {
        myMessage fragment = new myMessage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_message, container, false);
    }

    /**
     * 跳转登录页面、注册页面等
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @SuppressLint("ResourceType")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button btn_login = getActivity().findViewById(R.id.btn_login);
        Button btn_signup = getActivity().findViewById(R.id.btn_signup);
        Button btn_ranking = getActivity().findViewById(R.id.btn_ranking);
        ImageButton btn_option = getActivity().findViewById(R.id.btn_option);
        Button btn_logout = getActivity().findViewById(R.id.btn_logout);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("option-config", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        //判断是否登录以确定按钮是否可选
        if (!"".equals(token)) {
            btn_login.setEnabled(false);
            btn_signup.setEnabled(false);
            TextView tv_username = getActivity().findViewById(R.id.tv_username);
            tv_username.setText(getValuesUtil.getStrValue(getActivity(),"username"));
        } else {
            btn_ranking.setEnabled(false);
            btn_logout.setEnabled(false);
        }
        //跳转登录页面
        btn_login.setOnClickListener(view -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        //跳转注册页面
        btn_signup.setOnClickListener(view -> startActivity(new Intent(getActivity(), SignUpActivity.class)));
        //跳转设置页面
        btn_option.setOnClickListener(view -> startActivity(new Intent(getActivity(), OptionActivity.class)));
        //注销登录逻辑
        btn_logout.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("注销登录");
            builder.setMessage("确定注销登录吗？");
            builder.setPositiveButton("是",(dialogInterface, i) -> {
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("token","");
                edit.putString("username","");
                edit.commit();
                getActivity().recreate();
            });
            builder.setNegativeButton("否",(dialogInterface, i) -> {});
            builder.create().show();
        });
        btn_ranking.setOnClickListener(view -> startActivity(new Intent(getActivity(), RankingActivity.class)));
    }
}