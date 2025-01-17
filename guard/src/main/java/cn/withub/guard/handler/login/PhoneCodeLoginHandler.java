package cn.withub.guard.handler.login;

import android.text.TextUtils;
import android.view.View;

import cn.withub.guard.Authing;
import cn.withub.guard.CountryCodePicker;
import cn.withub.guard.LoginButton;
import cn.withub.guard.PhoneNumberEditText;
import cn.withub.guard.R;
import cn.withub.guard.VerifyCodeEditText;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.network.OIDCClient;
import cn.withub.guard.util.ALog;
import cn.withub.guard.util.Util;

import static cn.withub.guard.Authing.getAuthProtocol;

public class PhoneCodeLoginHandler extends AbsLoginHandler{

    private String phoneCountryCode;
    private String phoneNumber;
    private String phoneCode;

    public PhoneCodeLoginHandler(LoginButton loginButton, ILoginRequestCallBack callback) {
        super(loginButton, callback);
    }

    @Override
    protected boolean login() {
        View phoneCountryCodeET = Util.findViewByClass(loginButton, CountryCodePicker.class);
        View phoneNumberET = Util.findViewByClass(loginButton, PhoneNumberEditText.class);
        View phoneCodeET = Util.findViewByClass(loginButton, VerifyCodeEditText.class);
        if (phoneCountryCodeET != null && phoneCountryCodeET.isShown()){
            CountryCodePicker countryCodePicker = (CountryCodePicker)phoneCountryCodeET;
            phoneCountryCode = countryCodePicker.getCountryCode();
        }
        if (phoneNumberET != null && phoneNumberET.isShown()) {
            PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText)phoneNumberET;
            phoneNumber = phoneNumberEditText.getText().toString();
        }
        if (phoneCodeET != null && phoneCodeET.isShown()) {
            VerifyCodeEditText verifyCodeEditText = (VerifyCodeEditText)phoneCodeET;
            phoneCode = verifyCodeEditText.getText().toString();
        }
        if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(phoneCode)) {
            loginButton.startLoadingVisualEffect();
            loginByPhoneCode(phoneCountryCode, phoneNumber, phoneCode);
            return true;
        }

        if (phoneNumberET != null && phoneNumberET.isShown()
                && phoneCodeET != null && phoneCodeET.isShown()) {

            String countryCode = "";
            if (phoneCountryCodeET != null && phoneCountryCodeET.isShown()){
                CountryCodePicker countryCodePicker = (CountryCodePicker)phoneCountryCodeET;
                countryCode = countryCodePicker.getCountryCode();
                if (TextUtils.isEmpty(countryCode)) {
                    fireCallback(mContext.getString(R.string.authing_invalid_phone_country_code));
                    return false;
                }
            }

            PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText)phoneNumberET;
            if (!phoneNumberEditText.isContentValid()) {
                fireCallback(mContext.getString(R.string.authing_invalid_phone_number));
                return false;
            }

            final String phone = phoneNumberEditText.getText().toString();
            final String code = ((VerifyCodeEditText) phoneCodeET).getText().toString();
            if (TextUtils.isEmpty(code)) {
                fireCallback(mContext.getString(R.string.authing_incorrect_verify_code));
                return false;
            }

            loginButton.startLoadingVisualEffect();
            loginByPhoneCode(countryCode, phone, code);
            return true;
        }

        return false;
    }

    private void loginByPhoneCode(String phoneCountryCode, String phone, String verifyCode) {
        Authing.AuthProtocol authProtocol = getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            AuthClient.loginByPhoneCode(phoneCountryCode, phone, verifyCode, this::fireCallback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            new OIDCClient().loginByPhoneCode(phoneCountryCode, phone, verifyCode, this::fireCallback);
        }
        ALog.d(TAG, "login by phone code");
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
