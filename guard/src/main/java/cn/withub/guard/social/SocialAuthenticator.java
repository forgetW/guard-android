package cn.withub.guard.social;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import cn.withub.guard.AuthCallback;
import cn.withub.guard.activity.AuthActivity;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.flow.AuthFlow;

public abstract class SocialAuthenticator {

    private AuthContainer.AuthProtocol authProtocol = AuthContainer.AuthProtocol.EInHouse;

    public abstract void login(Context context, @NotNull AuthCallback<UserInfo> callback);

    protected abstract void standardLogin(String authCode, @NotNull AuthCallback<UserInfo> callback);

    protected abstract void oidcLogin(String authCode, @NotNull AuthCallback<UserInfo> callback);

    public void setAuthProtocol(AuthContainer.AuthProtocol authProtocol) {
        this.authProtocol = authProtocol;
    }

    public AuthContainer.AuthProtocol getAuthProtocol() {
        return authProtocol;
    }

    protected void login(Context context, String authCode, @NotNull AuthCallback<UserInfo> callback){
        AuthContainer.AuthProtocol authProtocol = getAuthProtocol(context);
        if (authProtocol == AuthContainer.AuthProtocol.EInHouse) {
            standardLogin(authCode, callback);
        } else if (authProtocol == AuthContainer.AuthProtocol.EOIDC) {
            oidcLogin(authCode, callback);
        }
    }

    protected AuthContainer.AuthProtocol getAuthProtocol(Context context) {
        if (!(context instanceof AuthActivity)) {
            return authProtocol;
        }

        AuthActivity activity = (AuthActivity) context;
        AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
        return flow.getAuthProtocol();
    }
}
