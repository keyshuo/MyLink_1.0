package com.example.mylink_10.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylink_10.R;

public class OptionActivity extends AppCompatActivity {

    private static final Integer defOpt = 0;        //默认选项
    private SharedPreferences optionConfig;

    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        //获取之前选择的选项
        optionConfig = getSharedPreferences("option-config", Context.MODE_PRIVATE);
        SharedPreferences.Editor optEdit = optionConfig.edit();

        findViewById(R.id.btn_musicOpt).setOnClickListener(view -> {
            startActivity(new Intent(this, MusicActivity.class));
        });

        //background related


//        reload();
    }

    /**
     * 重载所有已选择选项
     */
//    private void reload() {
//        int backgroundOpt = optionConfig.getInt("background", defOpt);
//        sp_background_opt.setSelection(backgroundOpt);
//    }
}