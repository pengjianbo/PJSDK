package com.paojiao.sdk.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.paojiao.sdk.utils.LoadingDialogUtils;
import com.paojiao.sdk.utils.Utils;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/10/27 下午4:31
 */
public class BaseDialog extends Dialog {

    protected Context mContext;
    protected ProgressDialog mProgressDialog;

    public BaseDialog(Context context) {
        super(context);
        init(context);
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        // 背景色透明
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        setCanceledOnTouchOutside(false);
        onLayoutCallBack();
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
            }
        });
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK | i == KeyEvent.KEYCODE_HOME) {
                    dismiss();
                }
                return false;
            }
        });
    }

    @Override
    public void setContentView(View view) {
        int[] point = onLayoutCallBack();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(point[0], point[1]);
        super.setContentView(view, params);
    }

    public int[] onLayoutCallBack(){
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40F, mContext.getResources().getDisplayMetrics());
        Point point = Utils.getScreenSize(mContext);
        int width = Math.min(point.x, point.y);
        int height = Math.max(point.x, point.y);
        lp.width = width - space; // 宽度
        lp.height = height / 2; // 高度
        dialogWindow.setAttributes(lp);
        return new int[]{lp.width, lp.height};
    }

    public ProgressDialog buildProgressDialog() {
        dismissProgressDialog();
        mProgressDialog = LoadingDialogUtils.buildProgressDialog(mContext, "请稍后…", true);
        return mProgressDialog;
    }

    public ProgressDialog buildProgressDialog(String msg) {
        dismissProgressDialog();
        mProgressDialog = LoadingDialogUtils.buildProgressDialog(mContext, msg, true);
        return mProgressDialog;
    }

    public ProgressDialog buildProgressDialog(boolean cancelable) {
        dismissProgressDialog();
        mProgressDialog = LoadingDialogUtils.buildProgressDialog(mContext, cancelable);
        return mProgressDialog;
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
