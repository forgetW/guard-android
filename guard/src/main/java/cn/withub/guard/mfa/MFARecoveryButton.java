package cn.withub.guard.mfa;

import static cn.withub.guard.flow.AuthFlow.KEY_MFA_RECOVERY_CODE;
import static cn.withub.guard.flow.AuthFlow.KEY_USER_INFO;
import static cn.withub.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.withub.guard.R;
import cn.withub.guard.activity.AuthActivity;
import cn.withub.guard.analyze.Analyzer;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.flow.AuthFlow;
import cn.withub.guard.internal.LoadingButton;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.util.Util;

public class MFARecoveryButton extends MFABaseButton {

    private String recoveryCode;

    public MFARecoveryButton(@NonNull Context context) {
        this(context, null);
    }

    public MFARecoveryButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public MFARecoveryButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("MFARecoveryButton");

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getResources().getString(android.R.string.ok));
        }

        loading.setTint(Color.WHITE);

        if (!(context instanceof AuthActivity)) {
            return;
        }

        setOnClickListener(this::click);
        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = activity.getFlow();
        recoveryCode = (String) flow.getData().get(KEY_MFA_RECOVERY_CODE);
        if (!Util.isNull(recoveryCode)) {
            post(()->{
                View v = Util.findViewByClass(this, RecoveryCodeEditText.class);
                if (v != null) {
                    RecoveryCodeEditText editText = (RecoveryCodeEditText) v;
                    editText.getEditText().setText(recoveryCode);
                }
            });
        }
    }

    private void click(View clickedView) {
        if (recoveryCode == null) {
            doMFA();
        } else {
            View v = Util.findViewByClass(this, CheckBox.class);
            if (v == null) {
                done();
            } else {
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
                    done();
                }
            }
        }
    }

    private void doMFA() {
        if (!(getContext() instanceof AuthActivity)) {
            return;
        }
        AuthActivity activity = (AuthActivity) getContext();
        View v = Util.findViewByClass(this, RecoveryCodeEditText.class);
        if (v != null) {
            RecoveryCodeEditText editText = (RecoveryCodeEditText) v;
            String recoveryCode = editText.getText().toString();
            startLoadingVisualEffect();
            AuthClient.mfaVerifyByRecoveryCode(recoveryCode, (code, message, data) -> activity.runOnUiThread(() -> mfaDone(code, message, data)));
        }
    }

    private void mfaDone(int code, String message, UserInfo userInfo) {
        stopLoadingVisualEffect();
        if (code == 200) {
            try {
                AuthActivity activity = (AuthActivity) getContext();
                AuthFlow flow = activity.getFlow();
                flow.getData().put(KEY_MFA_RECOVERY_CODE, userInfo.getRecoveryCode());
                int step = flow.getMfaRecoveryCurrentStep();
                flow.setMfaRecoveryCurrentStep(step++);

                Intent intent = new Intent(getContext(), AuthActivity.class);
                intent.putExtra(AuthActivity.AUTH_FLOW, flow);
                int[] ids = flow.getMfaRecoveryLayoutIds();
                if (step < ids.length) {
                    intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[step]);
                } else {
                    // fallback to our default
                    intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, R.layout.authing_mfa_otp_recovery_1);
                }
                activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Util.setErrorText(this, message);
        }
    }

    private void done() {
        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = activity.getFlow();
        Intent intent = new Intent();
        intent.putExtra("user", (UserInfo) flow.getData().get(KEY_USER_INFO));
        activity.setResult(AuthActivity.OK, intent);
        activity.finish();
    }
}
