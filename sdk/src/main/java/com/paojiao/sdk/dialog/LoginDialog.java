package com.paojiao.sdk.dialog;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.paojiao.sdk.Consts;
import com.paojiao.sdk.H5WebViewActivity;
import com.paojiao.sdk.http.Api;
import com.paojiao.sdk.listener.LoginListener;
import com.paojiao.sdk.task.LoginTask;
import com.paojiao.sdk.utils.FormVerifyUtils;
import com.paojiao.sdk.utils.ResourceUtils;
import com.paojiao.sdk.utils.StringUtils;
import com.paojiao.sdk.utils.Utils;
import com.paojiao.sdk.widget.FullAutoCompleteTextView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Desction:登录对话框
 * Author:pengjianbo
 * Date:15/10/27 下午4:30
 */
public class LoginDialog extends BaseDialog {

    private LoginListener mLoginListener;

    private Button mBtnLogin;
    private Button mBtnReg;
    private Button mBtnQuickLogin;
    private FullAutoCompleteTextView mEtUsername;
    private EditText mEtPassword;
    private TextView mTvForgetPwd;

    private List<String> mUserNameList;
    private ArrayAdapter<String> mUserNameAdapter;

    public LoginDialog(Context context, LoginListener listener) {
        super(context);
        setContentView(ResourceUtils.getLayoutId(context, "pj_dialog_login"));
        this.mLoginListener = listener;

        mBtnLogin = (Button) findViewById(ResourceUtils.getId(context, "pj_login_do_button"));
        mBtnReg = (Button) findViewById(ResourceUtils.getId(context, "pj_btn_reg"));
        mBtnQuickLogin = (Button) findViewById(ResourceUtils.getId(context, "pj_login_quick_reg_button"));
        mEtUsername = (FullAutoCompleteTextView) findViewById(ResourceUtils.getId(context, "pj_login_username_edittext"));
        mEtPassword = (EditText) findViewById(ResourceUtils.getId(context, "pj_login_password_editText"));
        mTvForgetPwd = (TextView) findViewById(ResourceUtils.getId(context, "pj_login_forget_pwd_textView"));

        mBtnLogin.setOnClickListener(mLoginClickListener);
        mBtnReg.setOnClickListener(mRegClickListener);
        mBtnQuickLogin.setOnClickListener(mQuickLoginClickListener);
        mTvForgetPwd.setOnClickListener(mForgetClickListener);

        mUserNameList = Utils.getUserNameList();
        if (mUserNameList.size() > 0) {
            String lastLoginUsername = mUserNameList.get(0);
            mEtUsername.setText(lastLoginUsername);
            mEtPassword.setText(Consts.LOGINED_DEFAULT_PASSWORD);
        }
        mUserNameAdapter = new ArrayAdapter<>(context, ResourceUtils.getLayoutId(context, "pj_adapter_account_choose_item"), mUserNameList);
        mEtUsername.setAdapter(mUserNameAdapter);
        mEtUsername.setOnItemClickListener(mAccountChooseItemListener);
        mEtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtils.isEmpty(editable.toString())) {
                    mEtPassword.setText("");
                }

            }
        });

    }

    private View.OnClickListener mLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String username = mEtUsername.getText().toString();
            String password = mEtPassword.getText().toString();

            if (!FormVerifyUtils.checkUserName(username)) {
                Toast.makeText(mContext, "请输入6-16位用户名", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!FormVerifyUtils.checkPassword(password)) {
                Toast.makeText(mContext, "请输入6-30位密码", Toast.LENGTH_SHORT).show();
                return;
            }

            String token = Utils.getTokenByUserName(username);
            //判断是登录还是token验证
            if (mUserNameList.contains(username)
                    && TextUtils.equals(password, Consts.LOGINED_DEFAULT_PASSWORD)
                    && !StringUtils.isEmpty(token)) {
                doTokenVerify(token);
            } else {
                doLogin(false, username, password);
            }
        }
    };

    private View.OnClickListener mRegClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RegisterDialog dialog = new RegisterDialog(mContext, mLoginListener);
            dialog.show();

            dismiss();
        }
    };

    private View.OnClickListener mQuickLoginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            doLogin(true, null, null);
        }
    };

    //忘记密码
    private View.OnClickListener mForgetClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, H5WebViewActivity.class);
            intent.putExtra(H5WebViewActivity.URL_TYPE, H5WebViewActivity.TYPE_RESET_PWD);
            intent.putExtra(H5WebViewActivity.URL, Api.RESET_PWD);
            intent.putExtra(H5WebViewActivity.PARAMS, "?");
            mContext.startActivity(intent);
            dismiss();
        }
    };

    private AdapterView.OnItemClickListener mAccountChooseItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String username = mUserNameList.get(position);
            mEtUsername.setText(username);
            mEtPassword.setText(Consts.LOGINED_DEFAULT_PASSWORD);
        }
    };

    private void doLogin(boolean isQuickLogin, String username, String password) {
        Map<String, String> params = new HashMap<>();
        String apiUrl = Api.LOGIN;
        if (!isQuickLogin) {
            params.put("userName", username);
            params.put("password", password);
        } else {
            apiUrl = Api.QUICK_LOGIN;
        }

        LoginTask task = new LoginTask(mContext, apiUrl, params, mLoginListener, this);
        task.start();
    }

    private void doTokenVerify(String token) {
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        LoginTask task = new LoginTask(mContext, Api.TOKEN_VERIFY, params, mLoginListener, this);
        task.start();
    }
}
