package cn.withub.guard.social;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.withub.guard.R;
import cn.withub.guard.analyze.Analyzer;

public class WeComLoginButton extends SocialLoginButton {

    public WeComLoginButton(@NonNull Context context) {
        this(context, null);
    }

    @Override
    protected SocialAuthenticator createAuthenticator() {
        return new WeCom(type);
    }

    public WeComLoginButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public WeComLoginButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("WeComLoginButton");

        setImageResource(R.drawable.ic_authing_wecom);
    }
}
