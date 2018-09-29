package com.wind.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.wind.carlauncher.LauncherApplication;

/**
 * Created by houjian on 2018/5/13.
 */

public class VibrationDetectService extends Service {
    private static String TABLENAME = "carPositionInfo";
    private static final int SPEED_SHRESHOLD = 150;
    // 两次检测的时间间隔
    private static final int UPTATE_INTERVAL_TIME = 70;

    private static final int POSTSERV_INTERVAL_TIME = 36000;

    private static final String TAG = "VibrationDetectService";
    private SensorManager sm;

    // 手机上一个位置时重力感应坐标
    private float lastX;
    private float lastY;
    private float lastZ;

    private long lastUpdateTime;
    private long lastSendServTime;
    private LauncherApplication trackApp;
    private PowerManager mPowerManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"service oncreate");
        trackApp = (LauncherApplication) getApplicationContext();

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        int sensorType = Sensor.TYPE_ACCELEROMETER;
        sm.registerListener(sensorEventListener, sm.getDefaultSensor(sensorType), SensorManager.SENSOR_DELAY_NORMAL);

        mPowerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"service onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        sm.unregisterListener(sensorEventListener);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                long currentUpdateTime = System.currentTimeMillis();
                long timeInterval = currentUpdateTime - lastUpdateTime;

                if(timeInterval < UPTATE_INTERVAL_TIME){
                    return;
                }

                lastUpdateTime = currentUpdateTime;

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                float deltaX = x - lastX;
                float deltaY = y - lastY;
                float deltaZ = z - lastZ;

                lastX = x;
                lastY = y;
                lastZ = z;

                double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                        * deltaZ)
                        / timeInterval * 10000;

                boolean isScreenOn = mPowerManager.isScreenOn();
                Log.i(TAG, "isScreenOn = " + isScreenOn + "speed = " + speed);

                if(speed >= SPEED_SHRESHOLD && !isScreenOn){
                    Log.i(TAG, "speed > 150 ");
                    long currentSendTime =  System.currentTimeMillis();
                    long sendtimeInterval = currentSendTime - lastSendServTime;

                    if(sendtimeInterval < POSTSERV_INTERVAL_TIME){
                        return;
                    }
                    lastSendServTime = currentSendTime;
//                    postMessageToServ();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

}
