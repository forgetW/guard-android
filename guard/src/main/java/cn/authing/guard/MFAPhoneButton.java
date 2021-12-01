package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.data.UserInfo;
import cn.authing.guard.flow.AuthFlow;
import cn.authing.guard.internal.LoadingButton;
import cn.authing.guard.network.AuthClient;
import cn.authing.guard.util.Util;

public class MFAPhoneButton extends LoadingButton {

    public MFAPhoneButton(@NonNull Context context) {
        this(context, null);
    }

    public MFAPhoneButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public MFAPhoneButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getResources().getString(R.string.authing_bind));
        }

        loading.setTint(Color.WHITE);

        if (context instanceof AuthActivity) {
            setOnClickListener(this::click);
            AuthActivity activity = (AuthActivity) context;
            AuthFlow flow = activity.getFlow();
            String phone = (String) flow.getData().get(AuthFlow.KEY_MFA_PHONE);
            if (!TextUtils.isEmpty(phone)) {
                startLoadingVisualEffect();
                AuthClient.sendSms(phone, (code, message, data)-> activity.runOnUiThread(this::stopLoadingVisualEffect));
            }
        }
    }

    private void click(View clickedView) {
        if (!(getContext() instanceof AuthActivity)) {
            return;
        }

        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = activity.getFlow();

        View v = Util.findViewByClass(this, VerifyCodeEditText.class);
        if (v != null) {
            String phone = (String) flow.getData().get(AuthFlow.KEY_MFA_PHONE);
            VerifyCodeEditText editText = (VerifyCodeEditText)v;
            String verifyCode = editText.getText().toString();
            startLoadingVisualEffect();
            AuthClient.mfaVerifyByPhone(phone, verifyCode, (code, message, data)-> activity.runOnUiThread(()-> mfaDone(code, message, data)));
        } else {
            v = Util.findViewByClass(this, PhoneNumberEditText.class);
            if (v != null) {
                PhoneNumberEditText editText = (PhoneNumberEditText) v;
                String phone = editText.getText().toString();
                flow.getData().put(AuthFlow.KEY_MFA_PHONE, phone);
                startLoadingVisualEffect();
                AuthClient.mfaCheck(phone, null, (code, message, data) -> {
                    if (code == 200) {
                        try {
                            boolean ok = data.getBoolean("result");
                            if (ok) {
                                sendSms(flow, phone);
                            } else {
                                stopLoadingVisualEffect();
                                editText.showError(activity.getString(R.string.authing_phone_number_already_bound));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            stopLoadingVisualEffect();
                            Util.setErrorText(this, e.toString());
                        }
                    } else {
                        stopLoadingVisualEffect();
                        Util.setErrorText(this, message);
                    }
                });
            }
        }
    }

    private void sendSms(AuthFlow flow, String phone) {
        AuthActivity activity = (AuthActivity) getContext();
        AuthClient.sendSms(phone, (code, message, data)-> activity.runOnUiThread(()->{
            stopLoadingVisualEffect();
            next(flow);
        }));
    }

    private void next(AuthFlow flow) {
        AuthActivity activity = (AuthActivity) getContext();

        int step = flow.getMfaPhoneCurrentStep();
        flow.setMfaPhoneCurrentStep(step++);

        Intent intent = new Intent(getContext(), AuthActivity.class);
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        int[] ids = flow.getMfaPhoneLayoutIds();
        if (step < ids.length) {
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, ids[step]);
        } else {
            // fallback to our default
            intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, R.layout.authing_mfa_phone_1);
        }
        activity.startActivityForResult(intent, AuthActivity.RC_LOGIN);
    }

    private void mfaDone(int code, String message, JSONObject data) {
        stopLoadingVisualEffect();
        if (code == 200) {
            try {
                AuthActivity activity = (AuthActivity) getContext();
                UserInfo userInfo = UserInfo.createUserInfo(data);
                Intent intent = new Intent();
                intent.putExtra("user", userInfo);
                activity.setResult(AuthActivity.OK, intent);
                activity.finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (code == 500 && message.startsWith("duplicate key value violates unique constraint")) {
            Util.setErrorText(this, "Phone number already bound by another user");
        } else {
            Util.setErrorText(this, message);
        }
    }
}
