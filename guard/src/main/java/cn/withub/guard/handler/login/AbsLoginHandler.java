package cn.withub.guard.handler.login;

import android.content.Context;

import cn.withub.guard.LoginButton;
import cn.withub.guard.activity.AuthActivity;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.flow.AuthFlow;
import cn.withub.guard.handler.BaseHandler;

public abstract class AbsLoginHandler extends BaseHandler {

    protected static final String TAG = AbsLoginHandler.class.getSimpleName();
    protected AbsLoginHandler mNextHandler;
    protected LoginButton loginButton;
    protected final Context mContext;
    protected ILoginRequestCallBack mCallBack;

    public AbsLoginHandler(LoginButton loginButton, ILoginRequestCallBack callback) {
        this.loginButton = loginButton;
        this.mCallBack = callback;
        this.mContext = loginButton.getContext();
    }

    protected void setNextHandler(AbsLoginHandler loginHandler) {
        mNextHandler = loginHandler;
    }

    protected void requestLogin() {
        if (!login() && null != mNextHandler) {
            mNextHandler.requestLogin();
        }
    }

    abstract boolean login();

    protected void fireCallback(String message) {
        fireCallback(500, message, null);
    }

    protected void fireCallback(int code, String message, UserInfo userInfo) {
        if (null != mCallBack) {
            mCallBack.callback(code, message, userInfo);
        }
    }

}
