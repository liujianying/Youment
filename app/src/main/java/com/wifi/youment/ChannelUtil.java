package com.wifi.youment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Trace;
import android.text.TextUtils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by liujianying on 15/9/29.
 */
public class ChannelUtil {

    private static final String CHANNEL_KEY = "channel";
    private static final String CHANNEL_DEFAULT = "offical";

    private static final String PREF_KEY_CHANNEL = "pref_key_channel";
    private static final String PREF_KEY_CHANNEL_VERSION = "pref_key_channel_version";
    private static String mChannet = null;

    public static String getChannel(Context context) {
        return getChannel(context, CHANNEL_DEFAULT);
    }


    public static String getChannel(Context context, String defauleChannel) {
        // 内存中获取
        if (!TextUtils.isEmpty(mChannet)) {
            return mChannet;
        }

        //sp中获取
        mChannet = getChannelFromSP(context);
        if (!TextUtils.isEmpty(mChannet)) {
            return mChannet;
        }


        //从apk中获取
        mChannet = getChannelForApk(context, CHANNEL_KEY);
        if (!TextUtils.isEmpty(mChannet)) {
            //保存sp中备用
            saveChannellnSp(context, mChannet);
            return mChannet;
        }

        // 全部获取失败
        return defauleChannel;
    }

    private static void saveChannellnSp(Context context, String mChannet) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(PREF_KEY_CHANNEL,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(PREF_KEY_CHANNEL_VERSION, mChannet);
        editor.apply();
    }

    private static String getChannelFromSP(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_KEY_CHANNEL,
                Activity.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_KEY_CHANNEL_VERSION, null);
    }


    private static String getChannelForApk(Context context, String channelKEY) {
        // 从apk包获取
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        String sourceDir = applicationInfo.sourceDir;
        //默认放在meta-inf 里，所以需要拼接一下
        String key = "META-INF/" + channelKEY;
        String ret = "";
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(sourceDir);
            Enumeration<?> enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) enumeration.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(key)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String[] split = ret.split("_");
        String channel = "";
        if (split != null && split.length >= 2) {
            channel = ret.substring(split[0].length() + 1);
        }
        return channel;
    }
}



















