package cn.withub.theragun;

import android.content.Intent;
import android.os.Bundle;

import cn.withub.MainActivity;
import cn.withub.R;
import cn.withub.guard.LoginButton;
import cn.withub.guard.activity.BaseAuthActivity;

public class TheragunVerifyCodeActivity extends BaseAuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theragun_login_verify_code);

        String phone = getIntent().getStringExtra("phone");
        LoginButton btn = findViewById(R.id.btn_login);
        btn.setPhoneNumber(phone);

        btn.setOnLoginListener((code, message, data) -> {
            if (code == 200) {
                Intent intent = new Intent(TheragunVerifyCodeActivity.this, MainActivity.class);
                intent.putExtra("user", data);
                startActivity(intent);
                finish();
            }
        });
    }
}