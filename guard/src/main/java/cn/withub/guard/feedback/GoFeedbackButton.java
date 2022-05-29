package cn.withub.guard.feedback;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.withub.guard.R;
import cn.withub.guard.activity.AuthActivity;
import cn.withub.guard.activity.FeedbackActivity;
import cn.withub.guard.analyze.Analyzer;
import cn.withub.guard.flow.AuthFlow;
import cn.withub.guard.internal.GoSomewhereButton;

public class GoFeedbackButton extends GoSomewhereButton {

    public GoFeedbackButton(@NonNull Context context) {
        this(context, null);
    }

    public GoFeedbackButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public GoFeedbackButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Analyzer.report("GoHelpButton");

        setOnClickListener((v)->{
            if (context instanceof AuthActivity) {
                AuthActivity activity = (AuthActivity)context;
                AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                Intent intent = new Intent(getContext(), FeedbackActivity.class);
                intent.putExtra(AuthActivity.AUTH_FLOW, flow);
                intent.putExtra(AuthActivity.CONTENT_LAYOUT_ID, flow.getAuthHelpLayoutId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    protected String getDefaultText() {
        return getResources().getString(R.string.authing_feedback);
    }
}
