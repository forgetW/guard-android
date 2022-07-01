package cn.withub.guard.handler.register;

import android.text.TextUtils;
import android.view.View;

import cn.withub.guard.AccountEditText;
import cn.withub.guard.RegisterButton;
import cn.withub.guard.VerifyCodeEditText;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.network.OIDCClient;
import cn.withub.guard.util.ALog;
import cn.withub.guard.util.Util;


public class EmailCodeRegisterHandler extends AbsRegisterHandler {


    public EmailCodeRegisterHandler(RegisterButton loginButton, IRegisterRequestCallBack callBack) {
        super(loginButton, callBack);
    }

    @Override
    protected boolean register() {
        View emailET = Util.findViewByClass(mRegisterButton, AccountEditText.class);
        View verifyCodeET = Util.findViewByClass(mRegisterButton, VerifyCodeEditText.class);
        if (emailET != null && emailET.isShown()
                && verifyCodeET != null && verifyCodeET.isShown()) {
            final String account = ((AccountEditText) emailET).getText().toString();
            final String verifyCode = ((VerifyCodeEditText) verifyCodeET).getText().toString();
            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(verifyCode)) {
                Util.setErrorText(mRegisterButton, "Email or verifyCode is invalid");
                fireCallback("Email or verifyCode is invalid");
                return false;
            }

            mRegisterButton.startLoadingVisualEffect();
            registerByEmailCode(account, verifyCode);
            return true;
        }
        return false;
    }

    private void registerByEmailCode(String email, String verifyCode) {
        if (getAuthProtocol() == AuthContainer.AuthProtocol.EInHouse) {
            AuthClient.registerByEmailCode(email, verifyCode, this::fireCallback);
        } else if (getAuthProtocol() == AuthContainer.AuthProtocol.EOIDC) {
            new OIDCClient().registerByEmailCode(email, verifyCode, this::fireCallback);
        }
        ALog.d(TAG, "register by email code");
    }

}
