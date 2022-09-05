package cn.withub.guard.mfa;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cn.withub.guard.Authing;
import cn.withub.guard.activity.AuthActivity;
import cn.withub.guard.data.Config;
import cn.withub.guard.data.ExtendedField;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.flow.AuthFlow;
import cn.withub.guard.flow.FlowHelper;
import cn.withub.guard.internal.LoadingButton;


public class MFABaseButton extends LoadingButton {


    public MFABaseButton(@NonNull Context context) {
        super(context);
    }

    public MFABaseButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MFABaseButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    protected void mfaOk(int code, String message, UserInfo userInfo) {
        Authing.getPublicConfig((config)->{
            if (getContext() instanceof AuthActivity) {
                AuthActivity activity = (AuthActivity) getContext();
                AuthFlow flow = (AuthFlow) activity.getIntent().getSerializableExtra(AuthActivity.AUTH_FLOW);
                List<ExtendedField> missingFields = FlowHelper.missingFields(config, userInfo);
                if (shouldCompleteAfterLogin(config) && missingFields.size() > 0) {
                    flow.getData().put(AuthFlow.KEY_USER_INFO, userInfo);
                    FlowHelper.handleUserInfoComplete(this, missingFields);
                } else {
                    AuthFlow.Callback<UserInfo> cb = flow.getAuthCallback();
                    if (cb != null) {
                        cb.call(getContext(), code, message, userInfo);
                    }

                    Intent intent = new Intent();
                    intent.putExtra("user", userInfo);
                    activity.setResult(AuthActivity.OK, intent);
                    activity.finish();
                }
            }
        });
    }


    private boolean shouldCompleteAfterLogin(Config config) {
        List<String> complete = config.getCompleteFieldsPlace();
        return complete != null && complete.contains("login");
    }
}
