package com.wind.carlauncher;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by houjian on 2018/1/26.
 */

public class DeviceNodeRead {
    private static final String TAG = "DeviceNodeRead";
    private Handler mHandler;
    private int speed;
    private int battery;

    public DeviceNodeRead(Handler handler){
         mHandler = handler;
         Thread t = new Thread(new UpdateUIThread());
         t.start();
    }

    private String getNodeString(String path){
        String prop = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            prop = bufferedReader.readLine();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return  prop;
    }

    public static String read(String sys_path){
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("cat " + sys_path);

            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line ;
            while (null != (line = br.readLine())) {
                Log.i(TAG, "read data ---> " + line);
                return line;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "*** ERROR *** Here is what I know: " + e.getMessage());
        }
        return null;
    }

    private class UpdateUIThread implements Runnable {
        public void run() {
            while (true){
                Bundle bundle = new Bundle();
                String path = "/sys/class/controller/controller/val";
                File file = new File(path);
                Log.i("minos", "path = " + path + " file.exists() = " + file.exists());
                if (!file.exists()){
                    speed = speed + 6;
                    if(speed > 40){
                        speed  = 0;
                    }

                    battery = battery + 6;
                    if(battery > 100){
                        battery = 0;
                    }

                    bundle.putInt("batteryFailure",1);
                    bundle.putInt("controller", 1);
                    bundle.putInt("motor", 1);
                    bundle.putInt("faultCode", 1);
                    bundle.putInt("malfunction", 1);
                    bundle.putInt("parkingLight",1);
                    bundle.putInt("cruiseControl",  1);
                    bundle.putInt("battery", 1);
                    bundle.putInt("currentSpeed", 1);
                    bundle.putInt("highBeam", 1);
                    bundle.putInt("leftArrow", 1);
                    bundle.putInt("rightArrow", 1);
                    bundle.putInt("currentSpeed", speed);
                    bundle.putInt("battery", battery);

                    Message message = new Message();
                    message.obj = bundle;
                    message.what = 1;

                    mHandler.sendMessage(message);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    continue;

                }else{
                    String nodeInfo = getNodeString("/sys/class/controller/controller/val");
                    Log.i(TAG,"----->nodeInfo: " + nodeInfo);
                    String[] splitInfo = nodeInfo.split(",");

//                    if(splitInfo.length > 4){
//                        bundle.putInt("batteryFailure",Integer.parseInt(splitInfo[0]));
//                        bundle.putInt("controller", Integer.parseInt(splitInfo[1]));
//                        bundle.putInt("motor", Integer.parseInt(splitInfo[2]));
//                        bundle.putInt("faultCode", Integer.parseInt(splitInfo[3]));
//                        bundle.putInt("malfunction", Integer.parseInt(splitInfo[4]));
//                        bundle.putInt("parkingLight", Integer.parseInt(splitInfo[5]));
//                        bundle.putInt("cruiseControl",  Integer.parseInt(splitInfo[6]));
//                        bundle.putInt("battery", Integer.parseInt(splitInfo[7]));
//                        bundle.putInt("currentSpeed", Integer.parseInt(splitInfo[8]));
//                        bundle.putInt("highBeam", Integer.parseInt(splitInfo[9]));
//                        bundle.putInt("leftArrow", Integer.parseInt(splitInfo[10]));
//                        bundle.putInt("rightArrow", Integer.parseInt(splitInfo[11]));
//                    }else {
                        speed = speed + 6;
                        if(speed > 40){
                            speed  = 0;
                        }

                        battery = battery + 6;
                        if(battery > 100){
                            battery = 0;
                        }

                    bundle.putInt("batteryFailure",1);
                    bundle.putInt("controller", 1);
                    bundle.putInt("motor", 1);
                    bundle.putInt("faultCode", 1);
                    bundle.putInt("malfunction", 1);
                    bundle.putInt("parkingLight",1);
                    bundle.putInt("cruiseControl",  1);
                    bundle.putInt("battery", 1);
                    bundle.putInt("currentSpeed", 1);
                    bundle.putInt("highBeam", 1);
                    bundle.putInt("leftArrow", 1);
                    bundle.putInt("rightArrow", 1);
                    bundle.putInt("currentSpeed", speed);
                    bundle.putInt("battery", battery);
//                    }

                    Message message = new Message();
                    message.obj = bundle;
                    message.what = 1;

                    mHandler.sendMessage(message);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
