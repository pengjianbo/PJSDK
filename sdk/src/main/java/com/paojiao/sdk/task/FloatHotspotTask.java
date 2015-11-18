package com.paojiao.sdk.task;

import com.paojiao.sdk.PJSDK;
import com.paojiao.sdk.dialog.FloatHotspotDialog;
import com.paojiao.sdk.http.Api;
import com.paojiao.sdk.http.HttpListener;
import com.paojiao.sdk.http.HttpUtils;
import com.paojiao.sdk.utils.AppCacheUtils;
import com.paojiao.sdk.utils.Utils;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/10/28 下午3:38
 */
public class FloatHotspotTask implements BaseTask {

    private String token;

    public FloatHotspotTask(String token) {
        this.token = token;
    }

    private void doFloatHotspot() {
        Map<String, String> params = new HashMap<>();
        params.put("gameId", PJSDK.getAppId()+"");
        params.put("token", token);
        HttpUtils.post(Api.FLOAT_HOTSPOT, params, new HttpListener() {
            @Override
            public void onSuccess(JSONObject json, String msg) {
                super.onSuccess(json, msg);
                JSONObject data = json.optJSONObject("data");
                if (data != null) {
                    String url = data.optString("url");
                    String key = Utils.md5(url);
                    boolean isShow = AppCacheUtils.get(PJSDK.getContext()).getBoolean(key, true);
                    if (isShow) {
                        String params = "&gameId=" + PJSDK.getAppId() + "&token=" + token;
                        FloatHotspotDialog dialog = new FloatHotspotDialog(PJSDK.getContext());
                        dialog.show(url, params);
                    }
                }
            }
        });
    }

    @Override
    public void start() {
        doFloatHotspot();
    }
}
