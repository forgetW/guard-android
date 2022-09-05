package cn.withub.guard.handler.login;

import android.text.TextUtils;
import android.view.View;

<<<<<<< HEAD:guard/src/main/java/cn/withub/guard/handler/login/EmailCodeLoginHandler.java
import cn.withub.guard.EmailEditText;
import cn.withub.guard.LoginButton;
import cn.withub.guard.R;
import cn.withub.guard.VerifyCodeEditText;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.network.OIDCClient;
import cn.withub.guard.util.ALog;
import cn.withub.guard.util.Util;
=======
import cn.authing.guard.Authing;
import cn.authing.guard.EmailEditText;
import cn.authing.guard.LoginButton;
import cn.authing.guard.R;
import cn.authing.guard.VerifyCodeEditText;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.network.OIDCClient;
import cn.authing.guard.util.ALog;
import cn.authing.guard.util.Util;
>>>>>>> authing/master:guard/src/main/java/cn/authing/guard/handler/login/EmailCodeLoginHandler.java

public class EmailCodeLoginHandler extends AbsLoginHandler{

    private String emailNumber;
    private String emailCode;

    public EmailCodeLoginHandler(LoginButton loginButton, ILoginRequestCallBack callback) {
        super(loginButton, callback);
    }

    @Override
    protected boolean login() {
        View emailNumberET = Util.findViewByClass(loginButton, EmailEditText.class);
        View emailCodeET = Util.findViewByClass(loginButton, VerifyCodeEditText.class);
        if (emailNumberET != null && emailNumberET.isShown()) {
            EmailEditText emailNumberEditText = (EmailEditText)emailNumberET;
            emailNumber = emailNumberEditText.getText().toString();
        }
        if (emailCodeET != null && emailCodeET.isShown()) {
            VerifyCodeEditText verifyCodeEditText = (VerifyCodeEditText)emailCodeET;
            emailCode = verifyCodeEditText.getText().toString();
        }
        if (!TextUtils.isEmpty(emailNumber) && !TextUtils.isEmpty(emailCode)) {
            loginButton.startLoadingVisualEffect();
            loginByEmailCode(emailNumber, emailCode);
            return true;
        }

        if (emailNumberET != null && emailNumberET.isShown()
                && emailCodeET != null && emailCodeET.isShown()) {
            EmailEditText emailNumberEditText = (EmailEditText)emailNumberET;
            if (!emailNumberEditText.isContentValid()) {
                fireCallback(mContext.getString(R.string.authing_invalid_phone_number));
                return false;
            }

            final String email = emailNumberEditText.getText().toString();
            final String code = ((VerifyCodeEditText) emailCodeET).getText().toString();
            if (TextUtils.isEmpty(code)) {
                fireCallback(mContext.getString(R.string.authing_incorrect_verify_code));
                return false;
            }

            loginButton.startLoadingVisualEffect();
            loginByEmailCode(email, code);
            return true;
        }
        return false;
    }

    private void loginByEmailCode(String email, String verifyCode) {
        Authing.AuthProtocol authProtocol = getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            AuthClient.loginByEmailCode(email, verifyCode, this::fireCallback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            new OIDCClient().loginByEmailCode(email, verifyCode, this::fireCallback);
        }
        ALog.d(TAG, "login by email code");
    }
}
