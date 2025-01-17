package cn.withub;

import static cn.withub.guard.activity.AuthActivity.OK;
import static cn.withub.guard.activity.AuthActivity.RC_LOGIN;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.netease.nis.quicklogin.QuickLogin;
import com.netease.nis.quicklogin.listener.QuickLoginPreMobileListener;
import com.netease.nis.quicklogin.listener.QuickLoginTokenListener;

import java.util.concurrent.Executor;

import cn.withub.SignInActivity;
import cn.withub.guard.AuthCallback;
import cn.withub.guard.data.ImageLoader;
import cn.withub.guard.util.ALog;
import cn.withub.guard.util.Const;
import cn.withub.oneclick.OneClickUtil;
import cn.withub.ut.UTActivity;
import cn.withub.abao.AbaoActivity;
import cn.withub.appauth.AppAuthActivity;
import cn.withub.authenticator.AuthenticatorActivity;
import cn.withub.guard.Authing;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.flow.AuthFlow;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.network.OIDCClient;
import cn.withub.guard.oneclick.OneClick;
import cn.withub.oneclick.OneClickActivity;
import cn.withub.push.LoginByPushNotificationActivity;
import cn.withub.scan.ScanAuthActivity;
import cn.withub.theragun.TheragunAuthActivity;
import cn.withub.webview.AuthingWebViewActivity;
import cn.withub.wechat.WechatAuthActivity;

public class SampleListActivity extends AppCompatActivity {

    private static final int AUTHING_LOGIN = 0;

    private boolean biometric;

    String[] from = {
            "Authing 标准登录",
            "手机号一键登录（Authing UI）",
            "手机号一键登录（纯逻辑）",
            "微信",
            "Theragun",
            "阿宝说",
            "Auth Container",
            "AppAuth",
            "Authing WebView 登录",
            "Authing 自定义 WebView 登录",
            "MFA",
            "登录/注册后用户信息完善",
            "扫码登录",
            "生物二次验证",
            "Authenticator",
            "Login by push notification",
            "帐号密码登录",
            "发送验证码",
            "验证码登录",
            "登出",
            "刷新token",
            "谷歌登录",
            "场景测试",
            "获取手机号码",
            "一键登录",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_list);

        ListView listView = findViewById(R.id.lv_samples);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.sample_list_item, from);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((arg0, arg1, arg2, pos) -> {
            if (pos == 7) {//AppAuth
                startActivity(AppAuthActivity.class);
                return;
            } else if (pos == 12) {//扫码登录
                startActivity(ScanAuthActivity.class);
                return;
            } else if (pos == 14) {//Authenticator
                startActivity(AuthenticatorActivity.class);
                return;
            }  else if (pos == 21) {//google登录
                Intent intent = new Intent(SampleListActivity.this, SignInActivity.class);
                startActivity(intent);
                return;
            } else if (pos == 22) {//google登录
                Intent intent = new Intent(SampleListActivity.this, UTActivity.class);
                startActivity(intent);
                return;
            }
            else if(pos == 19){
                AuthClient.logout(this::fireCallback, true);
                return;
            }
            else if(pos == 20){
                UserInfo currentUser = Authing.getCurrentUser();
                if (currentUser != null) {
                    String refreshToken = currentUser.getRefreshToken();
                    if (!TextUtils.isEmpty(refreshToken)) {
                        new OIDCClient().getNewAccessTokenByRefreshToken(refreshToken, this::fireCallback);
                    }
                }
            }
            if (null != Authing.getCurrentUser()) {
                gotoMain();
                return;
            }
            if (pos == AUTHING_LOGIN) {//Authing 标准登录
                AuthFlow.start(this);
            } else if (pos == 1) {//手机号一键登录（Authing UI）
                Intent intent = new Intent(SampleListActivity.this, OneClickActivity.class);
                startActivityForResult(intent, RC_LOGIN);
            } else if (pos == 2) {//手机号一键登录（纯逻辑）
                // if you want to return refreshToken、idToken、refreshToken
                Authing.setAuthProtocol(Authing.AuthProtocol.EOIDC);
                OneClick oneClick = new OneClick(SampleListActivity.this);
                oneClick.start(((code, message, userInfo) -> {
                    if (code != 200) {
                        Toast.makeText(Authing.getAppContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        gotoMain(userInfo);
                    }
                }));
            } else if (pos == 3) {//微信
                startActivity(WechatAuthActivity.class);
            } else if (pos == 4) {//Theragun
                startActivity(TheragunAuthActivity.class);
            } else if (pos == 5) {//阿宝说
                startActivity(AbaoActivity.class);
            } else if (pos == 6) {//Auth Container
                Authing.setAuthProtocol(Authing.AuthProtocol.EOIDC);
                AuthFlow.start(this);
//                Intent intent = new Intent(SampleListActivity.this, NissanVirtualKeyAuthActivity.class);
//                startActivity(intent);
            } else if (pos == 8) {//Authing WebView 登录
                AuthFlow flow = AuthFlow.startWeb(this);
//                flow.setScope("openid");
                flow.setSkipConsent(true);
            } else if (pos == 9) {//Authing 自定义 WebView 登录
                startActivity(AuthingWebViewActivity.class);
            } else if (pos == 10) {//MFA
                Authing.init(SampleListActivity.this, "61c173ada0e3aec651b1a1d1");
                AuthFlow.start(this);
            } else if (pos == 11) {//登录/注册后用户信息完善
                Authing.init(SampleListActivity.this, "61ae0c9807451d6f30226bd4");
                AuthFlow.start(this);
            } else if (pos == 13) {//生物二次验证
                biometric = true;
                AuthFlow.start(this);
            } else if (pos == 15) {//Login by push notification
                Intent intent = new Intent(SampleListActivity.this, LoginByPushNotificationActivity.class);
                startActivityForResult(intent, RC_LOGIN);
            } else if (pos == 16) {

//                String clientId = Authing.getClientId();
//
//                Authing.autoLogin(this::fireCallback);
                final AuthFlow flow = new AuthFlow();
                flow.setAuthProtocol(AuthContainer.AuthProtocol.EOIDC);
                if (flow.getAuthProtocol() == AuthContainer.AuthProtocol.EInHouse) {
                    AuthClient.loginByAccount("18002330359", "NMGxczx2022", this::fireCallback);
                } else if (flow.getAuthProtocol() == AuthContainer.AuthProtocol.EOIDC) {
                    new OIDCClient().loginByAccount("18002330359", "NMGxczx2022", this::fireCallback);
                }
            }else if(pos == 17){
                AuthClient.sendSms("17723555741", this:: fireCallback);
            }
            else if(pos == 18){
                EditText editTextNumber = findViewById(R.id.editTextNumber);
                String trim = editTextNumber.getText().toString().trim();
                new OIDCClient().loginByPhoneCode("17723555741", trim, this::fireCallback);
            }
            else if(pos == 23){
                oneClickUtil = new OneClickUtil();
                oneClickUtil.pre(getApplicationContext(), new AuthCallback<String>() {
                    @Override
                    public void call(int code, String message, String data) {
                        Toast.makeText(getApplicationContext(), "获取成功", Toast.LENGTH_LONG).show();
                        ALog.d("fireCallback", "fireCallback: " + message + "--code: " + code);
                    }
                });
            }
            else if(pos == 24){
                if (oneClickUtil != null) {
                    oneClickUtil.starLogin(this::fireCallback);
                }
            }
        });
    }

    private OneClickUtil oneClickUtil;


    protected void fireCallback(int code, String message, Object o) {
        ALog.d("fireCallback", "fireCallback: " + message + "--code: " + code);
    }

    protected void fireCallback(int code, String message, UserInfo userInfo) {
        ALog.d("fireCallback", "fireCallback: " + message + "--code: " + code);
        ALog.d("fireCallback", "fireCallback: AccessToken---" + userInfo.getAccessToken());
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(SampleListActivity.this, cls);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOGIN && resultCode == OK && data != null) {
            if (biometric) {
                startBiometric();
            } else {
                gotoMain();
            }
        }
    }

    private void startBiometric() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "" + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                gotoMain();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.authing_biometric_title))
                .setSubtitle(getString(R.string.authing_biometric_tip))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .setNegativeButtonText("Cancel")
                .build();
        biometricPrompt.authenticate(promptInfo);
    }

    private void gotoMain(UserInfo data) {
        if (data != null) {
            AuthFlow.showUserProfile(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_setting) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoMain() {
//        new Push().registerDevice(this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}