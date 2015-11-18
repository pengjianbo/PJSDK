package com.paojiao.sdk.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.paojiao.sdk.PJSDK;
import com.paojiao.sdk.bean.RoleInfo;
import com.paojiao.sdk.bean.UserBean;
import com.paojiao.sdk.http.HttpListener;
import com.paojiao.sdk.listener.LoginListener;
import com.paojiao.sdk.listener.PayListener;
import com.paojiao.sdk.listener.SplashListener;

public class MainActivity extends AppCompatActivity {

    /** 泡椒提供给合作方的gameId或appId */
    public static final int APP_ID = 10;
    /** 泡椒提供给合作方的privateKey或appKey */
    public static final String APP_KEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAOM6rlce79Mg2qX7ilajVEKAuR7qCdGF+DQrMafTjbwob6MhSZBp/+g0yPA1QEbWnGnZc4iMj16wxYN36sKGYq22bbNvmqMYK2e+8bpKPEpxdqN2pZ6LamXB5D1FLsUmpnKNXPfeV2PNTmGDEMA6PRNs+a7oriXouWcSsByyzCXDAgMBAAECgYEAzFFqjDnsKarEEDWzOZ4ZWMcILi0BcR4GhVpFhwOp0YOGhmebDzjQ+8ni2GL5jfjRojf+M8036UwH/ePo+sr9lEW0K5B/DWj/tpQ1VpO5hNuiV4IpzaU7h7n87h8k0LpBTaYSyYF7pi0ylw0FcwDAkMOvzhmUXdetv5YZaVKUejkCQQD3D267jIv/JtuBrt5izgNcCrH5o+F8+bpoFjpkZQH4Ck2Tly5SIAPtCAiTFGHk343vojlgq/LygKYEyr0LV9G3AkEA63OMq8W1F5RMsUll4Vb7QlgiEl9YEzfNxaa4wtxzqBmZ8ASZBHIt4jENDw2uGL1IcZRSipUqjjka11HWi+icVQJBAJUvdng9+FOMjUQT6inTqOetdab/NKwYyF0N/xicfcHfxtR2l/vzZSt+jF70EeD4tWacmPUtH7kbwiRmoBz3XKUCQDyv38Y2g2K8ergHbxcR0FldVgreQFlTvNoBCwZOxBDD6IWQ6c/XjjQK6I7Eu+bsgsujq1LE998IoP52vZH6NrECQQCQP4EAAu9f1mW7+pXg9psc7arYtFwzKfYArDc3rKHMRpbTiwBCUXZGyy/mfSW6Onu70EJsG44ePxy8ytm1KZ+9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        PJSDK.initialize(this, APP_ID, APP_KEY, true)
                .setSplashListener(new SplashListener() {
                    @Override
                    public void onSplashComplete() {

                    }
                });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PJSDK.doLogin(new LoginListener() {
                    @Override
                    public void onSuccess(UserBean user) {
                        super.onSuccess(user);

                        RoleInfo roleInfo = new RoleInfo("胡哥哥", 69, "才高八斗", 7554);
                        PJSDK.uploadPlayerInfo(roleInfo, new HttpListener() {

                        });
                    }

                    @Override
                    public void onFailure() {
                        super.onFailure();
                    }

                });
            }
        });
        Button btnPay = (Button) findViewById(R.id.btn_pay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 订单标题，如：购买100元宝
                String subject = "购买1个元宝";
                // 订单价格，单位RMB元，浮点类型
                float price = 0.1f;
                // 合作方自定义参数，一般为订单号
                String ext = "NO123456789";
                // 该订单的备注信息
                String remark = "订单备注信息";
                PJSDK.doPay(subject, price, remark, ext, new PayListener() {

                    @Override
                    public void onPaySuccess() {
                        Toast.makeText(MainActivity.this, "pay success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPayFailure() {
                        Toast.makeText(MainActivity.this, "pay failure ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPayCancel() {
                        Toast.makeText(MainActivity.this, "pay cancel", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PJSDK.destroy();
    }
}
