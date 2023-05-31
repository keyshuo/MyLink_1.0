package com.example.mylink_10.activity;

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

import com.example.mylink_10.R;
import com.example.mylink_10.gameRelated.Game;
import com.example.mylink_10.gameRelated.GameConf;
import com.example.mylink_10.gameRelated.GameView;
import com.example.mylink_10.util.getValuesUtil;
import com.google.gson.Gson;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class DuizhanActivity extends AppCompatActivity {
    public String ip="1.15.76.132:8080";
    private static class CompetitionWrapper {
        public ModeSelectionActivity.Competition competition;
    }
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
    boolean flag=false;
    boolean flag1=false;
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
        System.out.println("setBroadcast");
        IntentFilter intentFilter = new IntentFilter("action_win"); // 检查胜利的广播
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter("action_score"); // 更新分数的广播
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver2, intentFilter2);
    }

    @Override
    public void onDestroy() { // 注销广播接收器
        System.out.println("onDestroy");
        super.onDestroy();
//        Thread thread= new Thread(() -> {
//            try {
//                System.out.println(competition.sign);
//                URL url = new URL("http://"+ip+"/competition/my/finishGame?room=" + competition.sign);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//                int responseCode = connection.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    Log.d("取消", "成功 ");
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        thread.start();
        competition.end=true;
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver2);
    }

    private void start() {
        System.out.println("start");
        gameView.postInvalidate();
        game.startNewGame();
    }

    private void startTimer() {
        System.out.println("startTime");
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
        System.out.println("stopTime");
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duizhan);
        Intent intent = getIntent();
        String competitionJson= intent.getStringExtra("competition");
        Gson gson = new Gson();
        token= getValuesUtil.getStrValue(this,"token");
        username= getValuesUtil.getStrValue(this,"username");
        competition = gson.fromJson(competitionJson, ModeSelectionActivity.Competition.class);
        System.out.println("这是对战模式的oncreate");
        runOnUiThread(this::showReadyButton);
        init();
    }

    @Override
    public void onBackPressed() {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(DuizhanActivity.this);
            builder.setTitle("是否要认输？");
            builder.setPositiveButton("是", (dialog, which) -> {
                dialog.cancel();
                Thread thread = new Thread(() -> {
                    competition.players[pos].score = -1;
                    competition.end = true;
                    Gson gson1 = new Gson();
                    String json = gson1.toJson(competition);
                    webSocketClient.send(json);
                });
                thread.start();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 在操作完成后调用 super.onBackPressed() 执行返回操作
                Intent intent = new Intent(DuizhanActivity.this, ModeSelectionActivity.class);
                webSocketClient.close();
                startActivity(intent);
                finish();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });
            builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
            builder.setCancelable(false);

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        System.out.println("onBackPressed");
//        competition.end=true;
//        Gson gson1 = new Gson();
//        String json = gson1.toJson(competition);
//        webSocketClient.send(json); // 发送比赛信息
//        webSocketClient.close();
//        Intent intent = new Intent(DuizhanActivity.this, ModeSelectionActivity.class);
//        startActivity(intent);
//        //finish();
//    }

    /**
     * 游戏初始化
     */
    private void init() {
        System.out.println("init");
        GameConf.init(this, getApplicationContext());
        game = new Game();
        gameView = findViewById(R.id.gv);
        gameView.start(game, true);
        start();
        // et = new EditText(getApplicationContext());
        setBroadcast();
    }

    //准备游戏后才开始
    private void showReadyButton() {
        System.out.println("showReadyButton");
        int pos=position();
        String serverUrl = "ws://"+ip+"/competition/startGame?sign="+competition.sign+"&username="+username; // 替换为实际的服务器 WebSocket URL
        try {
            URI uri = new URI(serverUrl);
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    System.out.println("open");
                    new Thread(() -> {
                        while (!flag && !flag1) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            competition.players[pos].score = myScore;
                            Gson gson1 = new Gson();
                            String json = gson1.toJson(competition);
                            webSocketClient.send(json); // 发送比赛信息
                        }
                    }).start();
                    //此处没问题
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("message");
                    //收到开始时间，时间到了开始。
                    //开始游戏后，不断接收信息，显示对方分数
                    Gson gson = new Gson();
                    competition = gson.fromJson(message, ModeSelectionActivity.Competition.class);
                    runOnUiThread(() -> {
                        TextView sco = findViewById(R.id.sco);
                        sco.setText("对手分数" + competition.players[getOpponent(pos)].score);
                    });
                    //上方没问题
                    System.out.println(message);
                    System.out.println("对手状态"+competition.players[getOpponent(pos)].connStatus);
                    System.out.println("游戏状态"+flag1);
                    if (!flag1 && competition.end) {
                        flag1=true;
                        stopTimer();
                        runOnUiThread(() -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(DuizhanActivity.this);
                            builder.setTitle(compare(pos));
                            builder.setPositiveButton("退出", (dialog, which) -> {
                                dialog.cancel();
//                                webSocketClient.close();
                                Thread thread= new Thread(() -> {
                                    try {
                                        URL url = new URL("http://"+ip+"/competition/my/finishGame?room=" + competition.sign);
                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                        connection.setRequestMethod("GET");
                                        int responseCode = connection.getResponseCode();
                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                            Log.d("取消", "成功 ");
                                        }
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                thread.start();
                                Intent intent = new Intent(DuizhanActivity.this, ModeSelectionActivity.class);
                                startActivity(intent);
                                finish();
                            });
                            builder.setCancelable(false);

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        });
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                //未实现：
                //1.需要比赛结束后，检测到end后，直接比较，然后弹窗，弹完点击退出，删除房间OK
                //2.修改为正计时
                //3.棋盘根据索引统一初始化
                //4.重新连接可以在本地完成，逻辑大概是，

//                    new Thread(() -> {
//                        while (!flag) {
//                            TextView sco = findViewById(R.id.sco);
//                            sco.setText("对手分数" + competition.players[getOpponent(pos)].score);
//                            System.out.println("接收");
//                            if (competition.end) {
//                                flag = true;
//                                break;
//                            }
//                        }
//                    }).start();
//                    while (!flag) {
//                        显示输赢。
//                        这个方法返回字符串
//                        compare(pos);
//                    }

                @Override
                public void onWebsocketClosing(WebSocket conn, int code, String reason, boolean remote){
                    System.out.println("onclosing");
//                    competition.players[pos].connStatus=false;
//                    Gson gson1 = new Gson();
//                    String json = gson1.toJson(competition);
//                    webSocketClient.send(json); // 发送比赛信息
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("onclose");
                    Thread thread= new Thread(() -> {
                        try {
                            URL url = new URL("http://"+ip+"/competition/my/finishGame?room=" + competition.sign);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            int responseCode = connection.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                Log.d("取消", "成功 ");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    thread.start();
                }

                @Override
                public void onError(Exception ex) {
                }
            };
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("即将开始");
        builder.setMessage(competition.start+"，开始游戏");
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        Thread thread1=new Thread(() -> {
            while (true){
                LocalTime time1= LocalTime.parse(competition.start);
                LocalTime currentTime = LocalTime.now();
                if (currentTime.isAfter(time1)||currentTime.equals(time1)) {
                    dialog.cancel();
                    startTimer();
                    tim = finalTime;
                    num = findViewById(R.id.num_duizhan);
                    System.out.println("websocketConnection");
                    webSocketClient.connect(); // 执行连接
                    break;
                }
            }
        });
        thread1.start();
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
