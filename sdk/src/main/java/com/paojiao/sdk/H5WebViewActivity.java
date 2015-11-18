package com.paojiao.sdk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.paojiao.sdk.listener.LogoutListener;
import com.paojiao.sdk.listener.PJWebViewDownLoadListener;
import com.paojiao.sdk.listener.PayListener;
import com.paojiao.sdk.utils.ResourceUtils;
import com.paojiao.sdk.utils.Utils;

/**
 * Desction:H5 webview页面
 * Author:pengjianbo
 * Date:15/10/27 下午3:43
 */
public class H5WebViewActivity extends Activity {

    public static final int TYPE_PAY = 1;//支付
    public static final int TYPE_UC = 2;//用户中心
    public static final int TYPE_BIND_MOBILE = 3;//绑定手机号
    public static final int TYPE_RESET_PWD = 4;//忘记密码

    public static final String PARAMS = "params";
    public static final String URL = "url";
    public static final String URL_TYPE = "url_type";

    private final String NAME_SPASE = "pjsdk";
    private static final int FILECHOOSER_RESULTCODE = 1;
    private String mParams;
    private String mUrl;
    private int mUrlType;

    private WebView mWebView;
    private ImageView mIvBack;
    private Button mBtnBackGame;
    private TextView mTvTitle;
    private ProgressBar mProgressBar;
    private ValueCallback<Uri> mUploadMessage;
    private boolean mHasPayResult;//是否有支付结果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(ResourceUtils.getLayoutId(this, "pj_activity_h5_webview"));

        mParams = getIntent().getStringExtra(PARAMS);
        mUrl = getIntent().getStringExtra(URL);
        mUrlType = getIntent().getIntExtra(URL_TYPE, TYPE_PAY);

        mParams += getPublicParams();//添加公共参数
        mParams += "sign=" + Utils.getSignByParams(mParams);

        mWebView = (WebView) findViewById(ResourceUtils.getId(this, "pj_webview"));
        mProgressBar = (ProgressBar) findViewById(ResourceUtils.getId(this, "pb_load"));
        mIvBack = (ImageView) findViewById(ResourceUtils.getId(this, "pj_iv_back"));
        mBtnBackGame = (Button) findViewById(ResourceUtils.getId(this, "pj_btn_back_game"));
        mTvTitle = (TextView) findViewById(ResourceUtils.getId(this, "tv_title"));

        if (mUrlType == TYPE_PAY) {
            PJSDK.hideFloatingView();
            mTvTitle.setText("支付");
            mIvBack.setVisibility(View.GONE);
        } else if (mUrlType == TYPE_BIND_MOBILE) {
            mTvTitle.setText("绑定手机");
        }

        configWebview();

        mIvBack.setOnClickListener(mBackListener);
        mBtnBackGame.setOnClickListener(mBackGameListener);

        String getUrl = mUrl + mParams;
        mWebView.loadUrl(getUrl);
    }

    private void configWebview() {
        mWebView.getSettings().setJavaScriptEnabled(true);
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
                    startActivity(intent);
                } catch (Exception e) {
                }
            }
        });

        mWebView.addJavascriptInterface(new PJJavascriptInterface(this), NAME_SPASE);
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(final WebView view,
                    final String url) {
                if (url.startsWith("tel:") || url.startsWith("weixin:")) {

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        Toast.makeText(H5WebViewActivity.this, "请先安装微信后再支付！", Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);
                if (progress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            // 关键代码，以下函数是没有API文档的，所以在Eclipse中会报错，如果添加了@Override关键字在这里的话。
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(i, "File Chooser"),
                        FILECHOOSER_RESULTCODE);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg,
                    String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                H5WebViewActivity.this.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            // For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                    String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                H5WebViewActivity.this.startActivityForResult(
                        Intent.createChooser(i, "File Chooser"),
                        H5WebViewActivity.FILECHOOSER_RESULTCODE);
            }
        });

        mWebView.setDownloadListener(new PJWebViewDownLoadListener(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) { return; }
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mUrlType == TYPE_PAY) {
            } else {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    backUp();
                }
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private View.OnClickListener mBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                if (mUrlType == TYPE_PAY && !mHasPayResult) {
                    PayListener payListener = PJSDK.getPayListener();
                    if (payListener != null) {
                        payListener.onPayCancel();
                    }
                }
                backUp();
            }
        }
    };

    private View.OnClickListener mBackGameListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mUrlType == TYPE_PAY && !mHasPayResult) {
                PayListener payListener = PJSDK.getPayListener();
                if (payListener != null) {
                    payListener.onPayCancel();
                }
            }
            backUp();
        }
    };

    public class PJJavascriptInterface {
        private Activity activity;

        public PJJavascriptInterface(Activity context) {
            this.activity = context;
        }

        /**
         * 支付成功
         */
        @android.webkit.JavascriptInterface
        public void onPaymentSuccess(final String orderNo) {
            if ( activity == null && activity.isFinishing()) {
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mHasPayResult = true;
                    PayListener payListener = PJSDK.getPayListener();
                    if (payListener != null) {
                        payListener.onPaySuccess();
                    }
                }
            });
        }

        /**
         * 支付失败
         */
        @android.webkit.JavascriptInterface
        public void onPaymentError(final int code, final String message, final String orderNo) {
            if ( activity == null && activity.isFinishing()) {
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mHasPayResult = true;
                    PayListener payListener = PJSDK.getPayListener();
                    if (payListener != null) {
                        payListener.onPayFailure();
                    }
                }
            });
        }

        @android.webkit.JavascriptInterface
        public void back2Game() {
            if ( activity == null && activity.isFinishing()) {
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    backUp();
                }
            });
        }

        /**
         * 显示标题
         *
         * @param visible 是否显示
         * @param title 标题内容
         */
        @android.webkit.JavascriptInterface
        public void setTitle(final boolean visible, final String title) {
            if ( activity == null && activity.isFinishing()) {
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTvTitle.setVisibility(visible ? View.VISIBLE : View.GONE);
                    mTvTitle.setText(title);
                }
            });
        }

        /**
         * 是否显示返回上一页的按钮
         */
        @android.webkit.JavascriptInterface
        public void showBackNavi(final boolean visible) {
            if ( activity == null && activity.isFinishing()) {
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mIvBack.setVisibility(visible ? View.VISIBLE : View.GONE);
                }
            });
        }

        /**
         * 是否显示回到游戏的按钮
         */
        @android.webkit.JavascriptInterface
        public void showBack2Game(final boolean visible) {
            if ( activity == null && activity.isFinishing()) {
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBtnBackGame.setVisibility(visible ? View.VISIBLE : View.GONE);
                }
            });
        }

        /**
         * 注销登录
         */
        @android.webkit.JavascriptInterface
        public void logout(final String uid, final String username) {
            if ( activity == null && activity.isFinishing()) {
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.saveUserToken(Consts.CUR_USERNAME, "");

                    LogoutListener logoutListener = PJSDK.getLogoutListener();
                    if (logoutListener != null) {
                        logoutListener.onLogout();
                    }

                    PJSDK.hideFloatingView();
                    finish();
                }
            });
        }
    }

    private void backUp() {
        finish();
        PJSDK.showFloatingView();
    }

    /**
     * 获取公共参数
     */
    private String getPublicParams() {
        StringBuilder params = new StringBuilder();
        params.append("udid=" + PJSDK.getUDID() + "&");
        params.append("gameId=" + PJSDK.getAppId() + "&");
        params.append("sdkVersion=" + PJSDK.SDK_VERSION + "&");
        params.append("appVersion=" + PJSDK.getAppVersion() + "&");
        params.append("channel=" + PJSDK.getChannel() + "&");
        params.append("token=" + Utils.getTokenByUserName(Consts.CUR_USERNAME) + "&");
        return params.toString();
    }
}
