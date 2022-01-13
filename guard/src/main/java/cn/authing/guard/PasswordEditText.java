package cn.authing.guard;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;

import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.Safe;
import cn.authing.guard.internal.BasePasswordEditText;

public class PasswordEditText extends BasePasswordEditText implements TextWatcher {

    public PasswordEditText(Context context) {
        this(context, null);
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Analyzer.report("PasswordEditText");
//        getEditText().setText(Safe.loadPassword());
    }

    protected int getDefaultHintResId() {
        return R.string.authing_password_edit_text_hint;
    }
}
