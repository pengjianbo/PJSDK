package com.paojiao.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.paojiao.sdk.Consts;
import com.paojiao.sdk.PJSDK;
import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/10/22 下午3:39
 */
public class Utils {

    private static final String ALL_CHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 判断SDCard是否可用
     */
    public static boolean existSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取UDID
     */
    public static String getUDID(Context context) {
        String udid = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(udid) || udid.equals("9774d56d682e549c")
                || udid.length() < 8) {
            SecureRandom random = new SecureRandom();
            udid = new BigInteger(64, random).toString(16);
        }

        if (TextUtils.isEmpty(udid) || udid.length() < 8 || udid.length() > 16) {
            udid = getRandomString(16);
        }

        return udid;
    }

    /**
     * 随机一个自定长度的字符串
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String getRandomString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALL_CHAR.charAt(random.nextInt(length)));
        }
        return sb.toString();
    }

    /**
     * 获取屏幕大小
     */
    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;
        return new Point(screenWidth, screenHeigh);
    }

    /**
     * 获得apk版本号
     */
    public static String getVersionName(Context context) {
        String version = "";
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (StringUtils.isEmpty(version)) {
            version = "";
        }

        return version;
    }

    public static String md5(String plainText) {
        StringBuffer buf = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) { i += 256; }
                if (i < 16) { buf.append("0"); }
                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    /**
     * 从Manifest中获取配置的渠道号
     */
    public static String getChannelFromManifest(Context context) {
        Bundle metaData = null;
        String channel = "paojiao";
        if (context == null) {
            return channel;
        }
        try {
            ApplicationInfo appinfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != appinfo) {
                metaData = appinfo.metaData;
            }
            if (null != metaData) {
                channel = metaData.getString("PJ_CHANNEL");
            }
            if (TextUtils.isEmpty(channel)) {
                channel = Integer.toString(metaData.getInt("PJ_CHANNEL"));
            }
        } catch (Exception e) {
        }

        if (!StringUtils.isEmpty(channel)) {
            return channel;
        } else {
            return "paojiao";
        }
    }

    public static String getParamsSign(Map<String, String> params) {
        StringBuffer stringBuffer = new StringBuffer();
        Set<String> set = params.keySet();
        List<String> list = new ArrayList<>();
        list.addAll(set);
        Collections.sort(list);
        for (int i = 0; i < list.size(); i++) {
            stringBuffer.append(list.get(i) + "=" + params.get(list.get(i)));
        }
        stringBuffer.append("mduSfA5TgCe8xxpAqxsIFpPeWWRqCVH4");
        String md5 = md5(stringBuffer.toString());
        return md5;
    }

    public static String getSignByParams(String params) {
        if (params.contains("?") && params.length() > 5) {
            params = params.substring(params.indexOf('?') + 1);
            String str[] = params.split("&");
            if (str.length > 0) {
                List<String> list = new ArrayList<>();
                Set<String> set = new HashSet<>();
                for (int i = 0; i < str.length; i++) {
                    set.add(str[i]);
                }
                list.addAll(set);
                Collections.sort(list);
                StringBuffer stringBuffer = new StringBuffer();
                for (String string : list) {
                    stringBuffer.append(string);
                }
                stringBuffer.append("mduSfA5TgCe8xxpAqxsIFpPeWWRqCVH4");
                String md5 = md5(stringBuffer.toString());
                return md5;
            }
        }
        return null;
    }

    public static void install(Context context, File uriFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(uriFile), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 获取IMEI
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        if (StringUtils.isEmpty(imei)) {
            imei = "";
        }

        return imei;
    }

    public static String getNetType(Context context) {
        String netType = null;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) { netType = ""; }

            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info == null) { netType = ""; }

            String type = info.getTypeName();

            if (type.equalsIgnoreCase("WIFI")) {
                // WIFI
                netType = "wifi";
            } else if (type.equalsIgnoreCase("MOBILE")) {
                // GPRS
                String proxyHost = android.net.Proxy.getDefaultHost();
                if (proxyHost != null && !proxyHost.equals("")) {
                    // WAP
                    netType = "wap";
                } else {
                    netType = "net";
                }
            }
        } catch (Exception ex) {
        }
        if (netType == null || netType.length() == 0) {
            netType = "unknown";
        }
        return netType;
    }

    /**
     * 获取MAC地址
     */
    public static String getMac(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();
        if (StringUtils.isEmpty(mac)) {
            mac = "";
        }
        return mac;
    }

    /**
     * 存储用户token
     */
    public static void saveUserToken(String username, String token) {
        AppCacheUtils.get(PJSDK.getContext()).put(String.format(Consts.Cache.TOKEN_NAME, username), token);
    }

    /**
     * 根据用户获取token
     * @param username
     * @return
     */
    public static String getTokenByUserName(String username) {
        String token = AppCacheUtils.get(PJSDK.getContext()).getString(String.format(Consts.Cache.TOKEN_NAME, username));
        return token;
    }

    /**
     * 更新用户名列表
     */
    public static void updateUserNameList(String loginUserName) {
        String userNameList = AppCacheUtils.get(PJSDK.getContext()).getString(Consts.Cache.USERNAME_LIST);
        List<String> updateUnameList = new ArrayList<>();
        if (StringUtils.isEmpty(userNameList)) {
            updateUnameList.add(loginUserName);
        } else {
            String[] userList = userNameList.split(",");
            if (userList == null || userList.length == 0) {
                updateUnameList.add(loginUserName);
            } else {
                updateUnameList.add(0, loginUserName);
                for (String username : userList) {
                    if (!TextUtils.equals(username, loginUserName)) {
                        updateUnameList.add(username);
                    }
                }
            }
        }

        StringBuilder result = new StringBuilder();
        for (String name : updateUnameList) {
            result.append(name);
            result.append(",");
        }

        result.delete(result.length() - 1, result.length());
        AppCacheUtils.get(PJSDK.getContext()).put(Consts.Cache.USERNAME_LIST, result.toString());
    }

    /**
     * 获取所有登录用户名
     * @return
     */
    public static List<String> getUserNameList() {
        List<String> list = new ArrayList<>();
        String userNameList = AppCacheUtils.get(PJSDK.getContext()).getString(Consts.Cache.USERNAME_LIST);
        if (StringUtils.isEmpty(userNameList)) {
        } else {
            String[] userList = userNameList.split(",");
            if (userList == null || userList.length == 0) {
            } else {
                for (String username : userList) {
                    list.add(username);
                }
            }
        }
        return list;
    }

}
