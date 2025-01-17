package cn.withub.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import cn.withub.MainActivity;
import cn.withub.R;
import cn.withub.guard.CountryCodePicker;
import cn.withub.guard.GlobalStyle;
import cn.withub.guard.LoginButton;
import cn.withub.guard.PhoneNumberEditText;
import cn.withub.guard.activity.BaseAuthActivity;
import cn.withub.guard.data.Country;

public class WechatAuthActivity extends BaseAuthActivity {

    private PhoneNumberEditText input;
    private CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove EditText's default underline
        GlobalStyle.setEditTextBackground(0);

        setContentView(R.layout.wechat_login);

        countryCodePicker = findViewById(R.id.ccp);
        input = findViewById(R.id.pnet_input);

        Button next = findViewById(R.id.btn_next);
        if (next != null) {
            next.setOnClickListener((v) -> {
                setContentView(R.layout.wechat_login_verify_code);
                gotoVerifyCode();
            });
        }
    }

    private void gotoVerifyCode() {
        Country country = countryCodePicker.getCountry();
        String code = "+" + country.getCode();
        PhoneNumberEditText phoneNumberEditText = findViewById(R.id.pnet);
        phoneNumberEditText.getEditText().setText(code + input.getText());
        phoneNumberEditText.getEditText().setEnabled(false);

        LoginButton btn = findViewById(R.id.btn_login);
        btn.setOnLoginListener((c, message, data) -> {
            if (c == 200) {
                Intent intent = new Intent(WechatAuthActivity.this, MainActivity.class);
                intent.putExtra("user", data);
                startActivity(intent);
                finish();
            }
        });
    }
}