package cn.withub.guard.social.wechat;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.withub.guard.AuthCallback;
import cn.withub.guard.Authing;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.network.OIDCClient;
import cn.withub.guard.social.Wechat;
import cn.withub.guard.util.ALog;

public class WXCallbackActivity extends AppCompatActivity implements IWXAPIEventHandler {

    public static final String TAG = WXCallbackActivity.class.getSimpleName();

    private static AuthCallback<UserInfo> callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Wechat.api = WXAPIFactory.createWXAPI(this, Wechat.appId);
        Wechat.api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        callback = null;
    }

    @Override
    public void onReq(BaseReq baseReq) {
        ALog.d(TAG, "onReq: ");
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                ALog.d(TAG, "Got wechat code: " + ((SendAuth.Resp) resp).code);
                Authing.AuthProtocol authProtocol = Authing.getAuthProtocol();
                if (authProtocol == Authing.AuthProtocol.EInHouse) {
                    AuthClient.loginByWechat(((SendAuth.Resp) resp).code, callback);
                } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
                    new OIDCClient().loginByWechat(((SendAuth.Resp) resp).code, callback);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                ALog.i(TAG, "wechat user canceled");
                if (callback != null) {
                    callback.call(BaseResp.ErrCode.ERR_USER_CANCEL, "", null);
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                ALog.w(TAG, "wechat user denied auth");
                if (callback != null) {
                    callback.call(BaseResp.ErrCode.ERR_AUTH_DENIED, "", null);
                }
                break;
            default:
                break;
        }

        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Wechat.api.handleIntent(intent, this);
    }

    public static void setCallback(AuthCallback<UserInfo> callback) {
        WXCallbackActivity.callback = callback;
    }

}
