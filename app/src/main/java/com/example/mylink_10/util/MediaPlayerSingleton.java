package com.example.mylink_10.util;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerSingleton {
//    private static MediaPlayerSingleton instance;
//    private final Context mContext;//上下文对象
//    private final MediaPlayer mMediaPlayer;//MediaPlayer媒体类
//    public static MediaPlayerSingleton getInstance(Context context) {
//        if (instance == null) {
//            synchronized (MediaPlayerSingleton.class) {
//                if (instance == null) {
//                    instance = new MediaPlayerSingleton(context);
//                }
//            }
//        }
//        return instance;
//    }
//
//    private MediaPlayerSingleton(Context context) {
//        mContext = context;
//        mMediaPlayer = new MediaPlayer();
//    }
//
//    public void start() {
//        mMediaPlayer.start();
//    }
//
//    public void pause() {
//        mMediaPlayer.pause();
//    }
//
//    public void stop() {
//        mMediaPlayer.stop();
//    }
//
//    public boolean isNull() {
//        return mMediaPlayer == null;
//    }
//
//    /**
//     * 是否正在播放
//     */
//    public boolean isPlaying() {
//        return mMediaPlayer.isPlaying();
//    }
//
//    public void reset() {
//        mMediaPlayer.release();
//    }
//
//    public void setDatasource(String path) throws IOException {
//        mMediaPlayer.setDataSource(path);
//    }
//
//    public void setLooping(Boolean isLooping) {
//        mMediaPlayer.setLooping(isLooping);
//    }
//
//    public void release() {
//        mMediaPlayer.release();
//    }
//
//    public void prepare() throws IOException {
//        mMediaPlayer.prepare();
//    }

    private static MediaPlayer player;
    public static MediaPlayer getInstance(Context context, int resourceId) {
        if (player == null) {
            player = MediaPlayer.create(context,resourceId);
            player.setLooping(true);
        }
        return player;
    }

    public static boolean isPlaying() {
        return player.isPlaying();
    }
}
