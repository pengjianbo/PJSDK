package com.paojiao.sdk.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/7/20 下午5:52
 */
public class LoadingDialogUtils {

    public static ProgressDialog buildProgressDialog(Context context, String msg,
            boolean cancelble) {
        final ProgressDialog dialog = new ProgressDialog(context, ResourceUtils.getStyleId(context, "LoadingDialog"));
        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        dialog.show();
        dialog.setContentView(ResourceUtils.getLayoutId(context, "pj_layout_progressbar"));
        ImageView loading = (ImageView) dialog.findViewById(ResourceUtils.getId(context, "iv_loading"));
        AnimationDrawable anim = (AnimationDrawable) loading.getBackground();
        anim.start();
        ((TextView) dialog.findViewById(ResourceUtils.getId(context, "tv_progressbar_message"))).setText(msg);
        dialog.setCancelable(cancelble);
        return dialog;
    }

    public static ProgressDialog buildProgressDialog(Context context, boolean cancelble) {
        final ProgressDialog dialog = new ProgressDialog(context, ResourceUtils.getStyleId(context, "LoadingDialog"));
        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }

        try {
            if (context instanceof Activity) {
                if (!((Activity) context).isFinishing()) {
                    dialog.show();
                }
            } else {
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setContentView(ResourceUtils.getLayoutId(context, "pj_layout_progressbar"));
        ImageView loading = (ImageView) dialog.findViewById(ResourceUtils.getId(context, "iv_loading"));
        AnimationDrawable anim = (AnimationDrawable) loading.getBackground();
        anim.start();
        dialog.setCancelable(cancelble);
        return dialog;
    }
}
