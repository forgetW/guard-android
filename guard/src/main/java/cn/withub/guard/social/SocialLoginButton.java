package cn.withub.guard.social;

import static cn.withub.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.view.View;

import cn.withub.guard.AuthCallback;
import cn.withub.guard.R;
import cn.withub.guard.activity.AuthActivity;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.flow.AuthFlow;
import cn.withub.guard.flow.FlowHelper;
import cn.withub.guard.util.Const;
import cn.withub.guard.util.Util;
import cn.withub.guard.PrivacyConfirmBox;


public abstract class SocialLoginButton extends androidx.appcompat.widget.AppCompatImageButton {

    protected SocialAuthenticator authenticator;
    protected AuthCallback<UserInfo> callback;
    protected AnimatedVectorDrawable backgroundDrawable;
    protected String type;

    public SocialLoginButton(Context context) {
        this(context, null);
    }

    protected abstract SocialAuthenticator createAuthenticator();

    private void loginDone(int code, String message, UserInfo userInfo) {
        post(()->{
            backgroundDrawable.stop();
            setBackgroundResource(R.drawable.ic_authing_circle);
        });

        if (callback != null) {
            callback.call(code, message, userInfo);
        } else if (getContext() instanceof AuthActivity) {
            if (code == 200) {
                AuthActivity activity = (AuthActivity) getContext();
                Intent intent = new Intent();
                intent.putExtra("user", userInfo);
                activity.setResult(AuthActivity.OK, intent);
                activity.finish();
            } else if (code == Const.EC_MFA_REQUIRED) {
                if (getContext() instanceof AuthActivity) {
                    AuthActivity activity = (AuthActivity) getContext();
                    AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                    flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
                }
                FlowHelper.handleMFA(this, userInfo.getMfaData());
            }
        }
    }

    public SocialLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.imageButtonStyle);
    }

    public SocialLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.ic_authing_circle);
        }
        backgroundDrawable = (AnimatedVectorDrawable)context.getDrawable(R.drawable.ic_authing_animated_loading_blue);
        setOnClickListener((v -> {
            if (requiresAgreement()) {
                return;
            }
            setBackground(backgroundDrawable);
            backgroundDrawable.start();
            if (authenticator == null){
                authenticator = createAuthenticator();
            }
            authenticator.login(context, this::loginDone);
        }));
    }

    private boolean requiresAgreement() {
        View box = Util.findViewByClass(this, PrivacyConfirmBox.class);
        if (box == null) {
            return false;
        }

        return ((PrivacyConfirmBox)box).require(true);
    }

    public void setOnLoginListener(AuthCallback<UserInfo> callback) {
        this.callback = callback;
    }

    public void setAuthProtocol(AuthContainer.AuthProtocol authProtocol){
        if (authenticator != null) {
            authenticator.setAuthProtocol(authProtocol);
        }
    }

    public void setType(String type) {
        this.type = type;
    }
}
