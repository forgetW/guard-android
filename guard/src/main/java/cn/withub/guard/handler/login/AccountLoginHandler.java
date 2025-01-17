package cn.withub.guard.handler.login;

import android.text.TextUtils;
import android.view.View;

import cn.withub.guard.AccountEditText;
import cn.withub.guard.Authing;
import cn.withub.guard.LoginButton;
import cn.withub.guard.PasswordEditText;
import cn.withub.guard.R;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.network.OIDCClient;
import cn.withub.guard.util.ALog;
import cn.withub.guard.util.Util;

public class AccountLoginHandler extends AbsLoginHandler {

    public AccountLoginHandler(LoginButton loginButton, ILoginRequestCallBack callback) {
        super(loginButton, callback);
    }

    @Override
    protected boolean login() {
        View accountET = Util.findViewByClass(loginButton, AccountEditText.class);
        View passwordET = Util.findViewByClass(loginButton, PasswordEditText.class);
        if (accountET != null && accountET.isShown()
                && passwordET != null && passwordET.isShown()) {
            final String account = ((AccountEditText) accountET).getText().toString();
//            final String password = "13600000000";
            final String password = ((PasswordEditText) passwordET).getText().toString();
            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                fireCallback(accountET.getContext().getString(R.string.authing_account_or_password_empty));
                return false;
            }

            loginButton.startLoadingVisualEffect();
            loginByAccount(account, password);
            return true;
        }
        return false;
    }

    private void loginByAccount(String account, String password) {
        Authing.AuthProtocol authProtocol = getAuthProtocol();
        if (authProtocol== Authing.AuthProtocol.EInHouse) {
            AuthClient.loginByAccount(account, password, this::fireCallback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            new OIDCClient().loginByAccount(account, password, this::fireCallback);
        }
        ALog.d(TAG, "login by account");
    }
}
