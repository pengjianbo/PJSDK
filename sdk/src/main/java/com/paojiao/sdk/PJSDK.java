package com.paojiao.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import com.paojiao.sdk.bean.RoleInfo;
import com.paojiao.sdk.dialog.LoginDialog;
import com.paojiao.sdk.dialog.SplashDialog;
import com.paojiao.sdk.http.Api;
import com.paojiao.sdk.http.HttpListener;
import com.paojiao.sdk.http.HttpUtils;
import com.paojiao.sdk.listener.LoginListener;
import com.paojiao.sdk.listener.LogoutListener;
import com.paojiao.sdk.listener.PayListener;
import com.paojiao.sdk.listener.SplashListener;
import com.paojiao.sdk.service.FloatViewService;
import com.paojiao.sdk.task.GameCheckVersionTask;
import com.paojiao.sdk.utils.AppCacheUtils;
import com.paojiao.sdk.utils.StringUtils;
import com.paojiao.sdk.utils.Utils;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/10/22 下午5:13
 */
public class PJSDK {

    public static final String SDK_VERSION = "3.1";

    private static Context mContext;
    private static int mAppId;
    private static String mAppKey;
    private static boolean mShowSplash;
    private static String mUDID;
    private static FloatViewService mFloatViewService;
    private static PayListener mPayListener;
    private static LogoutListener mLogoutListener;
    private static SplashListener mSplashListener;
    private static boolean mLogined;

    private static PJSDK mPJSDK;

    private PJSDK() {
    }

    public static PJSDK initialize(Context context, int appId, String appKey) {
        return initialize(context, appId, appKey, false);
    }

    public static PJSDK initialize(Context context, int appId, String appKey, boolean showSplash) {
        if (mPJSDK != null) {
            return mPJSDK;
        }

        mContext = context;
        mAppId = appId;
        mAppKey = appKey;
        mShowSplash = showSplash;
        mPJSDK = new PJSDK();

        if (showSplash) {
            mPJSDK.showSplash();
        }

        try {
            mContext.bindService(new Intent(mContext, FloatViewService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
        }

        //初始化UDID
        initUDID();

        //注册广播
        //mContext.registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        if (mShowSplash) {
            SplashDialog splashDialog = new SplashDialog(mContext);
            splashDialog.show(3000);
        }

        //检查游戏版本更新
        GameCheckVersionTask checkVersionTask = new GameCheckVersionTask();
        checkVersionTask.start();

        //统计
        mPJSDK.statSDK();

        return mPJSDK;
    }

    private void showSplash() {

    }

    /**
     * 登录泡椒
     */
    public static void doLogin(LoginListener listener) {
        if (mContext == null || mAppId == 0 || StringUtils.isEmpty(mAppKey) || mPJSDK == null) {
            return;
        }

        LoginDialog dialog = new LoginDialog(mContext, listener);
        dialog.show();
    }

    /**
     * 用户中心
     */
    public static void doUCenter() {
        if (mFloatViewService == null) {
            return;
        }

        Intent intent = new Intent(mContext, H5WebViewActivity.class);
        intent.putExtra(H5WebViewActivity.URL, Api.UCENTER_URL);
        intent.putExtra(H5WebViewActivity.PARAMS, "?");
        intent.putExtra(H5WebViewActivity.URL_TYPE, H5WebViewActivity.TYPE_UC);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * 支付
     */
    public static void doPay(String subject, float price, String remark, String ext, PayListener listener) {
        if ( !mLogined ) {
            doLogin(null);
            return;
        }

        if (mFloatViewService == null) {
            return;
        }

        mPayListener = listener;

        if (!TextUtils.isEmpty(remark)) {
            remark = remark.replace("#", ",");
        }
        String params = MessageFormat.format("?subject={0}&price={1}&ext={2}&remark={3}", subject, price, ext, remark);
        Intent intent = new Intent(mContext, H5WebViewActivity.class);
        intent.putExtra(H5WebViewActivity.URL, Api.PAY);
        intent.putExtra(H5WebViewActivity.PARAMS, params);
        intent.putExtra(H5WebViewActivity.URL_TYPE, H5WebViewActivity.TYPE_PAY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * 获取支付回调
     */
    public static PayListener getPayListener() {
        return mPayListener;
    }

    /**
     * 设置退出登录事件
     */
    public static void setLogoutListener(LogoutListener listener) {
        mLogoutListener = listener;
    }

    /**
     * 设置是否登录
     */
    public static void setLogined(boolean logined) {
        mLogined = logined;
    }

    /**
     *
     * @return
     */
    public static LogoutListener getLogoutListener() {
        return mLogoutListener;
    }

    /**
     * 设置Splash事件回调
     */
    public static void setSplashListener(SplashListener listener) {
        mSplashListener = listener;
    }

    public static SplashListener getSplashListener() {
        return mSplashListener;
    }

    /**
     * 显示悬浮图标
     */
    public static void showFloatingView() {
        if (mFloatViewService != null && mLogined) {
            mFloatViewService.showFloat();
        }
    }

    /**
     * 隐藏悬浮图标
     */
    public static void hideFloatingView() {
        if (mFloatViewService != null) {
            mFloatViewService.hideFloat();
        }
    }

    /**
     * 释放PJSDK数据
     */
    public static void destroy() {
        mPayListener = null;
        mLogined = false;
        try {
            mContext.unbindService(mServiceConnection);

            //if( mFloatViewService != null ) {
            //    mFloatViewService.destroyFloat();
            //}

            //mContext.unregisterReceiver(mHomeKeyEventReceiver);
        } catch (Exception e) {
        } finally {
            mFloatViewService = null;
            mPJSDK = null;
        }
    }

    private static void initUDID() {
        String udid = AppCacheUtils.get(mContext).getString(Consts.Cache.UDID);
        if (TextUtils.isEmpty(udid) || udid.length() < 8 || udid.length() > 16) {
            udid = Utils.getUDID(mContext);
            AppCacheUtils.get(mContext).put(Consts.Cache.UDID, udid);
        }
        mUDID = udid;
    }

    public static int getAppId() {
        return mAppId;
    }

    public static String getUDID() {
        if (StringUtils.isEmpty(mUDID)) {
            initUDID();
        }
        return mUDID;
    }

    public static String getAppVersion() {
        if (mContext == null) {
            return "1.0";
        }
        return Utils.getVersionName(mContext);
    }

    /**
     * 获取渠道名称
     */
    public static String getChannel() {
        if (mContext == null) {
            return "paojiao";
        }
        return Utils.getChannelFromManifest(mContext);
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * 连接到Service
     */
    private final static ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mFloatViewService = ((FloatViewService.FloatViewServiceBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mFloatViewService = null;
        }
    };

    /**
     * 监听是否点击了home键将客户端推到后台
     */
    //private static BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
    //    String SYSTEM_REASON = "reason";
    //    String SYSTEM_HOME_KEY = "homekey";
    //    String SYSTEM_HOME_KEY_LONG = "recentapps";
    //
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        String action = intent.getAction();
    //        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
    //            String reason = intent.getStringExtra(SYSTEM_REASON);
    //            if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
    //                //表示按了home键,程序到了后台
    //                PJSDK.hideFloatingView();
    //            } else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
    //                //表示长按home键,显示最近使用的程序列表
    //            }
    //        }
    //    }
    //};

    /**
     * SDK启动统计
     */
    private void statSDK() {
        Map<String, String> params = new HashMap<>();
        params.put("imei", Utils.getIMEI(mContext));
        params.put("net_type", Utils.getNetType(mContext));
        params.put("mode", Build.MODEL);
        params.put("sdk", Build.VERSION.RELEASE);
        params.put("productVersion", SDK_VERSION);
        params.put("mac", Utils.getMac(mContext));
        params.put("product", "paojiao_sdk");
        params.put("cid", getChannel());
        HttpUtils.post(Api.STAT_URL, params, null);
    }

    /**
     * 上传玩家信息
     *
     * @param roleInfo 玩家信息
     * @param listener 请求回调
     */
    public static void uploadPlayerInfo(RoleInfo roleInfo, HttpListener listener) {
        if ( !mLogined ) {
            doLogin(null);
            return;
        }
        //基础数据缺失，不予上报
        if (roleInfo == null || roleInfo.isEmpty()) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("roleName", roleInfo.getRoleName());
        params.put("roleLever", roleInfo.getRoleLevel() + "");
        params.put("roleServer", roleInfo.getRoleServer());
        params.put("roleMoney", roleInfo.getRoleMoney() + "");
        params.put("extra", "");
        HttpUtils.post(Api.EXIT_STAT_URL, params, listener);
    }
}
