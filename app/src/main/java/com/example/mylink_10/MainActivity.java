package com.example.mylink_10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mylink_10.util.MediaPlayerSingleton;
import com.example.mylink_10.util.ThemeUtil;
import com.example.mylink_10.util.ToastUtil;
import com.example.mylink_10.util.getValuesUtil;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences = null;
    MediaPlayer player;
    ViewPager viewPager;
    TabLayout tabLayout;
    List<Fragment> list;
    private MyAdapter adapter;
    private homePage h;
    private community c;
    private myMessage ms;
    private final int[] titles = {R.string.tab_home, R.string.tab_community, R.string.tab_my};
    private final int[] images = {R.drawable.home, R.drawable.classify, R.drawable.me};
    private int themeType;
    private float textSize;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        //获取之前选择的选项
//        themeType = getValuesUtil.getIntValue(this,"themeType"); //第一次默认亮色主题
//        if (themeType == 0) {
//            setTheme(R.style.AppTheme);
//        } else {
//            setTheme(R.style.AppTheme2);
//        }
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle mBundle = new Bundle();
        initView(mBundle);
        player = MediaPlayerSingleton.getInstance(this,R.raw.bgm1);
        player.start();
        String username = getValuesUtil.getStrValue(this, "username");
        String token = getValuesUtil.getStrValue(this, "token");
        Log.d("username", username);
        if ("".equals(token)) {
            ToastUtil.show(this, "还没有登录，快去登录以获取更多体验吧！");
        } else {
            ToastUtil.show(this, username+",欢迎回来");
        }
        TextView t = findViewById(R.id.tv_test);
        textSize = t.getTextSize();
        Log.d("SIZE", String.valueOf(t.getTextSize()));
    }

    private void initView(Bundle mBundle) {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        //页面，数据源
        list = new ArrayList<>();
        h = new homePage();
        c = new community();
        ms = new myMessage();
        list.add(h);
        list.add(c);
        list.add(ms);
        //Activity向Fragment传递信息
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setArguments(mBundle);
        }
        adapter = new MyAdapter(getSupportFragmentManager(), this); //ViewPager的适配器
        viewPager.setAdapter(adapter);     ///使用适配器将ViewPager与Fragment绑定在一起
        tabLayout.setupWithViewPager(viewPager);   //将TabLayout与ViewPager绑定
        InitTabBar(); //初始化自定义标签视图
        //添加标签监听
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {//选中图片操作
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    if (tab == tabLayout.getTabAt(i)) {
                        viewPager.setCurrentItem(i);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    class MyAdapter extends FragmentPagerAdapter {

        private Context context;

        public MyAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

    }

    public void InitTabBar() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                View v = LayoutInflater.from(this).inflate(R.layout.tab_item, null);
                TextView textView = (TextView) v.findViewById(R.id.tv_title);
                ImageView imageView = (ImageView) v.findViewById(R.id.iv_icon);
                textView.setText(titles[i]);
                imageView.setImageResource(images[i]);
                tab.setCustomView(v);  //为标签tab设置视图v
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        Log.d("Destroy","Destroy");
        player.pause();
        super.onDestroy();
    }


    @Override
    protected void onRestart() {
        themeType = getValuesUtil.getIntValue(this,"themeType");
        if (themeType == 0) {                           //读取为亮色主题时
            if (textSize!=39){  //发现不为亮色主题时
                recreate();
            }
        }
        if (themeType == 1) {
            if (textSize!=41) {//读取为暗色主题时
                recreate();                             //发现不为暗色主题时
            }
        }
        player.start();
        super.onRestart();
    }

    @Override
    protected void onStart() {
        player.start();
        Log.d("Start","Start");
        super.onStart();
    }
}