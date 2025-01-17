package cn.withub.guard.analyze;

import android.os.Build;
import android.provider.Settings;

import cn.withub.guard.Authing;
import cn.withub.guard.util.Const;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SDKUsageTask implements Runnable {

    @Override
    public void run() {
        String ssaid = Settings.Secure.getString(Authing.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = "https://developer-beta.authing.cn/stats/sdk-trace/?appid=" + Authing.getAppId()
                + "&sdk=android&version=" + Const.SDK_VERSION
                + "&host=" + Authing.getHost()
                + "&device=" + Build.MODEL
                + "&os-version=" + Build.VERSION.RELEASE
                + "&ip=" + ssaid;

        Request.Builder builder = new Request.Builder();
        builder.url(url);

        Request request = builder.build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        try {
            call.execute();
        } catch (Exception ignored) {
        }
    }
}
