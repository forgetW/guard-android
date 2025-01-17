package cn.withub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import cn.withub.guard.PasswordConfirmEditText;
import cn.withub.guard.PasswordEditText;
import cn.withub.R;
import cn.withub.guard.internal.LoadingButton;
import cn.withub.guard.mfa.RecoveryCodeEditText;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.util.Util;

public class ChangePasswordActivity extends AppCompatActivity implements TextWatcher {

    LoadingButton button;
    PasswordEditText oldEditText;
    PasswordEditText newEditText;
    PasswordConfirmEditText confirmEditText;
    RecoveryCodeEditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldEditText = findViewById(R.id.pwd_old);
        newEditText = findViewById(R.id.pwd_new);
        newEditText.getEditText().addTextChangedListener(this);

        confirmEditText = findViewById(R.id.et_confirm);

        et = findViewById(R.id.et_encrypted);

        button = findViewById(R.id.btn_submit);
        button.setOnClickListener((v)->{
            if (Util.isNull(oldEditText.getErrorText())
                    && Util.isNull(newEditText.getErrorText())
                    && Util.isNull(confirmEditText.getErrorText())
                    && !Util.isNull(newEditText.getText().toString())
                    && !Util.isNull(confirmEditText.getText().toString())) {
                button.startLoadingVisualEffect();
                AuthClient.updatePassword(newEditText.getText().toString(), oldEditText.getText().toString(), (code, message, data) -> {
                    button.stopLoadingVisualEffect();
                    if (code == 200) {
                        gotoSampleList();
                    } else {
                        showError(message);
                    }
                });
            }
        });
    }

    private void gotoSampleList() {
        Intent intent = new Intent(this, SampleListActivity.class);
        startActivity(intent);
    }

    private void showError(String error) {
        runOnUiThread(()->{
            Util.setErrorText(et, error);
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        et.getEditText().setText(Util.rsaEncryptPassword(s.toString()));
    }
}