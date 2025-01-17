package cn.withub.guard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.withub.guard.activity.AuthActivity;
import cn.withub.guard.analyze.Analyzer;
import cn.withub.guard.flow.AuthFlow;
import cn.withub.guard.internal.GoSomewhereButton;

public class GoRegisterButton extends GoSomewhereButton {

    public GoRegisterButton(@NonNull Context context) {
        this(context, null);
    }

    public GoRegisterButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GoRegisterButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("GoRegisterButton");

        Authing.getPublicConfig((config)->{
            if (config != null && (config.getRegisterTabList() == null || config.getRegisterTabList().size() == 0)) {
                setVisibility(View.GONE);
            }
        });
    }

    protected String getDefaultText() {
        return getResources().getString(R.string.authing_register_now);
    }

    protected int getTargetLayoutId() {
        if (getContext() instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) getContext();
            AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
            return flow.getRegisterLayoutId();
        }
        return super.getTargetLayoutId();
    }
}
