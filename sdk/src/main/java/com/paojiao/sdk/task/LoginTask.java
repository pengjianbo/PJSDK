package com.paojiao.sdk.task;

import android.content.Context;
import android.widget.Toast;
import com.paojiao.sdk.Consts;
import com.paojiao.sdk.PJSDK;
import com.paojiao.sdk.bean.UserBean;
import com.paojiao.sdk.dialog.BaseDialog;
import com.paojiao.sdk.dialog.BindMobileTipDialog;
import com.paojiao.sdk.http.HttpListener;
import com.paojiao.sdk.http.HttpUtils;
import com.paojiao.sdk.listener.LoginListener;
import com.paojiao.sdk.utils.StringUtils;
import com.paojiao.sdk.utils.Utils;
import java.util.Map;
import org.json.JSONObject;

/**
 * Desction:登录、快速登录、注册通用TASK
 * Author:pengjianbo
 * Date:15/10/28 下午3:41
 */
public class LoginTask implements BaseTask {

    private Context context;
    private String url;
    private Map<String, String> params;
    private LoginListener loginListener;
    private BaseDialog dialog;

    public LoginTask(Context context, String url, Map<String, String> params,
            LoginListener listener, BaseDialog dialog) {
        this.context = context;
        this.url = url;
        this.params = params;
        this.loginListener = listener;
        this.dialog = dialog;
    }

    private void doLogin() {
        HttpUtils.post(url, params, new HttpListener() {
            @Override
            public void onStart() {
                super.onStart();
                dialog.buildProgressDialog().show();
            }

            @Override
            public void onSuccess(JSONObject json, String msg) {
                super.onSuccess(json, msg);

                JSONObject data = json.optJSONObject("data");
                if (data != null) {

                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

                    UserBean userBean = new UserBean();
                    userBean.setUserName(data.optString("userName"));
                    userBean.setToken(data.optString("token"));
                    userBean.setUid(data.optString("uid"));
                    userBean.setNickname(data.optString("niceName"));
                    userBean.setEmail(data.optString("email"));
                    userBean.setActiveTime(data.optString("createdTime"));
                    userBean.setCreatedTime(data.optString("activeTime"));
                    userBean.setMobile(data.optString("mobile"));
                    PJSDK.setLogined(true);

                    if (loginListener != null) {
                        loginListener.onSuccess(userBean);
                    }

                    dialog.dismiss();
                    Consts.CUR_USERNAME = userBean.getUserName();

                    Utils.saveUserToken(userBean.getUserName(), userBean.getToken());
                    Utils.updateUserNameList(userBean.getUserName());


                    FloatHotspotTask task = new FloatHotspotTask(userBean.getToken());
                    task.start();

                    //判断是否绑定手机号码
                    if( StringUtils.isEmpty(userBean.getMobile()) && PJSDK.getContext() != null) {
                        BindMobileTipDialog bindMobileTipDialog = new BindMobileTipDialog(PJSDK.getContext());
                        bindMobileTipDialog.show();
                    }

                    PJSDK.showFloatingView();
                } else {
                    onFailure("-1", "服务器异常");
                }
            }

            @Override
            public void onFailure(String code, String errorMsg) {
                super.onFailure(code, errorMsg);

                if (loginListener != null) {
                    loginListener.onFailure();
                }

                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onExcetion(String errorMsg) {
                super.onExcetion(errorMsg);
                if (loginListener != null) {
                    loginListener.onFailure();
                }
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismissProgressDialog();

            }
        });
    }

    @Override
    public void start() {
        doLogin();
    }
}
