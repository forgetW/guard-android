package cn.withub;

import android.content.Intent;
import android.os.Bundle;

import cn.withub.R;
import cn.withub.guard.GlobalStyle;
import cn.withub.guard.LoginButton;
import cn.withub.guard.activity.BaseAuthActivity;
import cn.withub.guard.social.SocialLoginListView;

public class AndroidAuthActivity extends BaseAuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalStyle.clear();

        setContentView(R.layout.activity_login_android);

        LoginButton btn = findViewById(R.id.btn_login);
        if (btn != null) {
            btn.setOnLoginListener((code, message, data) -> {
                if (code == 200) {
                    Intent intent = new Intent(AndroidAuthActivity.this, MainActivity.class);
                    intent.putExtra("user", data);
                    startActivity(intent);
                    finish();
                }
            });
        }

        SocialLoginListView lv = findViewById(R.id.lv_social);
        if (lv != null) {
            lv.setOnLoginListener((code, message, data) -> {
                if (code == 200) {
                    Intent intent = new Intent(AndroidAuthActivity.this, MainActivity.class);
                    intent.putExtra("user", data);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
