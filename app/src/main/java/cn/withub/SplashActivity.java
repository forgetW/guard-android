package cn.withub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import java.util.Objects;

import cn.withub.R;
import cn.withub.guard.Authing;
import cn.withub.guard.data.Safe;

public class SplashActivity extends AppCompatActivity {

    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(()-> next(1), 1000);

        if (Authing.isGettingConfig()) {
            Authing.getPublicConfig(config -> {
                if (config == null) {
                    Toast.makeText(this, R.string.authing_no_network, Toast.LENGTH_LONG).show();
                } else {
                    Authing.autoLogin((code, message, userInfo) -> next(2));
                }
            });
        } else {
            Authing.autoLogin((code, message, userInfo) -> next(2));
        }

        Safe.saveAccount("13600000000");
        Safe.savePassword("13600000000");
    }

    private void next(int f) {
        flag |= f;

        // both condition meets
        if (flag == 3) {
            Intent intent = new Intent(this, SampleListActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
