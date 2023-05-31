package com.example.mylink_10.activity;

import static android.content.ContentValues.TAG;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylink_10.R;
import com.example.mylink_10.util.MediaPlayerSingleton;
import com.example.mylink_10.util.ThemeUtil;
import com.example.mylink_10.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ListView musicListView;
    private List<String> musicList;
    private String musicName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        mediaPlayer = MediaPlayerSingleton.getInstance(this,R.raw.bgm1);

        new GetMusicListTask().execute();
        musicListView = findViewById(R.id.musicListView);

//        btn_getMusicList.setOnClickListener(v -> );

        musicListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMusic = musicList.get(position);
            new DownloadMusicTask(selectedMusic).execute(selectedMusic);
        });
    }

    private void displayMusicList(List<String> musicList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, musicList);
        musicListView.setAdapter(adapter);
    }

    private class GetMusicListTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> musicList = new ArrayList<>();

            try {
                URL url = new URL("http://1.15.76.132:8080/music/getList");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                Log.d(TAG, "doInBackground: ");
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    StringBuilder responseBuilder = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    reader.close();
                    String serverResponse = responseBuilder.toString();

                    JSONObject responseJson = new JSONObject(serverResponse);
                    int code = responseJson.getInt("code");
                    if (code == 200) {
                        // 解析服务器响应中的音乐列表
                        JSONArray musicArray = responseJson.getJSONArray("data");
                        musicList = new ArrayList<>();
                        for (int i = 0; i < musicArray.length(); i++) {
                            String music = musicArray.getString(i);
                            // 将音乐列表添加到musicList
                            musicList.add(music);
                        }
                    } else {
                        // 处理请求错误
                        String error = responseJson.optString("error");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MusicActivity.this, "请求错误：" + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return musicList;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            musicList = result;
            displayMusicList(result);
        }
    }

    private class DownloadMusicTask extends AsyncTask<String, Void, Boolean> {
        private String selectedMusicName;

        public DownloadMusicTask(String selectedMusic) {
            this.selectedMusicName = selectedMusic;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String selectedMusicUrl = "http://1.15.76.132:8080/music/getMusic?name=" + params[0];
            musicName = params[0];
            // 检查本地是否已存在同名的文件
            File musicFile = new File(getFilesDir(), params[0]);
            if (musicFile.exists()) {
                // 如果文件已存在，直接将该文件设置为默认音乐
                setDefaultMusic(musicFile);
                return true;
            }
            runOnUiThread(() -> {
                ToastUtil.show(MusicActivity.this, "正在下载歌曲" + params[0]);
            });

            try {
                URL url = new URL(selectedMusicUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                //获取数据流
                InputStream inputStream = connection.getInputStream();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }
                    byte[] musicData = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    //保存文件
                    musicFile = new File(getFilesDir(), params[0]);
                    FileOutputStream fileOutputStream = new FileOutputStream(musicFile);
                    fileOutputStream.write(musicData);
                    fileOutputStream.close();
                    runOnUiThread(() -> {
                        Toast.makeText(MusicActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                    });
                    setDefaultMusic(musicFile);
                    return true;
                } else {
                    StringBuilder responseBuilder = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    reader.close();
                    String serverResponse = responseBuilder.toString();

                    JSONObject responseJson = new JSONObject(serverResponse);
                    // 处理请求错误
                    String error = responseJson.optString("error");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MusicActivity.this, "请求错误：" + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                connection.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
//            if (result) {
//                // 下载并保存音乐文件成功
//                Toast.makeText(MusicActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
//                // 将选择的音乐设置为默认背景音乐
//                setDefaultMusic(new File(getFilesDir(), selectedMusicName));
//            } else {
//                // 下载并保存音乐文件失败
//                Toast.makeText(MusicActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
//            }
        }

        // 设置音乐文件为默认
        private void setDefaultMusic(File musicFile) {
            // 在这里可以执行更新UI等
            // 例如，播放选中的音乐
            playMusic(musicFile);
        }

        private void playMusic(File musicFile) {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.reset();
                }

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(musicFile.getAbsolutePath());

                mediaPlayer.prepare();
//                mediaPlayer.setLooping(true);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // 在播放完成时执行逻辑
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.start();
                runOnUiThread(() -> {
                    ToastUtil.show(MusicActivity.this, "正在播放歌曲" + musicName);
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}