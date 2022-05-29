package cn.withub.guard;

import static cn.withub.guard.util.Const.NS_ANDROID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.withub.guard.activity.AuthActivity;
import cn.withub.guard.activity.IndexAuthActivity;
import cn.withub.guard.analyze.Analyzer;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.flow.AuthFlow;
import cn.withub.guard.internal.LoadingButton;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.util.Util;
import cn.withub.guard.util.Validator;

public class ResetPasswordButton extends LoadingButton {

    public ResetPasswordButton(@NonNull Context context) {
        this(context, null);
    }

    public ResetPasswordButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public ResetPasswordButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("ResetPasswordButton");

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(getResources().getString(R.string.authing_reset_password));
        }

        loading.setTint(Color.WHITE);

        setOnClickListener(this::click);
    }

    private void click(View clickedView) {
        if (!(getContext() instanceof AuthActivity)) {
            return;
        }

        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);

        View v = Util.findViewByClass(this, PasswordEditText.class);
        if (v != null) {
            String password = Util.getPassword(this);
            if (!TextUtils.isEmpty(password)) {
                handleResetPassword(flow, password);
            }
        } else {
            View view = Util.findViewByClass(this, PhoneNumberEditText.class);
            if (view != null) {
                next(flow, flow.getResetPasswordByPhoneLayoutId());
                return;
            }

            view = Util.findViewByClass(this, AccountEditText.class);
            if (view != null) {
                EditText editText = ((AccountEditText) view).getEditText();
                String s = editText.getText().toString();
                if (Validator.isValidEmail(s)) {
                    setEnabled(false);
                    startLoadingVisualEffect();
                    AuthClient.sendResetPasswordEmail(s, (code, message, data) -> ((Activity) getContext()).runOnUiThread(() -> {
                        stopLoadingVisualEffect();
                        setEnabled(true);
                        if (code == 200) {
                            AuthFlow.put(getContext(), AuthFlow.KEY_ACCOUNT, s);
                            next(flow, flow.getResetPasswordByEmailLayoutId());
                        } else {
                            Util.setErrorText(this, message);
                        }
                    }));
                } else if (Validator.isValidPhoneNumber(s)) {
                    AuthFlow.put(getContext(), AuthFlow.KEY_ACCOUNT, s);
                    next(flow, flow.getResetPasswordByPhoneLayoutId());
                }
            }
        }
    }

    private void handleResetPassword(AuthFlow flow, String password) {
        Object o = flow.getData().get(AuthFlow.KEY_USER_INFO);
        if (o instanceof UserInfo) {
            UserInfo userInfo = (UserInfo) o;
            if (!Util.isNull(userInfo.getFirstTimeLoginToken())) {
                resetPasswordByFirstTimeLoginToken(userInfo.getFirstTimeLoginToken(), password);
                return;
            }
        }

        String account = Util.getAccount(this);
        if (Validator.isValidEmail(account)) {
            resetPasswordByEmail(account, password);
        } else if (Validator.isValidPhoneNumber(account)) {
            resetPasswordByPhone(account, password);
        }
    }

    private void resetPasswordByFirstTimeLoginToken(String token, String password) {
        startLoadingVisualEffect();
        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
        AuthClient.resetPasswordByFirstTimeLoginToken(token, password, (code, message, data)-> activity.runOnUiThread(()->{
            if (code == 200) {
                gotoLogin(flow);
            } else {
                Util.setErrorText(this, message);
            }
            stopLoadingVisualEffect();
        }));
    }

    private void resetPasswordByPhone(String phone, String password) {
        startLoadingVisualEffect();
        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
        String vCode = Util.getVerifyCode(this);
        CountryCodePicker countryCodePicker = (CountryCodePicker)Util.findViewByClass(this, CountryCodePicker.class);
        String phoneCountryCode = (null == countryCodePicker) ? null :  countryCodePicker.getCountryCode();
        AuthClient.resetPasswordByPhoneCode(phoneCountryCode, phone, vCode, password, (code, message, data)-> activity.runOnUiThread(()->{
            if (code == 200) {
                gotoLogin(flow);
            } else {
                Util.setErrorText(this, message);
            }
            stopLoadingVisualEffect();
        }));
    }

    private void resetPasswordByEmail(String email, String password) {
        startLoadingVisualEffect();
        AuthActivity activity = (AuthActivity) getContext();
        AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
        String vCode = Util.getVerifyCode(this);
        AuthClient.resetPasswordByEmailCode(email, vCode, password, (code, message, data)-> activity.runOnUiThread(()->{
            if (code == 200) {
                gotoLogin(flow);
            } else {
                Util.setErrorText(this, message);
            }
            stopLoadingVisualEffect();
        }));
    }

    private void next(AuthFlow flow, int layout) {
        Intent intent = new Intent(getContext(), AuthActivity.class);
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, layout);
        getContext().startActivity(intent);
    }

    private void gotoLogin(AuthFlow flow) {
        AuthActivity activity = (AuthActivity) getContext();

        Intent intent = new Intent(getContext(), IndexAuthActivity.class);
        intent.putExtra(AuthActivity.AUTH_FLOW, flow);
        intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getIndexLayoutId());
        getContext().startActivity(intent);
        activity.finish();
    }
}
