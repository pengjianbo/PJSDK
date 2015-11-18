package com.paojiao.sdk.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import com.paojiao.sdk.utils.AppCacheUtils;
import com.paojiao.sdk.utils.ResourceUtils;
import com.paojiao.sdk.utils.Utils;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/10/28 下午2:46
 */
public class FloatHotspotDialog extends BaseDialog {

    private Button mBtnNoTips;
    private Button mBtnClose;
    private WebView mWebView;

    private String mUrl;

    public FloatHotspotDialog(Context context) {
        super(context);
        int layoutId = ResourceUtils.getLayoutId(context, "pj_dialog_float_hotspot");
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        setContentView(view);

        mBtnNoTips = (Button) findViewById(ResourceUtils.getId(context, "pj_btn_no_tips"));
        mBtnClose = (Button) findViewById(ResourceUtils.getId(context, "pj_btn_close"));
        mWebView = (WebView) findViewById(ResourceUtils.getId(context, "pj_webview"));

        mBtnNoTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !TextUtils.isEmpty(mUrl) ) {
                    String key = Utils.md5(mUrl);
                    AppCacheUtils.get(mContext).put(key, false);
                }
                dismiss();
            }
        });
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        configWebView();
    }

    public void show(String url, String params) {
        show();
        this.mUrl = url;
        String hotspotUrl = url + params;
        mWebView.loadUrl(hotspotUrl);
    }

    private void configWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true); // 设置显示缩放按钮
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);//自适应屏幕
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    getContext().startActivity(intent);
                } catch (Exception e) {
                }
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    getContext().startActivity(intent);
                } catch (Exception e) {
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }
}
