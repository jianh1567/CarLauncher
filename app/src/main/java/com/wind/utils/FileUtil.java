package com.wind.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by w010003593 on 2018/4/10.
 */

public class FileUtil {

    public static void writeSDFile(String str, String fileName, String filePath) {
        try {
            FileOutputStream output = new FileOutputStream(filePath + "//" + fileName,false);
            byte[] buffer = str.getBytes("UTF-8");
            output.write(buffer);
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeSDFile2(String str, String fileName, String filePath) {
        try {
            FileOutputStream output = new FileOutputStream(filePath + "//" + fileName,false);
            byte[] buffer = str.getBytes("UTF-8");
            output.write(buffer);
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readSDFile(String fileName, String filePath) {
        StringBuffer sb = new StringBuffer();
        File file = new File(filePath + "//" + fileName);
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        Log.i("wangtongming","result.toString().trim() = " + result.toString().trim());
        return result.toString().trim();
    }

    public static void recoverBackupFile(String filepath) {
        try {
            String xmlpathbak = filepath + ".bak";
            File bakfile = new File(xmlpathbak);
            if (!bakfile.exists())
                return;

            File file = new File(filepath);
            if (file.exists())
                file.delete();

            bakfile.renameTo(file);
        } catch (Exception e) {

        }
    }

    public static void backupFile(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists())
                return;
            File bakfile = new File(filepath + ".bak");
            if (bakfile.exists()) {
                bakfile.delete();
            }
            file.renameTo(bakfile);
        } catch (Exception e) {

        }
    }

    public static boolean deleteFile(String file) {
        File fileDirectory;

        try {
            fileDirectory = new File(file);
            if (fileDirectory.exists()) {
                if (fileDirectory.isFile()) {
                    fileDirectory.delete();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void modifyFile(File file, String auth) {
        Process process = null;
        try {
            // File tmpFile = file.getParentFile();
            // process = Runtime.getRuntime().exec(
            // "chmod 777 " + tmpFile.getAbsolutePath());
            // process.waitFor();
            if (auth == null)
                auth = "777";
            String command = "chmod " + auth + " " + file.getAbsolutePath();
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec(command);
            process.waitFor();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }
}
