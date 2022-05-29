package cn.withub.guard.activity;

import android.os.Bundle;
import android.view.View;

import cn.withub.guard.Authing;
import cn.withub.guard.EmailEditText;
import cn.withub.guard.R;
import cn.withub.guard.util.Util;

public class BindEmailActivity extends BaseAuthActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_bind_email);

        EmailEditText editText = findViewById(R.id.et_email);
        if (Authing.getCurrentUser() != null && !Util.isNull(Authing.getCurrentUser().getEmail())) {
            editText.getEditText().setText(Authing.getCurrentUser().getEmail());
            editText.disable();
            findViewById(R.id.ll_get_code).setVisibility(View.GONE);
        }
    }
}