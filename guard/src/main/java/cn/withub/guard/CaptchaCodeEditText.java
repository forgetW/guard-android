package cn.withub.guard;

import static cn.withub.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.withub.guard.analyze.Analyzer;
import cn.withub.guard.internal.EditTextLayout;

public class CaptchaCodeEditText extends EditTextLayout implements TextWatcher {

    public CaptchaCodeEditText(@NonNull Context context) {
        this(context, null);
    }

    public CaptchaCodeEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptchaCodeEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Analyzer.report("CaptchaCodeEditText");
        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "hint") == null) {
            getEditText().setHint(context.getString(R.string.authing_captcha_code_edit_text_hint));
        }
    }
}
