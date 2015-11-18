package com.paojiao.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.paojiao.sdk.utils.ResourceUtils;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/11/7 下午3:11
 */
public class CustomerServicesActivity extends Activity {
    private ImageView mIvBack;
    private Button mBtnToGame;
    private TextView mTvQQ;
    private TextView mTvQQ2;
    private TextView mTvPhone;
    private TextView mTvPhone2;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(ResourceUtils.getLayoutId(this, "pj_activity_customer_services"));

        mIvBack = (ImageView) findViewById(ResourceUtils.getId(this, "pj_iv_back"));
        mBtnToGame = (Button) findViewById(ResourceUtils.getId(this, "pj_btn_back_game"));
        mTvQQ = (TextView) findViewById(ResourceUtils.getId(this, "tv_qq"));
        mTvQQ2 = (TextView) findViewById(ResourceUtils.getId(this, "tv_qq2"));
        mTvPhone = (TextView) findViewById(ResourceUtils.getId(this, "tv_phone"));
        mTvPhone2 = (TextView) findViewById(ResourceUtils.getId(this, "tv_phone2"));

        initData();
        setListener();
    }

    protected void setListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        mBtnToGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        mTvQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                copy("2265380001");
                Toast.makeText(CustomerServicesActivity.this, "QQ号已复制到剪切板", Toast.LENGTH_SHORT).show();
            }
        });

        mTvQQ2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                copy("2272941924");
                Toast.makeText(CustomerServicesActivity.this, "QQ号已复制到剪切板", Toast.LENGTH_SHORT).show();
            }
        });

        mTvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                callPhone("0755-61679905");
            }
        });
        mTvPhone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                callPhone("0775-61534622");
            }
        });
    }

    protected void initData() {
        mTvQQ.setText(Html.fromHtml("客服QQ1：<font color='#c30202'>2265380001</font>"));
        mTvQQ2.setText(Html.fromHtml("客服QQ2：<font color='#c30202'>2272941924</font>"));
        mTvPhone.setText(Html.fromHtml("客服电话1：<font color='#c30202'>0755-61679905</font>"));
        mTvPhone2.setText(Html.fromHtml("客服电话2：<font color='#c30202'>0775-61534622</font>"));
    }

    /**
     * 实现文本复制功能
     */
    private void copy(String content) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    /**
     * 调用拨号页面
     * @param phone
     */
    private void callPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
