package cn.withub.guard.profile;

import static cn.withub.guard.util.Const.NS_ANDROID;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.withub.guard.Authing;
import cn.withub.guard.R;
import cn.withub.guard.internal.PrimaryButton;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.util.Util;

public class ModifyPhoneNumberButton extends PrimaryButton {

    private boolean hasPhone;

    public ModifyPhoneNumberButton(@NonNull Context context) {
        this(context, null);
    }

    public ModifyPhoneNumberButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public ModifyPhoneNumberButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (Authing.getCurrentUser() != null && !Util.isNull(Authing.getCurrentUser().getPhone_number()))
            hasPhone = true;

        Authing.getPublicConfig(config -> {
            if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
                if (hasPhone) {
                    setText(R.string.authing_unbind);
                } else {
                    setText(R.string.authing_bind);
                }
            }

            setOnClickListener((v -> clicked()));
        });
    }

    public void clicked() {
        startLoadingVisualEffect();
        if (hasPhone) {
            AuthClient.unbindPhone((code, message, data)-> {
                handleResult(code, message);
            });
        } else {
            String phoneCountryCode = Util.getPhoneCountryCode(this);
            String phone = Util.getPhoneNumber(this);
            String vCode = Util.getVerifyCode(this);
            AuthClient.bindPhone(phoneCountryCode, phone, vCode, (code, message, data)-> {
                handleResult(code, message);
            });
        }
    }

    private void handleResult(int code, String message) {
        stopLoadingVisualEffect();
        if (code == 200) {
            ((Activity)getContext()).finish();
        } else {
            Util.setErrorText(this, message);
        }
    }
}
