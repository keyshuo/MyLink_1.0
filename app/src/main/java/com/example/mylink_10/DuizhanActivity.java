package com.example.mylink_10;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.mylink_10.util.getValuesUtil;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class DuizhanActivity extends AppCompatActivity {

    private String token;
    private String username;
    private int myScore;
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
    //比赛记录
    private ModeSelectionActivity.Competition competition;
    boolean flag1=false;
    boolean flag2=false;
    boolean flag3=false;
    boolean flag4=false;
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
            TextView sco = findViewById(R.id.sco_mine);
            myScore=(GameConf.X * GameConf.Y - game.getBoard().getTot()) * 5;
            sco.setText("我的分数: " + myScore);
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
        token = getValuesUtil.getStrValue(this,"token");
        username = getValuesUtil.getStrValue(this,"username");

        Intent intent = getIntent();
        String competitionJson= intent.getStringExtra("competition");
        Gson gson = new Gson();
        competition = gson.fromJson(competitionJson, ModeSelectionActivity.Competition.class);
        // 弹出准备按钮，按下发出 WebSocket 连接请求
        // 按照服务器的逻辑进行比赛
        runOnUiThread(this::showReadyButton);
        //在点击后才开始init
        //init需要根据棋盘号更新，直接根据中等难度初始化！！！
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

    //准备游戏后才开始
    private void showReadyButton() {
        int pos=position();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("即将开始");
        builder.setMessage("点击准备，开始游戏");
        builder.setPositiveButton("准备", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("这是websocket连接！");
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String serverUrl = "ws://1.15.76.132:8080/competition/my/startGame?sign="+competition.sign+"&Authorization="+token; // 替换为实际的服务器 WebSocket URL
                        System.out.println(serverUrl);
                        try {
                            URI uri = new URI(serverUrl);
                            webSocketClient = new WebSocketClient(uri) {
                                @Override
                                public void onOpen(ServerHandshake serverHandshake) {
                                    //开始游戏后，开始发送游戏信息
                                    if (flag1 && !flag2){
                                        //competition对象分数信息需要更新！！！
                                        competition.players[pos].score=myScore;
                                        webSocketClient.send(competition.toString());//发送比赛信息
                                    }
                                }


                                @Override
                                public void onMessage(String message) {
                                    //收到开始时间，时间到了开始。
                                    Gson gson = new Gson();
                                    competition = gson.fromJson(message, ModeSelectionActivity.Competition.class);
                                    while (!flag1) {
                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                        String time = sdf.format(new Date());
                                        if (time.equals(competition.start)) {
                                            startTimer();
                                            flag1 = true;
                                            break;
                                        }
                                    }
                                    //开始游戏后，不断接收信息，显示对方分数
                                    while (!flag2) {
                                        //显示对方分数
                                        TextView sco = findViewById(R.id.sco);
                                        sco.setText("对手分数" + competition.players[getOpponent(pos)].score);
                                        if (competition.end) {
                                            flag2 = true;
                                            break;
                                        }
                                    }
                                    while (!flag3) {
                                        //显示输赢。
                                        //这个方法返回字符串
                                        compare(pos);
                                        webSocketClient.close();
                                    }
                                }
//                                    Thread thread1=new Thread(() -> {
//                                        if(competition.end){
//                                            stopTimer();
//                                            //还差一个停止计时
//                                            //发送判别输赢
//                                            //获取后，才能弹窗判断输赢
//                                        }
//                                    });
//                                    thread1.start();




                                @Override
                                public void onClose(int code, String reason, boolean remote) {
                                    // WebSocket 连接已关闭
                                    // 可以在这里处理连接关闭事件
                                    // 游戏结束，连接关闭，通知服务器删除比赛
                                    URL url = null;
                                    try {
                                        url = new URL("http://1.15.76.132:8080/competition/my/finishGame?room="+competition.sign);
                                    } catch (MalformedURLException e) {
                                        throw new RuntimeException(e);
                                    }
                                    HttpURLConnection connection = null;
                                    try {
                                        connection = (HttpURLConnection) url.openConnection();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    try {
                                        connection.setRequestMethod("GET");
                                    } catch (ProtocolException e) {
                                        throw new RuntimeException(e);
                                    }
                                    int responseCode = 0;
                                    try {
                                        responseCode = connection.getResponseCode();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        Log.d("取消", "成功 ");
                                    }
                                }

                                @Override
                                public void onError(Exception ex) {
                                    // WebSocket 连接发生错误
                                    // 可以在这里处理连接错误事件
                                }
                            };
                            webSocketClient.connect(); // 执行连接
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                //webSocketClient.connect();
            }
        });
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int position(){
        if (Objects.equals(competition.players[0].username, username)){
            return 0;
        }else if (Objects.equals(competition.players[1].username, username)) {
            return 1;
        }
        return -1;
    }
    private String compare(int pos){
        if (competition.players[pos].score>competition.players[getOpponent(pos)].score){
            return "你赢了";
        } else if (competition.players[pos].score<competition.players[getOpponent(pos)].score) {
            return "你输了";
        }else {
            return "平局";
        }
    }

    private int getOpponent(int value){
        int boolValue;
        if (value == 0) {
            boolValue = 1;
        } else {
            boolValue = 0;
        }
        return boolValue;
    }
}
