package cn.withub.guard.oneclick;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.helper.UnifyUiConfig;
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import javax.crypto.SecretKey;

import cn.withub.guard.AuthCallback;
import cn.withub.guard.Authing;
import cn.withub.guard.Callback;
import cn.withub.guard.R;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.data.ImageLoader;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.flow.AuthFlow;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.network.OIDCClient;
import cn.withub.guard.social.SocialAuthenticator;
import cn.withub.guard.social.SocialLoginListView;
import cn.withub.guard.util.ALog;
import cn.withub.guard.util.Const;
import cn.withub.guard.util.Util;

public class OneClick extends SocialAuthenticator implements Serializable {

    private static final String TAG = "OneClick";
    private static final int MSG_LOGIN = 1;

    public static String bizId = "cb2df164fe704bb990ecf80a8f7f0893";

//    SecretId：2c13ee878014356e69d1ed8512218e01
//    SecretKey：3d53ef7dbde9b99d831688ba3f54f660
    private final Context context;
    private final Handler handler;
    private UnifyUiConfig uiConfig;
    private AuthCallback<UserInfo> callback;
    private QuickLogin quickLogin;

    private int screenWidth; // dp

    public OneClick(Context context) {
        this.context = context;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MSG_LOGIN)
                    startLogin();
            }
        };
    }

    public OneClick(Context context, AuthCallback<UserInfo> callback ) {
        this.context = context;
        this.callback = callback;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MSG_LOGIN)
                    startLogin();
            }
        };
    }

    public void start(@NotNull AuthCallback<UserInfo> callback) {
        start(bizId, null, callback);
    }

    public void start(UnifyUiConfig config, @NotNull AuthCallback<UserInfo> callback) {
        start(bizId, config, callback);
    }

    public void start(String bid, UnifyUiConfig uiConfig, @NotNull AuthCallback<UserInfo> callback) {
        String _bid = TextUtils.isEmpty(bid) ? bizId : bid;
        this.uiConfig = uiConfig;
        this.callback = callback;

        getAndroidScreenProperty();

        Authing.getPublicConfig(config -> {
            ALog.d("fireCallback", "OneClick start _bid--" + _bid);
            String businessId = (_bid != null ) ? _bid : config.getSocialBusinessId(Const.EC_TYPE_YI_DUN);
            quickLogin = QuickLogin.getInstance();
            quickLogin.init(context, businessId);
            quickLogin.prefetchMobileNumber(new QuickLoginPreMobileListener() {
                @Override
                public void onGetMobileNumberSuccess(String YDToken, String mobileNumber) {
                    //预取号成功
                    ALog.d(TAG, "Got phone:" + mobileNumber);
                    ALog.d("fireCallback", "Got phone:" + mobileNumber);
                    handler.sendEmptyMessageDelayed(MSG_LOGIN, 1000);
                }

                @Override
                public void onGetMobileNumberError(String YDToken, String msg) {
                    ALog.e("fireCallback", "Got phone error:" + msg);
                    ALog.e("fireCallback", "Got phone error YDToken:" + YDToken);
                    ALog.e(TAG, "Got phone error:" + msg);
                    callback.call(500, msg, null);
                }
            });
        });
    }

    public void startLogin() {
        if (uiConfig != null) {
            quickLogin.setUnifyUiConfig(uiConfig);
            startOnePass();
            return;
        }

        Authing.getPublicConfig((config)->{
            if (config == null) {
                return;
            }

            String url = config.getUserpoolLogo();
            ImageLoader.with(context).execute(url, (ok, result)->{
                config(result);
                startOnePass();
            });
        });
    }

    public void startOnePass() {
        quickLogin.onePass(new QuickLoginTokenListener() {
            @Override
            public void onGetTokenSuccess(String YDToken, String accessCode) {
                quickLogin.quitActivity();
                //一键登录成功 运营商token：accessCode获取成功
                //拿着获取到的运营商token二次校验（建议放在自己的服务端）
                ALog.e(TAG, "onGetTokenSuccess:" + accessCode);
                ALog.e(TAG, "YDToken:" + YDToken);
                authingLogin(YDToken, accessCode);
            }

            @Override
            public void onGetTokenError(String YDToken, String msg) {
                quickLogin.quitActivity();
                ALog.e(TAG, "onGetTokenError:" + msg);
                callback.call(500, msg, null);
            }

            @Override
            public void onCancelGetToken() {
                callback.call(201, null, null);
            }
        });
    }

    private void authingLogin(String t, String ac) {
        Authing.AuthProtocol authProtocol = getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            AuthClient.loginByOneAuth(t, ac, this::fireCallback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            new OIDCClient().loginByOneAuth(t, ac, this::fireCallback);
        }
    }

    private void fireCallback(int code, String message, UserInfo userInfo){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(()-> {
            if (code != 200) {
                Toast.makeText(Authing.getAppContext(), message, Toast.LENGTH_SHORT).show();
            }
            callback.call(code, message, userInfo);
        });
    }

    public void getAndroidScreenProperty() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        //px
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)

        ALog.d(TAG, "屏幕宽度（像素）：" + width);
        ALog.d(TAG, "屏幕高度（像素）：" + height);
        ALog.d(TAG, "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
        ALog.d(TAG, "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        ALog.d(TAG, "屏幕宽度（dp）：" + screenWidth);
        ALog.d(TAG, "屏幕高度（dp）：" + screenHeight);
    }

    private void config(Drawable logo) {
        Drawable mainColorDrawable = new ColorDrawable(Util.getThemeAccentColor(context));

        int topMargin = (int)Util.dp2px(context, 16);
        RelativeLayout otherLoginRel = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParamsOther = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsOther.setMargins(0, topMargin, 0, 0);
        layoutParamsOther.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsOther.addRule(RelativeLayout.BELOW, com.netease.nis.quicklogin.R.id.yd_rl_loading);
        otherLoginRel.setLayoutParams(layoutParamsOther);

        Button other = new Button(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int m = (int) Util.dp2px(context, 24);
        lp.setMargins(m, 0, m, 0);
        other.setLayoutParams(lp);
        otherLoginRel.addView(other);
        other.setText(context.getString(R.string.authing_other_login));
        other.setStateListAnimator(null);
        other.setTextColor(0xff545968);
        other.setBackgroundColor(0xffF5F6F7);
        other.setMinimumWidth((int)Util.dp2px(context, screenWidth - 24*2));
        other.setMinimumHeight((int)Util.dp2px(context, 48));
        other.setOnClickListener((v)-> {
            quickLogin.quitActivity();
            AuthFlow.start((Activity) context);
            callback.call(500, "cancel", null);
        });

        RelativeLayout socialRel = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParamsSocial = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsSocial.setMargins(0, 0, 0, (int)Util.dp2px(context, 100));
        layoutParamsSocial.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsSocial.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        socialRel.setLayoutParams(layoutParamsSocial);
        SocialLoginListView slv = new SocialLoginListView(context);
        slv.setOnLoginListener(((code, message, userInfo) -> {
            quickLogin.quitActivity();
            callback.call(code, message, userInfo);
        }));
        socialRel.addView(slv);

        UnifyUiConfig c = new UnifyUiConfig.Builder()
                .setHideNavigation(true)
                .setLogoIconDrawable(logo)
                .setLogoTopYOffset(80)
                .setMaskNumberTopYOffset(250)
                .setSloganColor(0)
                .setSloganBottomYOffset(1000)
                .setLoginBtnText(context.getString(R.string.authing_current_phone_login))
                .setLoginBtnTopYOffset(320)
                .setLoginBtnWidth(screenWidth - 24*2)
                .setLoginBtnHeight(48)
                .setLoginBtnBackgroundDrawable(mainColorDrawable)
                .addCustomView(otherLoginRel, "otherBtn", UnifyUiConfig.POSITION_IN_BODY, null)
//                .addCustomView(socialRel, "socialList", UnifyUiConfig.POSITION_IN_BODY, null)
                .setPrivacyBottomYOffset(80)
                .setPrivacyMarginLeft(24)
                .setPrivacyMarginRight(24)
                .setPrivacyTextGravityCenter(false)
                .setCheckBoxGravity(Gravity.TOP)
                .setPrivacyCheckBoxWidth(21)
                .setPrivacyProtocolColor(context.getColor(R.color.authing_main))
                .setPrivacyCheckBoxHeight(18)
                .setUnCheckedImageName("authing_checkbox")
                .setCheckedImageName("authing_checked")
                .setPrivacySize(14)
                .build(context);
        quickLogin.setUnifyUiConfig(c);
    }

    @Override
    public void login(Context context, @NonNull AuthCallback<UserInfo> callback) {
        start(callback);
    }

    @Override
    protected void standardLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
    }

    @Override
    protected void oidcLogin(String authCode, @NonNull AuthCallback<UserInfo> callback) {
    }
}
