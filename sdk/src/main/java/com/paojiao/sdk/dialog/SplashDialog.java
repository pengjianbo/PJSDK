package com.paojiao.sdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import com.paojiao.sdk.PJSDK;
import com.paojiao.sdk.listener.SplashListener;
import com.paojiao.sdk.utils.ResourceUtils;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/11/7 下午5:08
 */
public class SplashDialog extends Dialog {

    private Context mContext;
    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            cancel();
        }
    };

    public SplashDialog(Context context) {
        super(context, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        mContext = context;
        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 背景色透明
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }


        setContentView(ResourceUtils.getLayoutId(context, "pj_dialog_splash"));
        mHandler = new Handler();
    }

    public void show(long delay) {
        if(mContext != null){
            super.show();
            if (delay > 0) {
                mHandler.postDelayed(mRunnable, delay);
            }
        }
    }

    @Override
    public void cancel() {
        try {
            mHandler.removeCallbacks(mRunnable);
        } catch (Exception ex) {
        }
        if (this.isShowing()) {
            super.cancel();
        }

        SplashListener splashListener = PJSDK.getSplashListener();
        if (splashListener != null) {
            splashListener.onSplashComplete();
        }
    }
}
