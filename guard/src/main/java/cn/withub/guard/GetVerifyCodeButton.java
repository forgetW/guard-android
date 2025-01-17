package cn.withub.guard;

import static cn.withub.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.withub.guard.analyze.Analyzer;
import cn.withub.guard.internal.LoadingButton;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.util.GlobalCountDown;
import cn.withub.guard.util.Util;

public class GetVerifyCodeButton extends LoadingButton {

    private String countDownTip;

    private String text;

    public GetVerifyCodeButton(@NonNull Context context) {
        this(context, null);
    }

    public GetVerifyCodeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GetVerifyCodeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("GetVerifyCodeButton");

        loadingLocation = OVER; // over on top since this button is usually small

        countDownTip = context.getString(R.string.authing_resend_after);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            text = getContext().getString(R.string.authing_get_verify_code);
            setText(text);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textSize") == null) {
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        }

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "background") == null) {
            setBackgroundResource(R.drawable.authing_verify_code_background);
        }

        if (GlobalCountDown.isCountingDown()) {
            countDown();
        }
        setOnClickListener((v -> getSMSCode()));
    }

    private void getSMSCode() {
        String phoneCountryCode = Util.getPhoneCountryCode(this);
        String phoneNumber = Util.getPhoneNumber(this);
        if (!TextUtils.isEmpty(phoneNumber)) {
            beforeSendSmsCode();
            AuthClient.sendSms(phoneCountryCode, phoneNumber, this::handleSMSResult);
        }
    }

    public void beforeSendSmsCode(){
        startLoadingVisualEffect();
        Util.setErrorText(this, null);
    }

    public void handleSMSResult(int code, String message, Object ignore) {
        post(()->{
            stopLoadingVisualEffect();
            if (code == 200) {
                // in stopLoadingVisualEffect it will setEnabled to true
                setEnabled(false);
                countDown();
                View v = Util.findViewByClass(this, VerifyCodeEditText.class);
                if (v != null) {
                    v.requestFocus();
                }
            } else {
                Util.setErrorText(this, message);
            }
        });
    }

    private void countDown() {
        if (GlobalCountDown.isCountingDown()) {
            updateCountDown();
            postDelayed(this::countDown, 1000);
        } else {
            setText(text);
            setEnabled(true);
        }
    }

    private void updateCountDown() {
        setEnabled(false);
        setText(String.format(countDownTip, GlobalCountDown.getFirstCountDown()));
    }

    public void setCountDownTip(String format) {
        countDownTip = format;
    }
}
