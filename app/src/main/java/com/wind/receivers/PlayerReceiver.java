package com.wind.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wind.carlauncher.DataManager;
import com.wind.data.PlayData;
import com.wind.interfaces.PlayInfoListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by w010003593 on 2018/4/10.
 */

public class PlayerReceiver extends BroadcastReceiver{
    private static final String TAG = "PlayerReceiver";
    private String nodePath = "/dev/controller";
    private PlayData playData;
    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "action: " + action);
        if (action.equals("com.android.music.metachanged")
                || action.equals("com.miui.player.metachanged")
                || action.equals("com.android.music.playstatechanged")
                || action.equals("com.android.music.queuechanged")) {
            String albumName = intent.getStringExtra("album");//专辑名
            String artistName = intent.getStringExtra("artist");//作者名
            String trackName = intent.getStringExtra("track");//歌曲名
            Log.i(TAG, "专辑名: " + albumName + ",作者名: " + artistName + ",歌曲名: " + trackName);

            if (playData == null){
                playData = new PlayData();
            }
            playData.setListener(new PlayInfoListener() {
                @Override
                public void doSomething(String album, String artist, String track) {
                    Log.i(TAG, "专辑名2: " + album + ",作者名: " + artist + ",歌曲名: " + track);
                    DataManager.getManager(context).setAlbumName(album);
                    DataManager.getManager(context).setArtistName(artist);
                    DataManager.getManager(context).setTrackName(track);
                }
            });
            playData.setPlayData(albumName, artistName, trackName);

        }else if(action.equals("com.wind.action.carlight")){
             String msg = intent.getStringExtra("lightMsg");
             writeNodeString(msg, nodePath);
        }
    }

    private void writeNodeString(String msg, String path){
        try {
            Log.i(TAG, "writeNodeString msg = " + msg);
            BufferedWriter bufWriter = null;
            bufWriter = new BufferedWriter(new FileWriter(path));
            bufWriter.write(msg);  // 写操作
            bufWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"can't write the " + path);
        }
    }
}
