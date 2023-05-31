package com.example.mylink_10.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylink_10.R;
import com.example.mylink_10.util.getValuesUtil;

public class OptionActivity extends AppCompatActivity{

    private SharedPreferences optionConfig;
    private SharedPreferences.Editor optEdit;
    private int themeType;

    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //获取之前选择的选项
        themeType = getValuesUtil.getIntValue(this,"themeType"); //第一次默认亮色主题
        if (themeType == 0) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppTheme2);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        optionConfig = getSharedPreferences("option-config", Context.MODE_PRIVATE);

        optEdit = optionConfig.edit();
        Log.d("OPTHEME",String.valueOf(themeType));
        findViewById(R.id.btn_musicOpt).setOnClickListener(view -> {
            startActivity(new Intent(this, MusicActivity.class));
        });

        //background related
        findViewById(R.id.btn_background).setOnClickListener(view -> {
            themeType = themeType == 0 ? 1 : 0;
            optEdit.putInt("themeType",themeType);
            optEdit.commit();
            recreate();
        });
    }
}