package com.dazhi.meitutest.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 14-2-26.
 */
public class Utils {
    /**
     * 判断是否有网络连接
     * 需要添加权限
     * @return
     */
    public static boolean isNetWork(Context context) {
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        // 访问网络状态需要权限 ACCESS_NETWORK_STATE
        boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if (wifi | internet) {
            //执行相关操作
            return true;
        } else {
            Toast.makeText(context,"网络无法连接",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 从服务器读取文本数据
     * @param in
     * @return
     */
    public static String readServiceText(InputStream in) throws IOException {
        String str = "";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (in != null) {
            byte[] buf = new byte[1024]; // 一次读取1k的数据
            int len = 0;
            try {
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                str = out.toString("UTF-8");
                out.close();
                in.close();
                Log.d("readServiceText", "----读取到的数据-----:" + str);
            } catch (IOException e) {
                throw e;
            }
        }
        return str;
    }

    /**
     * 向文件写数据
     * @param dir 目标文件
     * @param in 输入流对象
     * @return
     */
    public static boolean writeDataToFile(String dir, InputStream in) throws IOException{
        boolean flag = false;

        Log.d("writeDataToFile","filepath:" + dir);
        File file = new File(dir);
        if(! file.getParentFile().exists()){
           file.getParentFile().mkdirs();
        }
        FileOutputStream out = new FileOutputStream(file);

        byte[] buf = new byte[1024];
        int len = 0;
        try {
            if(null != in) {
                while((len = in.read(buf)) != -1){
                    out.write(buf, 0 , len);
                }
                flag = true;
            }
        } catch (IOException e) {
            Log.d("writeDataToFile", "读取文件失败" ,e);
            throw e;
        } finally {
            try {
                if(in != null){ in.close();}
                out.flush();
                out.close();
            } catch (IOException e) {
                throw e;
            }
        }
        return flag;
    }

    /**
     * 获取SDCard路径
     * @return
     */
    public static File getSDPath() {
        /*
        file_path = new File(getSDPath().getPath() + "/" + System.currentTimeMillis() + ".jpg");
        Uri uri = Uri.fromFile(file);
        imageView.setImageURI(uri);
        */
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir;
    }

    /**
     * copy File
     * @param srcPath 源文件
     * @param dirPath 目标文件
     * @return
     */
    public static boolean copyFile(String srcPath, String dirPath){
        boolean b = false;
        File srcFile = new File(srcPath);
        File dirFile = new File(dirPath);
        if(!dirFile.getParentFile().exists()){
            dirFile.getParentFile().mkdirs();
        }
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
             in = new DataInputStream(new BufferedInputStream(new FileInputStream(srcFile)));
             out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dirFile)));
             byte[] buf = new byte[1024];
             int len = 0;
             while( (len = in.read(buf)) != -1){
                 out.write(buf, 0 ,len);
             }
            out.flush();
            b = true;
        } catch (FileNotFoundException e) {
            Log.e("copyFile", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("copyFile", e.getMessage(), e);
        } finally {
            try {
                if(in != null){ in.close();}
                if(out != null){out.close();}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if(file.exists()) {
           file.delete();
        }
    }

    /**
     * 遍历一个文件夹下面的所有文件，一级文件
     */
    public static  List<File> traverseSingle(String filePath) {
        List<File> allFilePath = new ArrayList<File>();
        File dst = new File(filePath);
        if(dst != null && dst.exists()){
           File[] files = dst.listFiles();
           if(files != null){
               allFilePath = Arrays.asList(files); // 把数组转换为list
           }
        }
        return allFilePath;
    }

}
