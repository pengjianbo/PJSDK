package com.paojiao.sdk.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import com.paojiao.sdk.H5WebViewActivity;
import com.paojiao.sdk.http.Api;
import com.paojiao.sdk.utils.ResourceUtils;

/**
 * Desction:绑定手机号提示对话框
 * Author:pengjianbo
 * Date:15/10/27 下午8:44
 */
public class BindMobileTipDialog extends BaseDialog {

    private Button mBtnNoBind;
    private Button mBtnBind;

    public BindMobileTipDialog(Context context) {
        super(context);
        setContentView(ResourceUtils.getLayoutId(context, "pj_dialog_bind_mobile_tip"));
        mBtnNoBind = (Button) findViewById(ResourceUtils.getId(context, "pj_btn_nobind"));
        mBtnBind = (Button) findViewById(ResourceUtils.getId(context, "pj_btn_bind"));
        mBtnNoBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mBtnBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, H5WebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(H5WebViewActivity.URL_TYPE, H5WebViewActivity.TYPE_BIND_MOBILE);
                intent.putExtra(H5WebViewActivity.URL, Api.BIND_MOBILE);
                intent.putExtra(H5WebViewActivity.PARAMS, "?");
                mContext.startActivity(intent);
                dismiss();
            }
        });
    }
}
