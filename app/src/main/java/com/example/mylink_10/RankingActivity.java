package com.example.mylink_10;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class RankingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        String[] ranking = {"李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",
                "李思林 2014010205 0123456","李思林 2014010205 0123456",};
        //ListView相关
        ArrayAdapter rankingAdapter = new ArrayAdapter(this,R.layout.item_selector,ranking);
        ListView lv_ranking = findViewById(R.id.lv_ranking);
        lv_ranking.setAdapter(rankingAdapter);
    }
}