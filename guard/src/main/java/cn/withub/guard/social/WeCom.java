package cn.withub.guard.social;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.tencent.wework.api.IWWAPI;
import com.tencent.wework.api.WWAPIFactory;
import com.tencent.wework.api.model.WWAuthMessage;

import org.jetbrains.annotations.NotNull;

import cn.withub.guard.AuthCallback;
import cn.withub.guard.Authing;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.network.OIDCClient;
import cn.withub.guard.util.ALog;
import cn.withub.guard.util.Const;

public class WeCom extends SocialAuthenticator {

    private static final String TAG = "WeCom";

    private final String type;
    public static String schema;
    public static String agentId;
    public static String corpId;

    public WeCom() {
        type = Const.EC_TYPE_WECHAT_COM;
    }

    public WeCom(String type) {
        if (!TextUtils.isEmpty(type) && "wecom-agency".equals(type)) {
            this.type = Const.EC_TYPE_WECHAT_COM_AGENCY;
        } else {
            this.type = Const.EC_TYPE_WECHAT_COM;
        }
    }

    @Override
    public void login(Context context, @NotNull AuthCallback<UserInfo> callback) {
        Authing.getPublicConfig(config -> {
            IWWAPI iwwapi = WWAPIFactory.createWWAPI(context);
            String sch = (schema != null ) ? schema : config.getSocialSchema(type);
            iwwapi.registerApp(sch);

            final WWAuthMessage.Req req = new WWAuthMessage.Req();
            req.sch = sch;
            req.agentId = (agentId != null ) ? agentId : config.getSocialAgentId(type);
            req.appId = (corpId != null ) ? corpId : config.getSocialAppId(type);
            req.state = type;
            iwwapi.sendMessage(req, resp -> {
                if (resp instanceof WWAuthMessage.Resp) {
                    WWAuthMessage.Resp rsp = (WWAuthMessage.Resp) resp;
                    if (rsp.errCode == WWAuthMessage.ERR_CANCEL) {
                        ALog.i(TAG, "登录取消");
                        fireCallback(callback, null);
                    } else if (rsp.errCode == WWAuthMessage.ERR_FAIL) {
                        ALog.i(TAG, "登录失败");
                        fireCallback(callback, null);
                    } else if (rsp.errCode == WWAuthMessage.ERR_OK) {
                        ALog.i(TAG, "Auth onSuccess");
                        login(context, rsp.code, callback);
                    } else {
                        ALog.e(TAG, "Auth Failed, resp error");
                        fireCallback(callback, null);
                    }
                } else {
                    ALog.e(TAG, "Auth Failed, resp error");
                    fireCallback(callback, null);
                }
            });
        });
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        if (Const.EC_TYPE_WECHAT_COM.equals(type)) {
            AuthClient.loginByWecom(authCode, callback);
        } else if (Const.EC_TYPE_WECHAT_COM_AGENCY.equals(type)) {
            AuthClient.loginByWecomAgency(authCode, callback);
        }
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
        if (Const.EC_TYPE_WECHAT_COM.equals(type)) {
            new OIDCClient().loginByWecom(authCode, callback);
        } else if (Const.EC_TYPE_WECHAT_COM_AGENCY.equals(type)) {
            new OIDCClient().loginByWecomAgency(authCode, callback);
        }
    }

    private void fireCallback(AuthCallback<UserInfo> callback, UserInfo info) {
        if (callback != null) {
            callback.call(0, "", info);
        }
    }
}
