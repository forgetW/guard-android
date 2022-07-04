package cn.withub.guard;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.withub.guard.analyze.Analyzer;
import cn.withub.guard.data.Config;
import cn.withub.guard.data.Safe;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.network.Guardian;
import cn.withub.guard.network.OIDCClient;
import cn.withub.guard.util.ALog;
import cn.withub.guard.util.SystemUtil;
import cn.withub.guard.util.Util;

public class Authing {

    private final static String TAG = "Authing";

    private static final String DEF_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4xKeUgQ+Aoz7TLfAfs9+paePb5KIofVthEopwrXFkp8OCeocaTHt9ICjTT2QeJh6cZaDaArfZ873GPUn00eOIZ7Ae+TiA2BKHbCvloW3w5Lnqm70iSsUi5Fmu9/2+68GZRH9L7Mlh8cFksCicW2Y2W2uMGKl64GDcIq3au+aqJQIDAQAB";

    private static Context sAppContext;
    private static String scheme = "https";
    //https://zhdj.nmgdj.gov.cn/api/v2/applications/这里替换为appid/public-config
    private static String sHost = "zhdj.nmgdj.gov.cn"; // for private deployment
    private static String sPublicKey = "";
    private static String sAppId;

    public static List<String> getResponseTypes() {
        return responseTypes;
    }

    public static void setResponseTypes(List<String> responseTypes) {
        Authing.responseTypes = responseTypes;
    }

    private static List<String> responseTypes;

    public static String getScope() {
        return scope;
    }

    public static void setScope(String scope) {
        Authing.scope = scope;
    }

    private static String scope;

    public static String getClientId() {
        return client_id;
    }

    public static void setClientId(String client_id) {
        Authing.client_id = client_id;
    }

    private static String client_id;

    public static String getTenantId() {
        return tenant_id;
    }

    public static void setTenantId(String tenant_id) {
        Authing.tenant_id = tenant_id;
    }

    private static String tenant_id;
    private static boolean isGettingConfig;
    private static Config publicConfig;
    private static final Queue<Config.ConfigCallback> listeners = new ConcurrentLinkedQueue<>();
    private static UserInfo sCurrentUser;

    public static void init(final Context context, String appId) {
        sAppContext = context.getApplicationContext();
        sAppId = appId;
        requestPublicConfig();
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static String getScheme() {
        return scheme;
    }

    public static void setScheme(String scheme) {
        Authing.scheme = scheme;
    }

    public static String getHost() {
        return sHost;
    }

    public static void setHost(String host) {
        Authing.sHost = host;
    }

    public static String getPublicKey() {
        return sPublicKey;
    }

    public static void setPublicKey(String publicKey) {
        Authing.sPublicKey = publicKey;
    }

    public static void setOnPremiseInfo(String host, String publicKey) {
        Authing.sHost = host;
        Authing.sPublicKey = publicKey;
    }

    public static void autoLogin(AuthCallback<UserInfo> callback) {
        if (getCurrentUser() == null) {
            callback.call(500, "no user logged in", null);
        } else {
            String refreshToken = getCurrentUser().getRefreshToken();
            if (Util.isNull(refreshToken)) {
                AuthClient.getCurrentUser((code, message, userInfo) -> {
                    if (code != 200) {
                        if (code == 2020) {
                            ALog.d(TAG, "auto login token expired");
                            Safe.logoutUser(sCurrentUser);
                        }
                        sCurrentUser = null;
                        callback.call(code, message, userInfo);
                    } else {
                        AuthClient.updateIdToken(callback);
                    }
                });
            } else {
                new OIDCClient().getNewAccessTokenByRefreshToken(refreshToken, (code, message, userInfo) -> {
                    if (code != 200) {
                        if (code == 2020) {
                            ALog.d(TAG, "auto login token expired");
                            Safe.logoutUser(sCurrentUser);
                        }
                        sCurrentUser = null;
                        callback.call(code, message, userInfo);
                    } else {
                        new OIDCClient().getUserInfoByAccessToken(getCurrentUser(), callback);
                    }
                });
            }
        }
    }

    public static String getAppId() {
        return sAppId;
    }

    public static void getPublicConfig(Config.ConfigCallback callback) {
        // add listener first. otherwise callback might be fired in the other thread
        // and this listener is missed
        if (isGettingConfig) {
            listeners.add(callback);
        }

        if (publicConfig != null) {
            listeners.clear();
            callback.call(publicConfig);
        }
    }

    public static UserInfo getCurrentUser() {
        if (sCurrentUser == null) {
            sCurrentUser = Safe.loadUser();
        }
        return sCurrentUser;
    }

    public static void setCurrentUser(UserInfo userInfo) {
        sCurrentUser = userInfo;
    }

    public static void saveUser(UserInfo user) {
        sCurrentUser = user;
        Safe.saveUser(user);
    }

    private static void requestPublicConfig() {
        isGettingConfig = true;
        publicConfig = null;
        _requestPublicConfig();
    }

    private static void _requestPublicConfig() {
        String host = sHost;
//        if (!Util.isIp(sHost)) {
//            host = "console." + sHost;
//        }
        String url = scheme + "://" + host + "/api/v2/applications/" + sAppId + "/public-config";
        Guardian.request(null, url, "get", null, (response) -> {
            try {
                if (response.getCode() == 200) {
                    JSONObject data = response.getData();
                    Config config = Config.parse(data);
                    config.setUserAgent(SystemUtil.getUserAgent(getAppContext()));
                    fireCallback(config);
                } else {
                    ALog.e(TAG, "Get public config failed for appId: " + sAppId + " Msg:" + response.getMessage());
                    fireCallback(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                fireCallback(null);
            }
        });
    }

    private static void fireCallback(Config config) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            publicConfig = config;
            for (Config.ConfigCallback callback : listeners) {
                callback.call(config);
            }
            listeners.clear();
            isGettingConfig = false;
        });
    }

    public static boolean isGettingConfig() {
        return isGettingConfig;
    }
}
