package com.example.mylink_10.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mylink_10.R;
import com.example.mylink_10.gameRelated.Game;
import com.example.mylink_10.gameRelated.GameConf;
import com.example.mylink_10.gameRelated.GameView;
import com.example.mylink_10.util.DateFormatUtil;
import com.example.mylink_10.util.ThemeUtil;
import com.example.mylink_10.util.getValuesUtil;

import org.java_websocket.client.WebSocketClient;

import java.util.Timer;
import java.util.TimerTask;

public class NewGameActivity extends AppCompatActivity {
    private AlertDialog.Builder win, lost, over;
    private EditText et;
    private final int[] finalTime = {240000, 210000, 180000};
    private final int step = 45000;
    private int pos = 0;
    private GameView gameView;
    private Game game;
    private static WebSocketClient webSocketClient;
    private int tim;
    private TextView num, cnt;
    private Timer timer;
    private Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 0x666) {
                tim -= 100;
                if (tim == 0) {
                    stopTimer();
                    updateRecord(pos);
                    lost.setMessage("你一共成功通过了" + pos + "关！");
                    lost.show();
                } else {
                    String s = Float.toString((float) (1.0 * tim / 1000));
                    int dian = s.indexOf(".");
                    String s1 = s.substring(0, dian);
                    String s2 = s.substring(dian, dian + 2);
                    s = s1 + s2;
                    num.setText("倒计时: " + s + "s");
                }
            }
        }
    };
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("receiver", "ok");
            stopTimer();
            if (finalTime[GameConf.n] - step * (pos + 1) <= 45000) {
                stopTimer();
                updateRecord(pos + 1);
                over.setMessage("你一共成功通过了" + (pos + 1) + "关！");
                over.show();
            } else {
                updateRecord(pos + 1);
                win.show();
            }
        }
    };

    private void setBroadcast() { // 注册广播接收器，它接收GameView里发出的游戏胜利信息
        IntentFilter intentFilter = new IntentFilter("action_win");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() { // 注销广播接收器
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }

    private void start() {
        gameView.postInvalidate();
        displayRecord();
        game.startNewGame();
        tim = finalTime[GameConf.n] - step * pos;
        num = findViewById(R.id.num);
        startTimer();
    }

    private void startTimer() {
        stopTimer();
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0x666);
                }
            }, 0, 100);
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        init();
    }

    private void displayRecord() {
        if (cnt == null) {
            cnt = findViewById(R.id.cnt);
        }
        SharedPreferences sp = getSharedPreferences("record", Context.MODE_PRIVATE);
        String s = "username" + GameConf.n;
        int count = sp.getInt(s, 0);
        cnt.setText("最佳纪录: " + count + "关");
    }

    private void updateRecord(int count) {
        SharedPreferences sp = getSharedPreferences("option-config", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        String username = getValuesUtil.getStrValue(this, "username");
        String s = username + " " + GameConf.n + " " + DateFormatUtil.getTime();
        if (count > sp.getInt(s, 0)) {
            spEditor.putInt(s, count);
            spEditor.commit();
        }
    }

    /**
     * 游戏初始化
     */
    private void init() {
        GameConf.init(this, getApplicationContext());
        game = new Game();
        gameView = findViewById(R.id.gv);
        gameView.start(game, false);
        // et = new EditText(getApplicationContext());
        win = new AlertDialog.Builder(this).setTitle("过关!").setIcon(R.drawable.success)
                .setPositiveButton("下一关", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pos++;
                        start();
                    }
                }).setNeutralButton("退出闯关", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false);
        lost = new AlertDialog.Builder(this).setTitle("时间到！闯关失败……")
                .setNeutralButton("退出闯关", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pos = 0;
                        start();
                    }
                }).setCancelable(false);
        over = new AlertDialog.Builder(this).setTitle("通关！").setIcon(R.drawable.success)
                .setNeutralButton("退出闯关", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pos = 0;
                        start();
                    }
                }).setCancelable(false);
        start();
        setBroadcast();
    }

}