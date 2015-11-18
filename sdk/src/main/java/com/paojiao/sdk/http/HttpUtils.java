package com.paojiao.sdk.http;

import android.text.TextUtils;
import com.paojiao.sdk.utils.Utils;
import java.io.File;
import java.util.Map;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/10/22 下午2:45
 */
public class HttpUtils {

    public static void post(String url, Map<String, String> params, HttpListener listener) {
        if (TextUtils.isEmpty(url)) {
            if ( listener != null ) {
                listener.onExcetion("url不能为空");
                listener.onFinish();
            }
            return;
        }

        HttpTask task = new HttpTask(url, params, listener);
        task.execute();
    }

    public static void download(String url, File target, DownloadListener listener) {
        if (!Utils.existSDCard() || target == null || TextUtils.isEmpty(url)) {
            if ( listener != null ) {
                listener.onFailure(target);
                listener.onFinish();
            }
            return;
        }

        DownloadTask task = new DownloadTask(url, target, listener);
        task.execute();
    }

}
