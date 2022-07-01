package cn.withub.guard.handler.register;

import android.text.TextUtils;
import android.view.View;

import cn.withub.guard.AccountEditText;
import cn.withub.guard.PasswordConfirmEditText;
import cn.withub.guard.PasswordEditText;
import cn.withub.guard.R;
import cn.withub.guard.RegisterButton;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.network.OIDCClient;
import cn.withub.guard.util.ALog;
import cn.withub.guard.util.Util;

public class EmailRegisterHandler extends AbsRegisterHandler {

    private String email;

    public EmailRegisterHandler(RegisterButton loginButton, IRegisterRequestCallBack callBack) {
        super(loginButton, callBack);
    }

    @Override
    protected boolean register() {
        View emailET = Util.findViewByClass(mRegisterButton, AccountEditText.class);
        View passwordET = Util.findViewByClass(mRegisterButton, PasswordEditText.class);
        if ((email != null || emailET != null && emailET.isShown())
                && passwordET != null && passwordET.isShown()) {
            final String account = email != null ? email : ((AccountEditText) emailET).getText().toString();
            final String password = ((PasswordEditText) passwordET).getText().toString();
            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                Util.setErrorText(mRegisterButton, "Account or password is invalid");
                fireCallback("Account or password is invalid");
                return false;
            }

            View v = Util.findViewByClass(mRegisterButton, PasswordConfirmEditText.class);
            if (v != null) {
                PasswordConfirmEditText passwordConfirmEditText = (PasswordConfirmEditText)v;
                if (!password.equals(passwordConfirmEditText.getText().toString())) {
                    Util.setErrorText(mRegisterButton, mContext.getResources().getString(R.string.authing_password_not_match));
                    fireCallback(mContext.getResources().getString(R.string.authing_password_not_match));
                    return false;
                }
            }

            mRegisterButton.startLoadingVisualEffect();
            registerByEmail(account, password);
            return true;
        }
        return false;
    }

    private void registerByEmail(String email, String password) {
        if (getAuthProtocol() == AuthContainer.AuthProtocol.EInHouse) {
            AuthClient.registerByEmail(email, password, this::fireCallback);
        } else if (getAuthProtocol() == AuthContainer.AuthProtocol.EOIDC) {
            new OIDCClient().registerByEmail(email, password, this::fireCallback);
        }
        ALog.d(TAG, "register by email");
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
