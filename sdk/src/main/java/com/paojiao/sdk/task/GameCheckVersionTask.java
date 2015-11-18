package com.paojiao.sdk.task;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;
import com.paojiao.sdk.Consts;
import com.paojiao.sdk.PJSDK;
import com.paojiao.sdk.http.Api;
import com.paojiao.sdk.http.DownloadListener;
import com.paojiao.sdk.http.HttpListener;
import com.paojiao.sdk.http.HttpUtils;
import com.paojiao.sdk.utils.Utils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/11/7 下午4:33
 */
public class GameCheckVersionTask implements BaseTask {

    private ProgressDialog mProgressDialog;

    @Override
    public void start() {
        //判断是否有SD卡
        if (Utils.existSDCard()) {
            checkVersion();
        }
    }

    private void checkVersion() {
        Map<String, String> params = new HashMap<>();
        params.put("gameId", PJSDK.getAppId() + "");
        params.put("versionCode", PJSDK.getAppVersion());
        HttpUtils.post(Api.GAME_CHECK_VERSION, params, new HttpListener() {
            @Override
            public void onSuccess(JSONObject json, String msg) {
                super.onSuccess(json, msg);
                JSONObject data = json.optJSONObject("data");
                if (data != null) {
                    String downloadUrl = data.optString("downloadUrl");
                    String updateLog = data.optString("updateLog");
                    showUpdateDialog(downloadUrl, updateLog);
                }
            }
        });
    }

    /**
     * 显示更新选择框
     * @param downloadUrl
     * @param updateLog
     */
    private void showUpdateDialog(final String downloadUrl, final String updateLog) {
        if (!TextUtils.isEmpty(downloadUrl)) {
            AlertDialog dialog = new AlertDialog.Builder(PJSDK.getContext())
                    .setTitle("游戏更新")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(updateLog)
                    .setCancelable(false)
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.dismiss();
                                }
                            })
                    .setPositiveButton("更新",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.dismiss();
                                    downloadApk(downloadUrl, updateLog);
                                }
                            })
                    .create();
            // 设置window type
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
            } else {
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
            }

            dialog.show();
        }
    }

    /**
     * 下载文件
     * @param downloadUrl
     * @param updateLog
     */
    private void downloadApk(final String downloadUrl, final String updateLog) {
        File target = new File(Consts.CACHE_FOLDER, "game_" + PJSDK.getAppId() + "_" + PJSDK.getAppVersion() + ".apk");
        if (target.exists()) {
            target.delete();
        }

        mProgressDialog = new ProgressDialog(PJSDK.getContext());
        mProgressDialog.setMessage("正在下载,请稍后……");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        } else {
            mProgressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        mProgressDialog.show();

        HttpUtils.download(downloadUrl, target, new DownloadListener() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onProgress(int progress) {
                super.onProgress(progress);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.setProgress(progress);
                }
            }

            @Override
            public void onSuccess(File target) {
                super.onSuccess(target);
                Utils.install(PJSDK.getContext(), target);
            }

            @Override
            public void onFailure(File target) {
                super.onFailure(target);
                Toast.makeText(PJSDK.getContext(), "下载失败，请检查你的网络是否正常，确保能成功下载请到WIFI环境下进行",
                        Toast.LENGTH_SHORT).show();
                showUpdateDialog(downloadUrl, updateLog);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }
}
