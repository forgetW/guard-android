package cn.withub.guard.handler.register;

import android.text.TextUtils;
import android.view.View;

import cn.withub.guard.Authing;
import cn.withub.guard.CountryCodePicker;
import cn.withub.guard.PasswordEditText;
import cn.withub.guard.PhoneNumberEditText;
import cn.withub.guard.R;
import cn.withub.guard.RegisterButton;
import cn.withub.guard.VerifyCodeEditText;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.network.OIDCClient;
import cn.withub.guard.util.ALog;
import cn.withub.guard.util.Util;

public class PhoneCodeRegisterHandler extends AbsRegisterHandler {


    public PhoneCodeRegisterHandler(RegisterButton loginButton, IRegisterRequestCallBack callback) {
        super(loginButton, callback);
    }

    @Override
    protected boolean register() {
        View phoneCountryCodeET = Util.findViewByClass(mRegisterButton, CountryCodePicker.class);
        View phoneNumberET = Util.findViewByClass(mRegisterButton, PhoneNumberEditText.class);
        View phoneCodeET = Util.findViewByClass(mRegisterButton, VerifyCodeEditText.class);

        if (phoneNumberET != null && phoneNumberET.isShown()
                && phoneCodeET != null && phoneCodeET.isShown()) {

            String phoneCountryCode = "";
            if (phoneCountryCodeET != null && phoneCountryCodeET.isShown()){
                CountryCodePicker countryCodePicker = (CountryCodePicker)phoneCountryCodeET;
                phoneCountryCode = countryCodePicker.getCountryCode();
                if (TextUtils.isEmpty(phoneCountryCode)) {
                    Util.setErrorText(mRegisterButton, mContext.getString(R.string.authing_invalid_phone_country_code));
                    fireCallback(mContext.getString(R.string.authing_invalid_phone_country_code));
                    return false;
                }
            }

            PhoneNumberEditText phoneNumberEditText = (PhoneNumberEditText)phoneNumberET;
            if (!phoneNumberEditText.isContentValid()) {
                Util.setErrorText(mRegisterButton, mContext.getString(R.string.authing_invalid_phone_number));
                fireCallback(mContext.getString(R.string.authing_invalid_phone_number));
                return false;
            }

            final String phone = phoneNumberEditText.getText().toString();
            final String code = ((VerifyCodeEditText) phoneCodeET).getText().toString();
            if (TextUtils.isEmpty(code)) {
                Util.setErrorText(mRegisterButton, mContext.getString(R.string.authing_incorrect_verify_code));
                fireCallback(mContext.getString(R.string.authing_incorrect_verify_code));
                return false;
            }

            mRegisterButton.startLoadingVisualEffect();
            registerByPhoneCode(phoneCountryCode, phone, code, "");
            return true;
        }
        return false;
    }


    private void registerByPhoneCode(String phoneCountryCode, String phone, String phoneCode, String password) {
        Authing.AuthProtocol authProtocol = getAuthProtocol();
        if (authProtocol == Authing.AuthProtocol.EInHouse) {
            AuthClient.registerByPhoneCode(phoneCountryCode, phone, phoneCode, password, this::fireCallback);
        } else if (authProtocol == Authing.AuthProtocol.EOIDC) {
            new OIDCClient().registerByPhoneCode(phoneCountryCode, phone, phoneCode, password, this::fireCallback);
        }
        ALog.d(TAG, "register by phone code");
    }

}
