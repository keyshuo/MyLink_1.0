package com.example.mylink_10;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.example.mylink_10.gameRelated.Game;
import com.example.mylink_10.gameRelated.GameConf;
import com.example.mylink_10.gameRelated.GameView;

import org.java_websocket.client.WebSocketClient;

import java.util.Timer;
import java.util.TimerTask;

public class DuizhanActivity extends AppCompatActivity {
    private AlertDialog.Builder win, lost, over;
    private EditText et;
    private final int finalTime = 180000;
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
            if(msg.what == 0x666) {
                tim -= 100;
                if(tim == 0) {
                    stopTimer();
                    /*
                        自己的时间到了
                     */
                } else {
                    String s = Float.toString((float)(1.0 * tim / 1000));
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
            /*
                自己完成游戏
                win.show();
            */
        }
    };
    private BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView sco_mine = findViewById(R.id.sco_mine);
            sco_mine.setText("我的分数: " + ((GameConf.X * GameConf.Y - game.getBoard().getTot()) * 5));
        }
    };

    private void setBroadcast() { // 注册广播接收器，它接收GameView里发出的游戏胜利信息
        IntentFilter intentFilter = new IntentFilter("action_win"); // 检查胜利的广播
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter("action_score"); // 更新分数的广播
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver2, intentFilter2);
    }

    @Override
    public void onDestroy() { // 注销广播接收器
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver2);
    }

    private void start() {
        gameView.postInvalidate();
        game.startNewGame();
        tim = finalTime;
        num = findViewById(R.id.num_duizhan);
        startTimer();
    }

    private void startTimer() {
        stopTimer();
        if(timer == null) {
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
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duizhan);
        init();
    }

    /**
     * 游戏初始化
     */
    private void init() {
        GameConf.init(this, getApplicationContext());
        game = new Game();
        gameView = findViewById(R.id.gv);
        gameView.start(game, true);
        // et = new EditText(getApplicationContext());
        start();
        setBroadcast();
    }

}