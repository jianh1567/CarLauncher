package com.wind.manager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.trace.api.fence.FenceAlarmPushInfo;
import com.baidu.trace.api.fence.MonitoredAction;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.wind.carlauncher.LauncherApplication;
import com.wind.receivers.TrackReceiver;
import com.wind.utils.CommonUtil;

/**
 * Created by houjian on 2018/6/26.
 */

public class TrackAndPositionManager {
    private static final String TAG = "TrackAndPositionManager";
    private static TrackAndPositionManager mTrackManager;
    private LauncherApplication trackApp;
    private Context mContext;
    private boolean isTest = true;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private double mLastLat = 0.0;
    private double mLastLon = 0.0;
    private boolean isFirstIn = true;

    /**
     * 轨迹服务监听器
     */
    private OnTraceListener traceListener = null;

    private PowerManager.WakeLock wakeLock = null;

    private TrackReceiver trackReceiver = null;
    private PowerManager powerManager = null;
    private Handler mHandler;


    public LocationClient mLocationClient = null;

    public TrackAndPositionManager(Context context) {
        mContext = context;
    }

    public static TrackAndPositionManager getManager(Context context){
          if(mTrackManager == null){
              mTrackManager = new TrackAndPositionManager(context);
          }
          return  mTrackManager;
    }

    public void initTrackAndPosition(Handler handler){
        this.mHandler = handler;
        initTrackService();
        initPositionService();
    }

    public void initTrackService(){
        trackApp = (LauncherApplication) mContext.getApplicationContext();
        powerManager = (PowerManager) trackApp.getSystemService(mContext.POWER_SERVICE);

        trackApp.entityName = getWlanId();
        trackApp.initTrace(trackApp.entityName);

        initListener();
        trackApp.mClient.startTrace(trackApp.mTrace, traceListener);
    }

    public void initPositionService(){
        mLocationClient = new LocationClient(mContext.getApplicationContext());
        initLocationOption();
        //声明LocationClient类
        mLocationClient.registerLocationListener(new MyLocationListener());
        mLocationClient.start();
    }

    private void initLocationOption(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setWifiCacheTimeOut(5*60*1000);
        option.setEnableSimulateGps(false);

        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            if (location == null ) {
                return;
            }
            boolean isNeedPost = false;
            mCurrentLat = location.getLatitude();    //定位的当前位置维度 31.168754  经度 121.41196
            mCurrentLon = location.getLongitude();

            if(mLastLat != mCurrentLat
                    || mLastLon != mCurrentLon){
                mLastLat = mCurrentLat;
                mLastLon = mCurrentLon;
                isNeedPost = true;
            }

            Log.i("minos", "mCurrentLat = " + mCurrentLat + " mCurrentLon = " + mCurrentLon + " isNeedPost = " + isNeedPost);
        }
    }

    public void stopTrackAndPosition(){
        stopTrackService();
        stopPositionService();
    }

    public void stopTrackService(){
        trackApp.mClient.stopGather(traceListener);
        trackApp.mClient.stopTrace(trackApp.mTrace, traceListener);
    }

    public void stopPositionService(){
        mLocationClient.stop();
    }

    private String getWlanId(){
        WifiManager wm = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
        String wlanMac = wm.getConnectionInfo().getMacAddress();
        wlanMac = wlanMac.replace(":", "");
        Log.i(TAG, "wlanMac = " + wlanMac);
        return wlanMac;
    }

    private void initListener(){
        traceListener = new OnTraceListener() {

            /**
             * 绑定服务回调接口
             * @param errorNo  状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功 </pre>
             *                <pre>1：失败</pre>
             */
            @Override
            public void onBindServiceCallback(int errorNo, String message) {
                Log.i(TAG, "onBindServiceCallback errorNo = " + errorNo);
                showToastMessage(String.format("onBindServiceCallback, errorNo:%d, message:%s ", errorNo, message), isTest);
            }

            /**
             * 开启服务回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功 </pre>
             *                <pre>10000：请求发送失败</pre>
             *                <pre>10001：服务开启失败</pre>
             *                <pre>10002：参数错误</pre>
             *                <pre>10003：网络连接失败</pre>
             *                <pre>10004：网络未开启</pre>
             *                <pre>10005：服务正在开启</pre>
             *                <pre>10006：服务已开启</pre>
             */
            @Override
            public void onStartTraceCallback(int errorNo, String message) {
                Log.i(TAG, "onStartTraceCallback errorNo = " + errorNo);
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.START_TRACE_NETWORK_CONNECT_FAILED <= errorNo) {
                    registerReceiver();
                }

                if(StatusCodes.SUCCESS == errorNo || StatusCodes.TRACE_STARTED == errorNo){
                    trackApp.mClient.startGather(traceListener);
                }
                showToastMessage(String.format("onStartTraceCallback, errorNo:%d, message:%s ", errorNo, message), isTest);
            }

            /**
             * 停止服务回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>11000：请求发送失败</pre>
             *                <pre>11001：服务停止失败</pre>
             *                <pre>11002：服务未开启</pre>
             *                <pre>11003：服务正在停止</pre>
             */
            @Override
            public void onStopTraceCallback(int errorNo, String message) {
                Log.i(TAG, "onStopTraceCallback errorNo = " + errorNo);
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.CACHE_TRACK_NOT_UPLOAD == errorNo) {
                    unregisterPowerReceiver();
                }
                showToastMessage(String.format("onStopTraceCallback, errorNo:%d, message:%s ", errorNo, message), isTest);
            }

            /**
             * 开启采集回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>12000：请求发送失败</pre>
             *                <pre>12001：采集开启失败</pre>
             *                <pre>12002：服务未开启</pre>
             */
            @Override
            public void onStartGatherCallback(int errorNo, String message) {
                Log.i(TAG, "onStartGatherCallback errorNo = " + errorNo);
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.GATHER_STARTED == errorNo) {
                }
                showToastMessage(String.format("onStartGatherCallback, errorNo:%d, message:%s ", errorNo, message), isTest);
            }

            /**
             * 停止采集回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>13000：请求发送失败</pre>
             *                <pre>13001：采集停止失败</pre>
             *                <pre>13002：服务未开启</pre>
             */
            @Override
            public void onStopGatherCallback(int errorNo, String message) {
                Log.i(TAG, "onStopGatherCallback errorNo = " + errorNo);
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.GATHER_STOPPED == errorNo) {
                }
                showToastMessage(String.format("onStopGatherCallback, errorNo:%d, message:%s ", errorNo, message), isTest);
            }

            /**
             * 推送消息回调接口
             *
             * @param messageType 状态码
             * @param pushMessage 消息
             *                  <p>
             *                  <pre>0x01：配置下发</pre>
             *                  <pre>0x02：语音消息</pre>
             *                  <pre>0x03：服务端围栏报警消息</pre>
             *                  <pre>0x04：本地围栏报警消息</pre>
             *                  <pre>0x05~0x40：系统预留</pre>
             *                  <pre>0x41~0xFF：开发者自定义</pre>
             */
            @Override
            public void onPushCallback(byte messageType, PushMessage pushMessage) {
                if (messageType < 0x03 || messageType > 0x04) {
                    showToastMessage(pushMessage.getMessage(), isTest);
                    return;
                }
                FenceAlarmPushInfo alarmPushInfo = pushMessage.getFenceAlarmPushInfo();
                if (null == alarmPushInfo) {
                    showToastMessage(String.format("onPushCallback, messageType:%d, messageContent:%s ", messageType,
                                                      pushMessage), isTest);
                    return;
                }
                StringBuffer alarmInfo = new StringBuffer();
                alarmInfo.append("您的车辆于")
                        .append(CommonUtil.getHMS(alarmPushInfo.getCurrentPoint().getLocTime() * 1000))
                        .append(alarmPushInfo.getMonitoredAction() == MonitoredAction.enter ? "进入" : "离开")
                        .append(messageType == 0x03 ? "云端" : "本地")
                        .append("围栏：").append(alarmPushInfo.getFenceName());

            }

            @Override
            public void onInitBOSCallback(int errorNo, String message) {
                Log.i(TAG, "onInitBOSCallback errorNo = " + errorNo);
                showToastMessage(String.format("onInitBOSCallback, errorNo:%d, message:%s ", errorNo, message), isTest);
            }
        };
    }

    /**
     * 注册广播（电源锁、GPS状态）
     */
    private void registerReceiver() {
        if (trackApp.isRegisterReceiver) {
            return;
        }

        if (null == wakeLock) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "track upload");
        }
        if (null == trackReceiver) {
            trackReceiver = new TrackReceiver(wakeLock);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(StatusCodes.GPS_STATUS_ACTION);
        trackApp.registerReceiver(trackReceiver, filter);
        trackApp.isRegisterReceiver = true;
    }

    private void unregisterPowerReceiver() {
        if (!trackApp.isRegisterReceiver) {
            return;
        }
        if (null != trackReceiver) {
            trackApp.unregisterReceiver(trackReceiver);
        }
        trackApp.isRegisterReceiver = false;
    }

    private void showToastMessage(final String message, boolean isTest){
        if(isTest){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
