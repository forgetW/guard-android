package cn.withub.guard.social;

import android.content.Context;
import android.util.AttributeSet;

import cn.withub.guard.R;

public class GoogleLoginButton extends SocialLoginButton {

    public GoogleLoginButton(Context context) {
        this(context, null);
    }

    public GoogleLoginButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GoogleLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setImageResource(R.drawable.ic_authing_google);
    }

    @Override
    protected SocialAuthenticator createAuthenticator() {
        return new cn.withub.guard.social.Google();
    }
}
