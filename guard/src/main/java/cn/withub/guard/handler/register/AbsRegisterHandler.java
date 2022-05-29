package cn.withub.guard.handler.register;

import android.content.Context;

import cn.withub.guard.RegisterButton;
import cn.withub.guard.activity.AuthActivity;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.flow.AuthFlow;

public abstract class AbsRegisterHandler {

    protected static final String TAG = AbsRegisterHandler.class.getSimpleName();
    protected AbsRegisterHandler mNextHandler;
    protected RegisterButton mRegisterButton;
    protected final Context mContext;
    protected IRegisterRequestCallBack mCallBack;

    public AbsRegisterHandler(RegisterButton registerButton, IRegisterRequestCallBack callBack) {
        this.mRegisterButton = registerButton;
        this.mCallBack = callBack;
        this.mContext = registerButton.getContext();
    }

    protected void setNextHandler(AbsRegisterHandler loginHandler) {
        mNextHandler = loginHandler;
    }

    protected void requestRegister(){
        if (!register() && null != mNextHandler){
            mNextHandler.requestRegister();
        }
    }

    abstract boolean register();

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
