package cn.withub.push;

import android.os.Bundle;

import cn.withub.guard.activity.AuthActivity;
import cn.withub.R;

public class LoginByPushNotificationActivity extends AuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_push_notification);
    }
}