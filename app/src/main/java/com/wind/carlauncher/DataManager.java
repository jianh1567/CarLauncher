package com.wind.carlauncher;

import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.util.Log;

import com.wind.database.SQLiteOpenHelperUtil;
import com.wind.receivers.PlayerReceiver;
import com.wind.utils.FileUtil;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by w010003593 on 2018/4/10.
 */

public class DataManager{
    private static final String TAG = "DataManager";
    private static final double SINGLE_SU_DU = (1000/3600);
    private static DataManager manager = null;
    private AudioManager audioManager;
    private Context context;
    public String albumName;
    private String artistName;
    private String trackName;
    private double totalDistance;
    private double singleDistance;
    private PlayerReceiver mPlayerReceiver;
    private TrackChangeListener mTrackChangeListener;

    public DataManager(Context context) {
        this.context = context;
        audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        mPlayerReceiver = new PlayerReceiver();
    }

    public static DataManager getManager(Context context) {
        if (manager == null){
            manager = new DataManager(context);
        }
        return manager;
    }

    /*
     * 音乐是否在播放
     */
    public boolean isPlayMusic(){
        return audioManager.isMusicActive();
    }

    /*
     * 注册音乐播放器广播
     */
    public void registerReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.music.metachanged");
        intentFilter.addAction("com.android.music.playstatechanged");
        intentFilter.addAction("com.android.music.playbackcomplete");
        intentFilter.addAction("com.android.music.queuechanged");
        intentFilter.addAction("com.htc.music.metachanged");
        intentFilter.addAction("fm.last.android.metachanged");
        intentFilter.addAction("com.sec.android.app.music.metachanged");
        intentFilter.addAction("com.nullsoft.winamp.metachanged");
        intentFilter.addAction("com.amazon.mp3.metachanged");
        intentFilter.addAction("com.miui.player.metachanged");
        intentFilter.addAction("com.real.IMP.metachanged");
        intentFilter.addAction("com.sonyericsson.music.metachanged");
        intentFilter.addAction("com.rdio.android.metachanged");
        intentFilter.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        intentFilter.addAction("com.andrew.apollo.metachanged");
        intentFilter.addAction("com.kugou.android.music.metachanged");
        intentFilter.addAction("com.ting.mp3.playinfo_changed");
        context.registerReceiver(mPlayerReceiver,intentFilter);
    }

    public void unregisterReceiver(){
        context.unregisterReceiver(mPlayerReceiver);
    }

    /*
     * 获取音乐专辑
     */
    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
    /*
     * 获取音乐歌唱者
     */
    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    /*
     * 获取音乐名称
     */
    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
        Log.i("minos", "setTrackName trackName = " + trackName);
        mTrackChangeListener.onTrackNameChange(trackName);
    }

    public void setTrackNameChangeListener(TrackChangeListener listener){
        this.mTrackChangeListener = listener;
    };

    public interface TrackChangeListener{
        void onTrackNameChange(String trackName);
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getSingleDistance() {
        return singleDistance;
    }

    public void setSingleDistance(double singleDistance) {
        this.singleDistance = singleDistance;
    }

    public void startNoteTotalDistance(String filePath,String fileName,int CurSpeed,int R){
        double startDistance = 0.0;
        File file1 = new File(filePath);
        if (!file1.exists()){
            file1.mkdirs();
        }
        File file = new File(filePath + "/" + fileName);
        if (!file.exists()){
            try {
                file.createNewFile();
                FileUtil.writeSDFile("0.0",fileName,filePath);
                startDistance = Double.parseDouble(FileUtil.readSDFile(fileName, filePath));
                double curDistance = (CurSpeed*1000/3600) + startDistance;
                FileUtil.writeSDFile(String .format("%.1f",curDistance) ,fileName,filePath);
                setTotalDistance(curDistance);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                if(!StringUtils.isBlank(FileUtil.readSDFile(fileName, filePath))){
                    startDistance = Double.parseDouble(FileUtil.readSDFile(fileName, filePath));
                }else {
                    startDistance = 0;
                }
                Log.i(TAG,"startDistance: " + FileUtil.readSDFile(fileName, filePath));
                double curDistance = (CurSpeed*1000/3600) + startDistance;
                setTotalDistance(curDistance);
                FileUtil.writeSDFile(String .format("%.1f",curDistance),fileName,filePath);
            } catch (NumberFormatException e){
                Log.i(TAG,"NumberFormatException E");
            }
        }
    }

    public void startNoteSingleDistance(String filePath,String fileName,int CurSpeed,int R){
        double startDistance = 0.0;
        File file1 = new File(filePath);
        if (!file1.exists()){
            file1.mkdirs();
        }
        File file = new File(filePath + "/" + fileName);
        if (!file.exists()){
            try {
                file.createNewFile();
                FileUtil.writeSDFile("0.0",fileName,filePath);
                startDistance = Double.parseDouble(FileUtil.readSDFile(fileName, filePath));

                double curDistance = (CurSpeed*1000/3600) + startDistance;
                FileUtil.writeSDFile(String .format("%.1f",curDistance) ,fileName,filePath);
                setSingleDistance(curDistance);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                if(!StringUtils.isBlank(FileUtil.readSDFile(fileName, filePath))){
                    startDistance = Double.parseDouble(FileUtil.readSDFile(fileName, filePath));
                }else {
                    startDistance = 0;
                }
                Log.i(TAG,"startDistance: " + FileUtil.readSDFile(fileName, filePath));
                double curDistance = (CurSpeed*1000/3600) + startDistance;
                setSingleDistance(curDistance);
                FileUtil.writeSDFile(String .format("%.1f",curDistance),fileName,filePath);
            } catch (NumberFormatException e) {
                Log.i(TAG,"NumberFormatException E");
            }
        }
    }

    public void deletedSingleDistanceFile(String filePath){
        File fileDirectory;

        try {
            fileDirectory = new File(filePath);
            if (fileDirectory.exists()) {
                if (fileDirectory.isFile()) {
                    fileDirectory.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SQLiteOpenHelperUtil sqLiteOpenHelperUtil;
    private SQLiteDatabase database;

    public void initDB(){
        sqLiteOpenHelperUtil = new SQLiteOpenHelperUtil(context);
        database = sqLiteOpenHelperUtil.getWritableDatabase();
        if (isDBNULL() == 0){
            insertDB("single",0.0);
            insertDB("total",0.0);
        }
        updateDB("single",0.0,1);
    }

    public int isDBNULL(){
        Cursor c = database.rawQuery("select * from tb_distance", null);
        return c.getCount();
    }
    //插入数据
    public void insertDB(String type,double distance){
        sqLiteOpenHelperUtil.insertDB(database,type,distance);
    }

    //更新数据
    public void updateDB(String type,double distance,int mId){
        sqLiteOpenHelperUtil.UpdateDB(database,type,distance,mId);
    }
    //获取数据
    public double getDBDistance(int mId){
        return sqLiteOpenHelperUtil.QueryDB(database,mId);
    }

    public void singleDistance(int curSpeed){
        double startDistance = getDBDistance(1);
        double curDistance = (curSpeed*1000/3600 ) + startDistance;
        Log.i(TAG,"curDistance = " + curDistance + "  1000/3600 * curSpeed = " + 1000/3600 * curSpeed + " curSpeed = " + curSpeed);
        setSingleDistance(curDistance);
        updateDB("single",curDistance,1);
    }

    public void totalDistance(int curSpeed){
        double startDistance = getDBDistance(2);
        double curDistance = (curSpeed*1000/3600) + startDistance;
        Log.i(TAG,"totalDistance curDistance = " + curDistance + "  1000/3600 * curSpeed = " + curSpeed*1000/3600  + " curSpeed = " + curSpeed
                       + " startDistance = " + startDistance);
        setTotalDistance(curDistance);
        updateDB("total",curDistance,2);
    }


}
