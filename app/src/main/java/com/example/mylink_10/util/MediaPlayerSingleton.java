package com.example.mylink_10.util;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerSingleton {
    private static MediaPlayer player = null;

    private static boolean isplay;

    public static MediaPlayer getInstance(Context context,int resourceId) {
        if (player == null) {
            player = MediaPlayer.create(context,resourceId);
            player.setLooping(true);
        }
        return player;
    }

    public static boolean isIsplay() {
        return isplay == player.isPlaying();
    }
}
