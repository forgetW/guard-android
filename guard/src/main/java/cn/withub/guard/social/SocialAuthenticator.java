package cn.withub.guard.social;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

<<<<<<< HEAD:guard/src/main/java/cn/withub/guard/social/SocialAuthenticator.java
import cn.withub.guard.AuthCallback;
import cn.withub.guard.activity.AuthActivity;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.flow.AuthFlow;
=======
import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.handler.BaseHandler;
>>>>>>> authing/master:guard/src/main/java/cn/authing/guard/social/SocialAuthenticator.java

public abstract class SocialAuthenticator extends BaseHandler {

    public abstract void login(Context context, @NotNull AuthCallback<UserInfo> callback);

    protected abstract void standardLogin(String authCode, @NotNull AuthCallback<UserInfo> callback);

    protected abstract void oidcLogin(String authCode, @NotNull AuthCallback<UserInfo> callback);

    protected void login(Context context, String authCode, @NotNull AuthCallback<UserInfo> callback){
        Authing.AuthProtocol authProtocol = getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            standardLogin(authCode, callback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            oidcLogin(authCode, callback);
        }
    }

    protected void onDetachedFromWindow() {

    }
}
