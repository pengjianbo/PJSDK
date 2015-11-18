package com.paojiao.sdk.http;

import android.os.AsyncTask;
import android.text.TextUtils;
import com.paojiao.sdk.Consts;
import com.paojiao.sdk.PJSDK;
import com.paojiao.sdk.utils.StringUtils;
import com.paojiao.sdk.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/10/22 下午2:52
 */
public class HttpTask extends AsyncTask<Void, Void, String>{

    private String url;
    private Map<String, String> params;
    private HttpListener listener;

    public HttpTask(String url, Map<String, String> params, HttpListener listener) {
        this.url = url;
        this.params = params;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if ( listener != null ) {
            listener.onStart();
        }
        if ( params == null ) {
            params = new HashMap<>();
        }

        params.put("udid", PJSDK.getUDID());
        params.put("gameId", PJSDK.getAppId()+"");
        params.put("sdkVersion", PJSDK.SDK_VERSION);
        params.put("appVersion", PJSDK.getAppVersion());
        params.put("channel", PJSDK.getChannel());
        params.put("sign", Utils.getParamsSign(params));
    }

    @Override
    protected String doInBackground(Void... voids) {
        String result = null;
        try {
            HttpRequest http = HttpRequest.post(url)
                    .readTimeout(30000)
                    .connectTimeout(30000)
                    .form(params);
            if (http.ok()) {
                String body = http.body();
                if (!TextUtils.isEmpty(body)) {
                    return body;
                }
            }
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (Consts.DEBUG) {
            System.out.println("url=" + url + " params=" + params.toString() + "\nresponse=" + result);
        }

        if (TextUtils.isEmpty(result)) {
            if ( listener != null ) {
                listener.onExcetion("网络异常，请稍后再试");
                listener.onFinish();
            }
            return;
        }
        try {
            JSONObject json = new JSONObject(result);
            if ( json != null ) {
                String code = json.optString("code");
                String msg = json.optString("msg");
                if (StringUtils.isEmpty(msg)) {
                    msg = "网络异常，请稍后再试";
                }
                if (TextUtils.equals(code, "1")) {
                    if (listener != null) {
                        listener.onSuccess(json, msg);
                    }
                } else {
                    if (listener != null) {
                        listener.onFailure(code, msg);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            if ( listener != null ) {
                listener.onExcetion("网络异常，请稍后再试");
            }
        }

        if ( listener != null ) {
            listener.onFinish();
        }
    }


}
