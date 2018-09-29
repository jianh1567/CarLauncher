package com.wind.utils;

import android.util.Log;

/**
 * Created by w010003593 on 2018/4/10.
 */

public class Logger {

    private static final boolean isOpen = true;

    public static void V(String tag,String message){
        if (isOpen){
            Log.v(tag,message);
        }
    }
    public static void D(String tag,String message){
        if (isOpen){
            Log.d(tag,message);
        }
    }
    public static void I(String tag,String message){
        if (isOpen){
            Log.i(tag,message);
        }
    }
    public static void W(String tag,String message){
        if (isOpen){
            Log.w(tag,message);
        }
    }
    public static void E(String tag,String message){
        if (isOpen){
            Log.e(tag,message);
        }
    }

    public static void log(){

    }

}
