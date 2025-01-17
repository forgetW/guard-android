package cn.withub.nissan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import cn.withub.R;
import cn.withub.guard.AccountEditText;
import cn.withub.guard.GlobalStyle;
import cn.withub.guard.activity.BaseAuthActivity;
import cn.withub.guard.internal.LoadingButton;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.util.Validator;

public class NissanVirtualKeySendEmailActivity extends BaseAuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove EditText's default underline
        GlobalStyle.setEditTextBackground(0);

        setContentView(R.layout.activity_nissan_virtual_key_send_email);

        AccountEditText etEmail = findViewById(R.id.nvk_et_email);
        LoadingButton btn = findViewById(R.id.btn_reset);
        btn.setOnClickListener(v -> {
            if (Validator.isValidEmail(etEmail.getText())) {
                btn.startLoadingVisualEffect();

                AuthClient.sendResetPasswordEmail(etEmail.getText().toString(), (code, message, data)->{
                    runOnUiThread(()->{
                        if (code == 200) {
                            Toast.makeText(getApplicationContext(), "Password reset code sent", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(NissanVirtualKeySendEmailActivity.this, NissanVirtualKeyResetPasswordActivity.class);
                            intent.putExtra("email", etEmail.getText().toString());
                            startActivity(intent);
                            finish();
                        }
                        btn.stopLoadingVisualEffect();
                    });
                });
            }
        });
    }
}