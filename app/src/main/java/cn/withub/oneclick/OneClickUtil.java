package cn.withub.oneclick;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.helper.UnifyUiConfig;
import com.netease.nis.quicklogin.listener.LoginListener;
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;

import cn.withub.R;
import cn.withub.guard.AuthCallback;
import cn.withub.guard.Authing;
import cn.withub.guard.data.ImageLoader;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.flow.AuthFlow;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.network.OIDCClient;
import cn.withub.guard.oneclick.OneClick;
import cn.withub.guard.social.SocialLoginListView;
import cn.withub.guard.util.ALog;
import cn.withub.guard.util.Const;
import cn.withub.guard.util.Util;

import static cn.withub.guard.Authing.getAuthProtocol;

public class OneClickUtil {

    private String TAG = "fireCallback";
    private Context context;
    private QuickLogin quickLogin;

    private int screenWidth; // dp

    public void pre(Context context, AuthCallback<String> callback) {
        this.context = context;
        getAndroidScreenProperty();

        Authing.getPublicConfig(config -> {
            ALog.d("fireCallback", "OneClick start _bid--" + OneClick.bizId);
            String businessId = (OneClick.bizId != null) ? OneClick.bizId : config.getSocialBusinessId(Const.EC_TYPE_YI_DUN);
            quickLogin = QuickLogin.getInstance();
            quickLogin.init(context, businessId);
            quickLogin.prefetchMobileNumber(new QuickLoginPreMobileListener() {
                @Override
                public void onGetMobileNumberSuccess(String YDToken, String mobileNumber) {
                    //预取号成功
                    ALog.d(TAG, "Got phone:" + mobileNumber);
                    ALog.d(TAG, "Got YDToken:" + YDToken);
                    callback.call(200, "成功", mobileNumber);
                }

                @Override
                public void onGetMobileNumberError(String YDToken, String msg) {
                    ALog.e(TAG, "Got phone error:" + msg);
                    ALog.e(TAG, "Got phone error YDToken:" + YDToken);
                    callback.call(500, msg, YDToken);
                }
            });
        });
    }

    private  AuthCallback<UserInfo> callback;

    public void starLogin(AuthCallback<UserInfo> callback){
        this.callback = callback;
        Authing.getPublicConfig((config)->{
            if (config == null) {
                return;
            }

            String url = config.getUserpoolLogo();//"https://zhdjfile.nmgdj.gov.cn/nmlxq/logo/logo.png"
            url = "https://zhdjfile.nmgdj.gov.cn/nmlxq/logo/logo.png";
            ImageLoader.with(context).execute(url, (ok, result)->{
                config(result);
                startOnePass();
            });
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

    private void startOnePass() {
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

    private void config(Drawable logo) {
        Drawable mainColorDrawable = new ColorDrawable(0xffff4534);
        int topMargin = (int) Util.dp2px(context, 16);
        RelativeLayout otherLoginRel = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParamsOther = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsOther.setMargins(0, topMargin, 0, 0);
        layoutParamsOther.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParamsOther.addRule(RelativeLayout.BELOW, com.netease.nis.quicklogin.R.id.yd_btn_oauth);
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
        other.setMinimumWidth((int) Util.dp2px(context, screenWidth - 24 * 2));
        other.setMinimumHeight((int) Util.dp2px(context, 48));
        other.setOnClickListener((v) -> {
            quickLogin.quitActivity();
            AuthFlow.start((Activity) context);
            callback.call(500, "cancel", null);
        });

        RelativeLayout otherLoginRel2 = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.custom_other_login, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, (int) Util.dp2px(context, 130));
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        otherLoginRel2.setLayoutParams(layoutParams);
        ImageView wx = otherLoginRel2.findViewById(R.id.weixin);
        ImageView qq = otherLoginRel2.findViewById(R.id.qq);
        ImageView wb = otherLoginRel2.findViewById(R.id.weibo);
        wx.setOnClickListener(view->{
            Toast.makeText(context, "微信登录", Toast.LENGTH_LONG).show();
        });
        qq.setOnClickListener(view->{
            Toast.makeText(context, "qq登录", Toast.LENGTH_LONG).show();
        });
        wb.setOnClickListener(view->{
            Toast.makeText(context, "微博登录", Toast.LENGTH_LONG).show();
        });

        UnifyUiConfig c = new UnifyUiConfig.Builder()
                .setHideNavigation(true)
                .setLogoIconDrawable(logo)
                .setLogoHeight((int) (screenWidth * 0.25))
                .setLogoWidth((int) (screenWidth * 0.25))
                .setLogoTopYOffset(80)
                .setMaskNumberTopYOffset(240)
                .setMaskNumberSize(25)
                .setSloganSize(15) // 设置认证品牌字体大小
                .setSloganColor(Color.parseColor("#9A9A9A")) // 设置认证品牌颜色
                .setSloganTopYOffset(280) // 设置认证品牌顶部 Y 轴偏移
                .setLoginBtnText(context.getString(R.string.authing_current_phone_login))
                .setLoginBtnTopYOffset(350)
                .setLoginBtnWidth(screenWidth - 24 * 2)
                .setLoginBtnHeight(48)
                .setLoginBtnBackgroundDrawable(mainColorDrawable)
                .addCustomView(otherLoginRel, "otherBtn", UnifyUiConfig.POSITION_IN_BODY, null)
//                .addCustomView(otherLoginRel2, "relative", UnifyUiConfig.POSITION_IN_BODY, null)
                .setProtocolText("用户协议") // 设置隐私栏协议文本
                .setProtocolLink("https://www.baidu.com") // 设置隐私栏协议链接
                .setProtocol2Text("隐私政策") // 设置隐私栏协议文本
                .setProtocol2Link("https://www.baidu.com") // 设置隐私栏协议链接
                .setPrivacyBottomYOffset(30)
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

    private void getAndroidScreenProperty() {
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
}
