package com.example.mylink_10.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylink_10.R;

public class OptionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Integer defOpt = 0;        //默认选项
    private SharedPreferences optionConfig;
    private boolean mUseMyTheme;
    private SharedPreferences.Editor optEdit;

    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取之前选择的选项
        optionConfig = getSharedPreferences("option-config", Context.MODE_PRIVATE);
        mUseMyTheme = optionConfig.getBoolean("mUseMyTheme",true); //第一次默认亮色主题
        if (!mUseMyTheme) {
            setTheme(R.style.NightTheme);//黑色主题
        } else {
            setTheme(R.style.LightTheme);//亮色主题
        }

        setContentView(R.layout.activity_option);

        optEdit = optionConfig.edit();

        findViewById(R.id.btn_musicOpt).setOnClickListener(view -> {
            startActivity(new Intent(this, MusicActivity.class));
        });

        //background related
        findViewById(R.id.btn_background).setOnClickListener(this);

        reload();
    }

    @Override
    public void onClick(View view) {
        mUseMyTheme = !mUseMyTheme;//切换主题状态
        recreate();//重启资源
        optEdit.putBoolean("useMyTheme", mUseMyTheme);//存储主题更换状态
        optEdit.commit();
    }

    /**
     * 重载所有已选择选项
     */
    private void reload() {
        mUseMyTheme = optionConfig.getBoolean("mUseMyTheme", true);
    }
}