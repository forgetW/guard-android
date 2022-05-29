package cn.withub.guard.handler.login;

import android.content.Context;

import cn.withub.guard.LoginButton;
import cn.withub.guard.activity.AuthActivity;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.flow.AuthFlow;

public abstract class AbsLoginHandler {

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

    protected void requestLogin(){
        if (!login() && null != mNextHandler){
            mNextHandler.requestLogin();
        }
    }

    abstract boolean login();

    protected void fireCallback(String message) {
        fireCallback(500, message, null);
    }

    protected void fireCallback(int code, String message, UserInfo userInfo) {
        if (null != mCallBack){
            mCallBack.callback(code, message, userInfo);
        }
    }

    protected AuthContainer.AuthProtocol getAuthProtocol() {
        if (!(mContext instanceof AuthActivity)) {
            return AuthContainer.AuthProtocol.EInHouse;
        }

        AuthActivity activity = (AuthActivity) mContext;
        AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
        return flow.getAuthProtocol();
    }

}
