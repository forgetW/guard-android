package cn.withub.nissan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import cn.withub.MainActivity;
import cn.withub.R;
import cn.withub.guard.LoginButton;
import cn.withub.guard.activity.BaseAuthActivity;

public class NissanVirtualKeyAuthActivity extends BaseAuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nissan_virtual_key_login);

        TextView tvReset = findViewById(R.id.tv_reset);
        tvReset.setOnClickListener((v)->{
            Intent intent = new Intent(NissanVirtualKeyAuthActivity.this, NissanVirtualKeySendEmailActivity.class);
            startActivity(intent);
        });

        LoginButton btn = findViewById(R.id.btn_login);
        btn.setOnLoginListener((code, message, data) -> {
            if (code == 200) {
                Intent intent = new Intent(NissanVirtualKeyAuthActivity.this, MainActivity.class);
                intent.putExtra("user", data);
                startActivity(intent);
                finish();
            }
        });

        Button signBtn = findViewById(R.id.btn_goto_signup);
        signBtn.setOnClickListener((v)->{
            Intent intent = new Intent(NissanVirtualKeyAuthActivity.this, NissanVirtualKeySignupOneActivity.class);
            startActivity(intent);
        });
    }
}