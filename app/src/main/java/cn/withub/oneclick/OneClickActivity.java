package cn.withub.oneclick;

import android.os.Bundle;
<<<<<<< HEAD:app/src/main/java/cn/withub/oneclick/OneClickActivity.java
import cn.withub.R;
import cn.withub.guard.activity.AuthActivity;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.oneclick.OneClickAuthButton;
=======

import cn.authing.R;
import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.oneclick.OneClickAuthButton;
>>>>>>> authing/master:app/src/main/java/cn/authing/oneclick/OneClickActivity.java

public class OneClickActivity extends AuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_click);


        // If you want to return accessToken，do like this
        OneClickAuthButton oneClickAuthButton = findViewById(R.id.one_click_btn);
    }
}