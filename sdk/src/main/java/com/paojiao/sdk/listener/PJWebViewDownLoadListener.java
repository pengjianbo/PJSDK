package com.paojiao.sdk.listener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.DownloadListener;
import android.widget.Toast;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/10/28 下午6:24
 */
public class PJWebViewDownLoadListener implements DownloadListener {
    private Context context;

    public PJWebViewDownLoadListener( Context context){
        this.context=context;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
            long contentLength) {
        if(TextUtils.isEmpty(url)){
            Toast.makeText(context, "该应无法下载，链接地址为空", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            context.startActivity(intent);
        }

    }
}
