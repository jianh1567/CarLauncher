package com.wind.carlauncher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wind.manager.TrackAndPositionManager;
import com.wind.receivers.PlayerReceiver;
import com.wind.service.VibrationDetectService;
import com.wind.utils.NetWorkUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by houjian on 2018/1/23.
 */

public class DashboardActivity extends AppCompatActivity {
    public static final String TAG = "DashboardActivity";
    private static final int SHOW_CURRENT_SPEED = 1;
    private static final int SHOW_CURRENT_TIME = 2;
    private static final int SHOW_CURRENT_TRACK = 3;
    private static final int CALCULATE_DISTANCE = 4;
    public static final int IOTMQTT_CONNECT_MESSAGE = 5;
    private static final int IOTMQTT_DISCONNECT_MESSAGE = 6;
    public static final int IOTMQTT_PUBLISH_NORMAL_MESSAGE = 7;
    private static final int IOTMQTT_PUBLISH_TROUBLE_MESSAGE = 8;
    private ImageView speedUnits;
    private ImageView speedTens;
    private ImageView hourUnits;
    private ImageView hourTens;
    private ImageView minuteUnits;
    private ImageView minuteTens;
    private ImageView mBatteryFailure;
    private ImageView mController;
    private ImageView mMotor;
    private ImageView mFaultCode;
    private ImageView mMalfunction;
    private ImageView mParkingLight;
    private ImageView mHeadlampMode;
    private ImageView mHighBeam;
    private ImageView mCruiseControl;
    private ImageView mGprsStrong;
    private ImageView mGpsStrong;
    private ImageView mLeftArrow;
    private ImageView mRightArrow;
    private TextView mAmText;
    private TextView mPmText;
    private View dsView;
    private TextView mMusicName;
    private ImageView mMileageTenThousands;
    private ImageView mMileageThousands;
    private ImageView mMileageHundreds;
    private ImageView mMileageTens;
    private ImageView mMileageOnes;
    private ImageView mMileageHundred;
    private ImageView mVolume1;
    private ImageView mVolume2;
    private ImageView mVolume3;
    private ImageView mVolume4;
    private ImageView mVolume5;
    private ImageView mVolume6;
    private ImageView mVolume7;
    private ImageView mVolume8;
    private ImageView odo;
    private ImageView trip;
    private DistanceMode currentMode;

    private Message mShowTimeMessage;

    private List<View> views = new ArrayList<>();
    private ViewPager vp_main_viewpager;
    private TelephonyManager mTelephonyManager;
    private PhoneStatListener mListener;
    private LocationManager mLocationManager;

    private List<String> mPermissionList = new ArrayList<>();
    private int mCurrentSpeed;

    private PlayerReceiver mPlayerReceiver;
    private AudioManager mAudioManager;
    private MyVolumeReceiver mVolumeReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity_main);
        vp_main_viewpager = (ViewPager) findViewById(R.id.vp_main_viewpager);
        dsView = getLayoutInflater().inflate(R.layout.dashboard_layout, null);
        views.add(dsView);

        dsView.setForeground(getDrawable(R.drawable.battery0));

        speedUnits = (ImageView) dsView.findViewById(R.id.speed_units);
        speedTens = (ImageView) dsView.findViewById(R.id.speed_tens);
        hourTens = (ImageView) dsView.findViewById(R.id.hour_tens);
        hourUnits = (ImageView) dsView.findViewById(R.id.hour_units);
        minuteTens = (ImageView) dsView.findViewById(R.id.minute_tens);
        minuteUnits = (ImageView) dsView.findViewById(R.id.minute_units);
        mBatteryFailure = (ImageView) dsView.findViewById(R.id.batteryFailure); //电池故障
        mController = (ImageView) dsView.findViewById(R.id.controller); //控制器故障
        mMotor = (ImageView) dsView.findViewById(R.id.motor); //电机故障
        mFaultCode = (ImageView) dsView.findViewById(R.id.faultCode); //故障代码
        mMalfunction = (ImageView) dsView.findViewById(R.id.malfunction); //转把故障
        mParkingLight = (ImageView) dsView.findViewById(R.id.parkingLight); //P档停车
        mHeadlampMode = (ImageView) dsView.findViewById(R.id.headlampMode); //小灯
        mHighBeam = (ImageView) dsView.findViewById(R.id.highBeam); //大灯
        mCruiseControl = (ImageView) dsView.findViewById(R.id.cruiseControl); //巡航控制
        mGprsStrong = (ImageView) dsView.findViewById(R.id.gprsStrong); //gprs信号强度
        mGpsStrong = (ImageView) dsView.findViewById(R.id.gpsStrong); //gps信号强度
        mLeftArrow = (ImageView) dsView.findViewById(R.id.leftArrow); //左转向
        mRightArrow = (ImageView) dsView.findViewById(R.id.rightArrow); //右转向
        mMileageTenThousands = (ImageView) dsView.findViewById(R.id.mileage_tenThousands);
        mMileageThousands = (ImageView) dsView.findViewById(R.id.mileage_thousands);
        mMileageHundreds = (ImageView) dsView.findViewById(R.id.mileage_hundreds);
        mMileageTens = (ImageView) dsView.findViewById(R.id.mileage_tens);
        mMileageOnes = (ImageView) dsView.findViewById(R.id.mileage_ones);
        mMileageHundred = (ImageView) dsView.findViewById(R.id.mileage_hundred);
        odo = (ImageView) dsView.findViewById(R.id.odo);
        trip = (ImageView) dsView.findViewById(R.id.trip);

        initVolumeView(dsView);

        mAmText = (TextView) dsView.findViewById(R.id.am_text);
        mPmText = (TextView) dsView.findViewById(R.id.pm_text);

        mMusicName = (TextView) dsView.findViewById(R.id.music_name);

        speedUnits.setImageLevel(0);
        speedTens.setImageLevel(0);
        showCurrentTime();

        View appView = getLayoutInflater().inflate(R.layout.homescreen, null);
        views.add(appView);

        DeviceNodeRead deviceNodeRead = new DeviceNodeRead(mHandler);

        vp_main_viewpager.setAdapter(new MyAdapter());

        //获取telephonyManager
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
       //开始监听
        mListener = new PhoneStatListener();
       //监听手机信号强度
        mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);

        //GPS信号强度监测
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE) ;

        String[] permissions = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        };

        requestPermission(permissions , 0);

        DataManager.getManager(this).setTrackNameChangeListener(
                new DataManager.TrackChangeListener(){
                    @Override
                    public void onTrackNameChange(final String trackName) {
                        Log.i("minos", "setText trackName = " + trackName);
                        Message showTrackMessage = Message.obtain();
                        showTrackMessage.what = SHOW_CURRENT_TRACK;
                        showTrackMessage.obj = trackName;
                        mHandler.sendMessage(showTrackMessage);
                    }
                }
        );

        currentMode = DistanceMode.SINGLE;
//        DataManager.getManager(this).deletedSingleDistanceFile(filePath + noteSingleDistanceFileName);
        calculateDistance();

        registerBroadCastReciver();
        int currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) ;// 当前的媒体音量
        Log.i(TAG, "oncreate currVolume = " + currVolume);
        updateVolumeUi(currVolume);

        DataManager.getManager(this).initDB();
        TrackAndPositionManager.getManager(this).initTrackAndPosition(mHandler);
        mHandler.sendEmptyMessageDelayed(IOTMQTT_CONNECT_MESSAGE,1);

        Intent serviceIntent = new Intent(this, VibrationDetectService.class);
        this.startService(serviceIntent);
    }

    private void initVolumeView(View dsView){
        mVolume1 = (ImageView) dsView.findViewById(R.id.volume1);
        mVolume2 = (ImageView) dsView.findViewById(R.id.volume2);
        mVolume3 = (ImageView) dsView.findViewById(R.id.volume3);
        mVolume4 = (ImageView) dsView.findViewById(R.id.volume4);
        mVolume5 = (ImageView) dsView.findViewById(R.id.volume5);
        mVolume6 = (ImageView) dsView.findViewById(R.id.volume6);
        mVolume7 = (ImageView) dsView.findViewById(R.id.volume7);
        mVolume8 = (ImageView) dsView.findViewById(R.id.volume8);
    }

    private void registerBroadCastReciver(){
        mPlayerReceiver = new PlayerReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.music.metachanged");
        intentFilter.addAction("com.miui.player.metachanged");
        intentFilter.addAction("com.android.music.playstatechanged");
        intentFilter.addAction("com.android.music.queuechanged");
        intentFilter.addAction("com.wind.action.carlight");
        registerReceiver(mPlayerReceiver, intentFilter);

        mVolumeReceiver = new MyVolumeReceiver();
        IntentFilter vIntentFilter = new IntentFilter();
        vIntentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(mVolumeReceiver, vIntentFilter);
    }

    /**
     * 处理音量变化时的界面显示
     * @author
     */
    private class MyVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){
                int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) ;// 当前的媒体音量
                Log.i(TAG, "max = " + max + " currVolume = " + currVolume);
                updateVolumeUi(currVolume);
            }
        }
    }

    private void updateVolumeUi(int currVolume){
        if(currVolume >= 14 && currVolume <= 15){
            mVolume1.setAlpha(1.0f);
            mVolume2.setAlpha(1.0f);
            mVolume3.setAlpha(1.0f);
            mVolume4.setAlpha(1.0f);
            mVolume5.setAlpha(1.0f);
            mVolume6.setAlpha(1.0f);
            mVolume7.setAlpha(1.0f);
            mVolume8.setAlpha(1.0f);
        }else if(currVolume >= 12 && currVolume < 14){
            mVolume1.setAlpha(1.0f);
            mVolume2.setAlpha(1.0f);
            mVolume3.setAlpha(1.0f);
            mVolume4.setAlpha(1.0f);
            mVolume5.setAlpha(1.0f);
            mVolume6.setAlpha(1.0f);
            mVolume7.setAlpha(1.0f);
            mVolume8.setAlpha(0.0f);
        }else if(currVolume >= 10 && currVolume < 12){
            mVolume1.setAlpha(1.0f);
            mVolume2.setAlpha(1.0f);
            mVolume3.setAlpha(1.0f);
            mVolume4.setAlpha(1.0f);
            mVolume5.setAlpha(1.0f);
            mVolume6.setAlpha(1.0f);
            mVolume7.setAlpha(0.0f);
            mVolume8.setAlpha(0.0f);
        }else if(currVolume >= 8 && currVolume < 10){
            mVolume1.setAlpha(1.0f);
            mVolume2.setAlpha(1.0f);
            mVolume3.setAlpha(1.0f);
            mVolume4.setAlpha(1.0f);
            mVolume5.setAlpha(1.0f);
            mVolume6.setAlpha(0.0f);
            mVolume7.setAlpha(0.0f);
            mVolume8.setAlpha(0.0f);
        }else if(currVolume >= 6 && currVolume < 8){
            mVolume1.setAlpha(1.0f);
            mVolume2.setAlpha(1.0f);
            mVolume3.setAlpha(1.0f);
            mVolume4.setAlpha(1.0f);
            mVolume5.setAlpha(0.0f);
            mVolume6.setAlpha(0.0f);
            mVolume7.setAlpha(0.0f);
            mVolume8.setAlpha(0.0f);
        }else if(currVolume >= 4 && currVolume < 6){
            mVolume1.setAlpha(1.0f);
            mVolume2.setAlpha(1.0f);
            mVolume3.setAlpha(1.0f);
            mVolume4.setAlpha(0.0f);
            mVolume5.setAlpha(0.0f);
            mVolume6.setAlpha(0.0f);
            mVolume7.setAlpha(0.0f);
            mVolume8.setAlpha(0.0f);
        }else if(currVolume >= 2 && currVolume < 4){
            mVolume1.setAlpha(1.0f);
            mVolume2.setAlpha(1.0f);
            mVolume3.setAlpha(0.0f);
            mVolume4.setAlpha(0.0f);
            mVolume5.setAlpha(0.0f);
            mVolume6.setAlpha(0.0f);
            mVolume7.setAlpha(0.0f);
            mVolume8.setAlpha(0.0f);
        }else if(currVolume == 1){
            mVolume1.setAlpha(1.0f);
            mVolume2.setAlpha(0.0f);
            mVolume3.setAlpha(0.0f);
            mVolume4.setAlpha(0.0f);
            mVolume5.setAlpha(0.0f);
            mVolume6.setAlpha(0.0f);
            mVolume7.setAlpha(0.0f);
            mVolume8.setAlpha(0.0f);
        }else if(currVolume == 0){
            mVolume1.setAlpha(0.0f);
            mVolume2.setAlpha(0.0f);
            mVolume3.setAlpha(0.0f);
            mVolume4.setAlpha(0.0f);
            mVolume5.setAlpha(0.0f);
            mVolume6.setAlpha(0.0f);
            mVolume7.setAlpha(0.0f);
            mVolume8.setAlpha(0.0f);
        }
    }

    private void calculateDistance(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(mCurrentSpeed > 0){
//                    DataManager.getManager(DashboardActivity.this).startNoteTotalDistance(filePath,noteTotalDistanceFileName,mCurrentSpeed,1);
//                    DataManager.getManager(DashboardActivity.this).startNoteSingleDistance(filePath,noteSingleDistanceFileName,mCurrentSpeed,1);
                    DataManager.getManager(DashboardActivity.this).singleDistance(mCurrentSpeed);
                    DataManager.getManager(DashboardActivity.this).totalDistance(mCurrentSpeed);

                }

//                Log.i("TAG","totalDistance: " + DataManager.getManager(DashboardActivity.this).getTotalDistance());
                mHandler.sendEmptyMessageDelayed(CALCULATE_DISTANCE,1000);
            }
        };
        new Thread(runnable).start();
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == SHOW_CURRENT_SPEED){
                Bundle bundle = (Bundle) msg.obj;

                mCurrentSpeed = bundle.getInt("currentSpeed");
                if(mCurrentSpeed < 10){
                    speedTens.setImageLevel(0);
                    speedUnits.setImageLevel(mCurrentSpeed);
                }else {
                    speedUnits.setImageLevel(mCurrentSpeed % 10);
                    speedTens.setImageLevel(mCurrentSpeed / 10);
                }

                updateDashboardUI(bundle);
            }else if(msg.what == SHOW_CURRENT_TIME) {
                mHandler.removeMessages(SHOW_CURRENT_TIME);
                showCurrentTime();
            }else if(msg.what == SHOW_CURRENT_TRACK) {
                mHandler.removeMessages(SHOW_CURRENT_TRACK);
                Log.i(TAG, "msg.obj = " + msg.obj );
                if(msg.obj == null){
                    mMusicName.setText("未知歌曲");
                }else {
                    mMusicName.setText((String)msg.obj);
                }
            }else if(msg.what == CALCULATE_DISTANCE){
                mHandler.removeMessages(CALCULATE_DISTANCE);
                String distance = "";
                switch (currentMode) {
                    case TOTAL:
                        distance = DataManager.getManager(DashboardActivity.this).getTotalDistance() + "";

                        break;
                    case SINGLE:
                        distance = DataManager.getManager(DashboardActivity.this).getSingleDistance() + "";
                        break;
                }
                Log.i("minos", "distance = " + distance);
                updateMileageUI(distance);
                calculateDistance();
            }else if (msg.what == IOTMQTT_CONNECT_MESSAGE){
                int count = 0;
                while (true){
                    if (NetWorkUtil.isNetworkConnected(DashboardActivity.this)){
                        break;
                    }else {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else if (msg.what == IOTMQTT_DISCONNECT_MESSAGE){
            }else if (msg.what == IOTMQTT_PUBLISH_NORMAL_MESSAGE){
                Map<String, Object> data = new HashMap<String, Object>();


                mHandler.sendEmptyMessageDelayed(IOTMQTT_PUBLISH_NORMAL_MESSAGE,60*1000);
            }else if (msg.what == IOTMQTT_PUBLISH_TROUBLE_MESSAGE){
                Map<String, Object> car_trouble = new HashMap<String, Object>();
            }
        }
    };

    private void updateMileageUI(String distance){
        int numLength = distance.length();
        switch (numLength) {
            case 5:
                mMileageHundred.setImageLevel(Integer.parseInt(distance.charAt(0) + ""));
                mMileageOnes.setImageLevel(0);
                mMileageTens.setImageLevel(0);
                mMileageHundreds.setImageLevel(0);
                mMileageThousands.setImageLevel(0);
                mMileageTenThousands.setImageLevel(0);
                break;
            case 6:
                mMileageHundred.setImageLevel(Integer.parseInt(distance.charAt(1) + ""));
                mMileageOnes.setImageLevel(Integer.parseInt(distance.charAt(0) + ""));
                mMileageTens.setImageLevel(0);
                mMileageHundreds.setImageLevel(0);
                mMileageThousands.setImageLevel(0);
                mMileageTenThousands.setImageLevel(0);
                break;
            case 7:
                mMileageHundred.setImageLevel(Integer.parseInt(distance.charAt(2) + ""));
                mMileageOnes.setImageLevel(Integer.parseInt(distance.charAt(1) + ""));
                mMileageTens.setImageLevel(Integer.parseInt(distance.charAt(0) + ""));
                mMileageHundreds.setImageLevel(0);
                mMileageThousands.setImageLevel(0);
                mMileageTenThousands.setImageLevel(0);
                break;
            case 8:
                mMileageHundred.setImageLevel(Integer.parseInt(distance.charAt(3) + ""));
                mMileageOnes.setImageLevel(Integer.parseInt(distance.charAt(2) + ""));
                mMileageTens.setImageLevel(Integer.parseInt(distance.charAt(1) + ""));
                mMileageHundreds.setImageLevel(Integer.parseInt(distance.charAt(0) + ""));
                mMileageThousands.setImageLevel(0);
                mMileageTenThousands.setImageLevel(0);
                break;
            case 9:
                mMileageHundred.setImageLevel(Integer.parseInt(distance.charAt(4) + ""));
                mMileageOnes.setImageLevel(Integer.parseInt(distance.charAt(3) + ""));
                mMileageTens.setImageLevel(Integer.parseInt(distance.charAt(2) + ""));
                mMileageHundreds.setImageLevel(Integer.parseInt(distance.charAt(1) + ""));
                mMileageThousands.setImageLevel(Integer.parseInt(distance.charAt(0) + ""));
                mMileageTenThousands.setImageLevel(0);
                break;
            case 10:
                mMileageHundred.setImageLevel(Integer.parseInt(distance.charAt(5) + ""));
                mMileageOnes.setImageLevel(Integer.parseInt(distance.charAt(4) + ""));
                mMileageTens.setImageLevel(Integer.parseInt(distance.charAt(3) + ""));
                mMileageHundreds.setImageLevel(Integer.parseInt(distance.charAt(2) + ""));
                mMileageThousands.setImageLevel(Integer.parseInt(distance.charAt(1) + ""));
                mMileageTenThousands.setImageLevel(Integer.parseInt(distance.charAt(0) + ""));
                break;
            default :
                mMileageHundred.setImageLevel(0);
                mMileageOnes.setImageLevel(0);
                mMileageTens.setImageLevel(0);
                mMileageHundreds.setImageLevel(0);
                mMileageThousands.setImageLevel(0);
                mMileageTenThousands.setImageLevel(0);
                break;
        }
    }

    private void requestPermission(String[] permissions, int requestCode) {
        if(Build.VERSION.SDK_INT  >= Build.VERSION_CODES.M){
            for(String permission : permissions){
                 if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                      mPermissionList.add(permission);
                 }
            }

            if(mPermissionList.isEmpty()){
                Log.i("minos", "requestPermission permission granted");
                mLocationManager.addGpsStatusListener(mGpslistener);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            }else {
                String[] permission = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(this, permission, requestCode);
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0
                     && grantResults[0] == PackageManager.PERMISSION_GRANTED
                     && grantResults[1] == PackageManager.PERMISSION_GRANTED
                     && grantResults[2] == PackageManager.PERMISSION_GRANTED
                     && grantResults[3] == PackageManager.PERMISSION_GRANTED
                     && grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                Log.i("minos", "permission granted");
                mLocationManager.addGpsStatusListener(mGpslistener);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            } else {
                // Permission Denied
                Toast.makeText(DashboardActivity.this, "您没有授权该权限，请在设置中打开授权", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private class PhoneStatListener extends PhoneStateListener {
        //获取信号强度
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            //获取网络信号强度
            //获取0-4的5种信号级别，越大信号越好,但是api23开始才能用
            int level = signalStrength.getLevel();
            int gsmSignalStrength = signalStrength.getGsmSignalStrength();
            String signalInfo = signalStrength.toString();

            String[] params = signalInfo.split(" ");

            Log.i("minos", "mTelephonyManager.getNetworkType() = " + mTelephonyManager.getNetworkType());

            if (mTelephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
                int Itedbm = Integer.parseInt(params[9]);
                Log.i("minos", "level = " + level + "gsmSignalStrength = " + gsmSignalStrength + "Itedbm = " + Itedbm);
                if (Itedbm > -100) {
                    mGprsStrong.setImageResource(R.drawable.gprs_strong);
                } else {
                    mGprsStrong.setImageResource(R.drawable.gprs_weak);
                }
            } else if (mTelephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
                mGprsStrong.setImageResource(R.drawable.gprs_no);
            }
        }
    }

    private LocationListener locationListener=new LocationListener() {
        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i("minos", "当前GPS状态为可见状态");
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i("minos", "当前GPS状态为服务区外状态");
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i("minos", "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            @SuppressLint("MissingPermission") Location location=mLocationManager.getLastKnownLocation(provider);
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
        }
    };

    @SuppressLint("MissingPermission")
    GpsStatus.Listener mGpslistener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            Log.i("minos", "event = " + event);
            switch (event) {             //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i("minos", "第一次定位");
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.i("minos", "卫星状态改变");
                    GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites=gpsStatus.getMaxSatellites();
                    //创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    Log.i("minos", " maxSatellites = " + maxSatellites);
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        if (s.getSnr() > 10) {
                            count++;
                        }
                        Log.i("minos", " count = " + count + " s.getSnr() = " + s.getSnr());
                    }

                    if (count >= 4) {
                        mGpsStrong.setImageResource(R.drawable.gps_strong);
                    }else if( count < 4 && count >= 1){
                        mGpsStrong.setImageResource(R.drawable.gps_weak);
                    }else {
                        mGpsStrong.setImageResource(R.drawable.gps_no);
                    }

                    break;//定位启动
            case GpsStatus.GPS_EVENT_STARTED:
            Log.i("minos", "定位启动");
            break;
            //定位结束
            case GpsStatus.GPS_EVENT_STOPPED:
            Log.i("minos", "定位结束");
            break;
        }
    };
};


   private void showCurrentTime(){
        long time = System.currentTimeMillis();
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);

        int mAmOrPm = mCalendar.get(Calendar.AM_PM);
        int mHour = mCalendar.get(Calendar.HOUR);
        int mMinute = mCalendar.get(Calendar.MINUTE);

        if(mAmOrPm == Calendar.AM){
            mAmText.setVisibility(View.VISIBLE);
            mPmText.setVisibility(View.INVISIBLE);
        }else if(mAmOrPm == Calendar.PM){
            mAmText.setVisibility(View.INVISIBLE);
            mPmText.setVisibility(View.VISIBLE);
        }

        if(mHour < 10){
            hourTens.setImageLevel(0);
            hourUnits.setImageLevel(mHour);
        }else {
            hourTens.setImageLevel(mHour / 10);
            hourUnits.setImageLevel(mHour % 10);
        }

        if(mMinute < 10){
            minuteTens.setImageLevel(0);
            minuteUnits.setImageLevel(mMinute);
        }else {
            minuteTens.setImageLevel(mMinute / 10);
            minuteUnits.setImageLevel(mMinute % 10);
        }

        mShowTimeMessage = Message.obtain();
        mShowTimeMessage.what = SHOW_CURRENT_TIME;
        mHandler.sendMessageDelayed(mShowTimeMessage, 1000);
    }

    private void updateDashboardUI(Bundle bundle){
        int batteryFailure = bundle.getInt("batteryFailure");
        int controller = bundle.getInt("controller");
        int motor = bundle.getInt("motor");
        int faultCode = bundle.getInt("faultCode");
        int malfunction = bundle.getInt("malfunction");
        int parkingLight = bundle.getInt("parkingLight");
        int cruiseControl = bundle.getInt("cruiseControl");
        int battery = bundle.getInt("battery");
        int highBeam = bundle.getInt("highBeam");
        int leftArrow = bundle.getInt("leftArrow");
        int rightArrow = bundle.getInt("rightArrow");


        if (batteryFailure == 1 || controller == 1 || motor == 1
                || faultCode == 1 || malfunction == 1 || parkingLight == 1
                || cruiseControl == 1 || highBeam == 1 || battery < 20){

            mHandler.sendEmptyMessageDelayed(IOTMQTT_PUBLISH_TROUBLE_MESSAGE,1);
        }

        updateViewStatus(mBatteryFailure, batteryFailure);
        updateViewStatus(mController, controller);
        updateViewStatus(mMotor, motor);
        updateViewStatus(mFaultCode, faultCode);
        updateViewStatus(mMalfunction, malfunction);
        updateViewStatus(mParkingLight, parkingLight);
        updateViewStatus(mCruiseControl, cruiseControl);
        updateViewStatus(mHighBeam, highBeam);
        updateViewStatus(mHeadlampMode, 0);

        if(leftArrow == 1){
            mLeftArrow.setImageResource(R.drawable.left_arrow_green);
        }else if(leftArrow == 0){
            mLeftArrow.setImageResource(R.drawable.left_arrow);
        }

        if(rightArrow == 1){
            mRightArrow.setImageResource(R.drawable.right_arrow_green);
        }else if(rightArrow == 0){
            mRightArrow.setImageResource(R.drawable.right_arrow);
        }

        updateBatteryView(battery);
    }

    private void updateBatteryView(int battery){
        if(battery < 5){
            dsView.setForeground(getDrawable(R.drawable.battery0));
        }else if(battery >= 5 && battery < 20){
            dsView.setForeground(getDrawable(R.drawable.battery1));
        }else if(battery >= 20 && battery < 30){
            dsView.setForeground(getDrawable(R.drawable.battery2));
        }else if(battery >= 30 && battery < 50){
            dsView.setForeground(getDrawable(R.drawable.battery3));
        }else if(battery >= 50 && battery < 70){
            dsView.setForeground(getDrawable(R.drawable.battery4));
        }else if(battery >= 70 && battery < 80){
            dsView.setForeground(getDrawable(R.drawable.battery5));
        }else if(battery >= 80 && battery < 90){
            dsView.setForeground(getDrawable(R.drawable.battery6));
        }else if(battery >= 90){
            dsView.setForeground(getDrawable(R.drawable.battery7));
        }
    }

    private void updateViewStatus(ImageView view, int visible){
        if(visible == 1){
            view.setVisibility(View.VISIBLE);
        }else if(visible == 0){
            view.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public static boolean isInstallApk(Context context, String name) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageInfo.packageName.equals(name)) {
                return true;
            } else {
                continue;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean isActionMain = Intent.ACTION_MAIN.equals(intent.getAction());

        Log.i(TAG, "onNewIntent isActionMain = " + isActionMain);
        if(isActionMain){
            vp_main_viewpager.setCurrentItem(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_NONE);
        unregisterReceiver(mPlayerReceiver);
        unregisterReceiver(mVolumeReceiver);
        TrackAndPositionManager.getManager(this).stopTrackAndPosition();
    }

    class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v=views.get(position);
            container.addView(v);

            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
             View v=views.get(position);
             //前一张图片划过后删除该View
             container.removeView(v);
         }
    }

    enum DistanceMode {
        SINGLE, TOTAL
    }

    public void onClickVolume1(View view){
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,1,0);
    }

    public void onClickVolume2(View view){
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,3,0);
    }

    public void onClickVolume3(View view){
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,5,0);
    }

    public void onClickVolume4(View view){
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,7,0);
    }

    public void onClickVolume5(View view){
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,9,0);
    }

    public void onClickVolume6(View view){
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,11,0);
    }

    public void onClickVolume7(View view){
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,13,0);
    }

    public void onClickVolume8(View view){
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,15,0);
    }

    public void onClickOdo(View view){
        currentMode = DistanceMode.TOTAL;
        String distance = "";
        distance = DataManager.getManager(DashboardActivity.this).getTotalDistance() + "";
        Log.i(TAG,"onClickOdo distance = " + distance);
        updateMileageUI(distance);
    }

    public void onClickTrip(View view){
        currentMode = DistanceMode.SINGLE;
        String distance = "";
        distance = DataManager.getManager(DashboardActivity.this).getSingleDistance() + "";
        Log.i(TAG,"onClickTrip distance = " + distance);
        updateMileageUI(distance);
    }
}
