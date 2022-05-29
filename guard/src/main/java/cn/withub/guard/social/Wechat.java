package cn.withub.guard.social;

import android.content.Context;

import androidx.annotation.NonNull;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.jetbrains.annotations.NotNull;

import cn.withub.guard.AuthCallback;
import cn.withub.guard.Authing;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.social.wechat.WXCallbackActivity;
import cn.withub.guard.util.Const;

public class Wechat extends SocialAuthenticator {

    public static IWXAPI api;
    public static String appId;

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            api = WXAPIFactory.createWXAPI(context, appId, true);
            if (appId != null) {
                api.registerApp(appId);
            } else {
                api.registerApp(config.getSocialAppId(Const.EC_TYPE_WECHAT));
            }

            WXCallbackActivity.setCallback(callback);
            WXCallbackActivity.setAuthProtocol(getAuthProtocol(context));

            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo_test";
            api.sendReq(req);
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {

    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {

    }
}
