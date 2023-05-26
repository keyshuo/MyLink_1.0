package com.example.mylink_10;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ModeSelectionActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection);
        findViewById(R.id.btn_standalone).setOnClickListener(this);
        findViewById(R.id.btn_online).setOnClickListener(this);
        findViewById(R.id.btn_challenge).setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btn_standalone:
                intent = new Intent(this,XiuxianActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_online:
                intent = new Intent(this,DuizhanActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_challenge:
                intent = new Intent(this,NewGameActivity.class);
                startActivity(intent);
                break;
        }
    }
}