package cn.authing.guard.social;

import android.content.Context;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.jetbrains.annotations.NotNull;

import cn.authing.guard.AuthCallback;
import cn.authing.guard.Authing;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.social.wechat.WXCallbackActivity;
import cn.authing.guard.util.Const;

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
}
