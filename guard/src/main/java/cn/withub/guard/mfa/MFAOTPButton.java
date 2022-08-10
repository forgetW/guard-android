package cn.withub.guard.mfa;

import static cn.withub.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;

import cn.withub.guard.R;
import cn.withub.guard.VerifyCodeEditText;
import cn.withub.guard.activity.AuthActivity;
import cn.withub.guard.analyze.Analyzer;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.internal.LoadingButton;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.util.Util;

public class MFAOTPButton extends MFABaseButton implements AuthActivity.EventListener {

    public MFAOTPButton(@NonNull Context context) {
        this(context, null);
    }

    public MFAOTPButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public MFAOTPButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("MFAOTPButton");

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getResources().getString(android.R.string.ok));
        }

        loading.setTint(Color.WHITE);

        if (context instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) getContext();
            activity.subscribe(AuthActivity.EVENT_VERIFY_CODE_ENTERED, this);
            setOnClickListener(this::click);
        }
    }

    private void click(View clickedView) {
        doMFA();
    }

    private void doMFA() {
        if (!(getContext() instanceof AuthActivity)) {
            return;
        }
        AuthActivity activity = (AuthActivity) getContext();
        View v = Util.findViewByClass(this, VerifyCodeEditText.class);
        if (v != null) {
            VerifyCodeEditText editText = (VerifyCodeEditText) v;
            String verifyCode = editText.getText().toString();
            startLoadingVisualEffect();
            AuthClient.mfaVerifyByOTP(verifyCode, (code, message, data) -> activity.runOnUiThread(() -> mfaDone(code, message, data)));
        }
    }

    private void mfaDone(int code, String message, UserInfo userInfo) {
        stopLoadingVisualEffect();
        if (code == 200) {
            mfaOk(code, message, userInfo);
        } else {
            Util.setErrorText(this, message);
        }
    }

    @Override
    public void happened(String what) {
        doMFA();
    }
}
