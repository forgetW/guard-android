# 使用 AppAuth 接入 Authing

AppAuth 是一个实现了 OAuth、OIDC 协议的移动库。它通过打开系统浏览器进入身份提供商的托管页面完成认证，使用 AppAuth 可以快速完成认证。

AppAuth 和原生的对比如下：

|                      | AppAuth | 原生 | 说明 |
| -------------------- |:--------:| :------:| :------:| 
| 用户体验     |    差    |  好   |    AppAuth 会打开系统浏览器，完成认证后，在回到 App 的过程中系统会弹窗提示用户是否用原生应用打开   |
|  性能     |    差    |   好   |    AppAuth 每次都需要加载页面，即使已经认证过，也要弹出再弹回。（可以用原生代码规避，如手动保存登录状态）   |
|  开发难度     |    低    |  Depends   |    因为复杂的 UI 由身份提供商 Web 页面实现，所以不用开发 UI。但既然应用采用了 AppAuth，表明应用可以接受身份提供商 UI，那么也可以通过原生快速接入：[使用 Authing 原生 UI 极速接入](./../start_with_authing.md)   |
|  升级难度     |    低    |  高   |    需要 App 升级   |
|  掌控度     |    低    |  高   |    AppAuth 方式，UI以及认证流程主要由身份供应商定义；原生模式则完全由开发人员掌控   |


<br>

接下来介绍接入流程，分为配置部分和代码部分

<br>

# 配置步骤如下：

## 1. 引入依赖

```groovy
implementation 'cn.withub:guard:+'
implementation 'net.openid:appauth:0.10.0'
```

<br>

## 2. 在 AndroidManifest 里面配置如下：

```xml
<activity
    android:name="net.openid.appauth.RedirectUriReceiverActivity"
    android:exported="true"
    tools:node="replace">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />

        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <data
            android:host="authing.cn"
            android:path="/redirect"
            android:scheme="cn.guard" />
    </intent-filter>
</activity>
```

其中 android:host，android:path，android:scheme 由应用自定义，只要不和其他网站冲突就行

<br>

## 3. 在 Authing 后台配置回调

将上面步骤的的配置拼接为 URL 后配置到 Authing 的登录回调里面。在上面例子中，回调 URL 为：

cn.guard://authing.cn/redirect

那么我们需要在 authing 后台做如下配置：

![](./../images/appauth/set_login_callback.png)

## 4. 设置换取 token 身份验证方式

<br>

由于我们是客户端，不能存放 client secret，所以需要

![](./../images/appauth/disable_client_secret.png)

<br>

# 代码：

## 1. 初始化

在应用启动（如 App.java）里面调用：

```java
Authing.init(appContext, "your_authing_app_id");
```

## 2. 获取 Authing 端点信息

```java
Authing.getPublicConfig(config -> {
    String host = config.getIdentifier();
    AuthorizationServiceConfiguration.fetchFromIssuer(Uri.parse("https://" + host + ".authing.cn/oidc"),
            (serviceConfiguration, ex) -> {
                if (ex != null) {
                    Log.e(TAG, "failed to fetch configuration");
                    return;
                }

                authState = new AuthState(serviceConfiguration);
                startAuth(serviceConfiguration);
            });
});
```

## 3. 用系统浏览打开认证界面

```java
private void startAuth(AuthorizationServiceConfiguration serviceConfig) {
    AuthorizationRequest.Builder authRequestBuilder =
            new AuthorizationRequest.Builder(
                    serviceConfig,
                    Authing.getAppId(),
                    ResponseTypeValues.CODE,
                    Uri.parse("cn.guard://authing.cn/redirect"));

    AuthorizationRequest authRequest = authRequestBuilder
            .setScope("openid profile email phone address offline_access role extended_fields")
            .setPrompt("consent") // for refresh token
            .setCodeVerifier(Util.randomString(43))
            .build();

    authService = new AuthorizationService(this);
    Intent authIntent = authService.getAuthorizationRequestIntent(authRequest);
    startActivityForResult(authIntent, RC_AUTH);
}
```

>注意：这里的 "cn.guard://authing.cn/redirect" 即上面配置信息里面的回调地址，确保已在 authing 后台配置

## 4. 拿到凭证

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 1000) {
        AuthorizationResponse resp = AuthorizationResponse.fromIntent(data);
        AuthorizationException ex = AuthorizationException.fromIntent(data);
        // ... process the response or exception ...

        TokenRequest request = resp.createTokenExchangeRequest();
        authService.performTokenRequest(
            request,
            (resp1, ex1) -> {
                if (resp1 != null) {
                    // exchange succeeded
                    authState.update(resp1, ex1);
                    Log.d(TAG, resp1.idToken);
                    Log.d(TAG, "at:" + resp1.accessToken);
                    Log.d(TAG, "rt:" + resp1.refreshToken);
                    getUserInfo(resp1.accessToken, resp1.refreshToken);
                } else {
                    // authorization failed, check ex for more details
                }
            });
    } else {
        // ...
    }
}
```

## 5. 换取用户信息
```java
private void getUserInfo(String accessToken, String refreshToken) {
    UserInfo userInfo = new UserInfo();
    userInfo.setAccessToken(accessToken);
    userInfo.setRefreshToken(refreshToken);
    OIDCClient.getUserInfoByAccessToken(userInfo, (code, message, userInfo)->{
        if (code == 200) {
            // user info
        }
    });
}
```

## 6. 刷新 access token

如果需要用 refresh token 刷新 access token，则调用：

```java
OIDCClient.getNewAccessTokenByRefreshToken(rt, (code, message, data)->{
    if (code == 200) {
        Log.d(TAG, "new at:" + data.getAccessToken());
        Log.d(TAG, "new id token:" + data.getIdToken());
        Log.d(TAG, "new rt:" + data.getRefreshToken());

        runOnUiThread(()->{
            AuthFlow.showUserProfile(this);
        });
    }
});
```

>注意，每次调用会得到新的 refresh token

更多详情参考 Demo 里面的 AppAuthActivity.java