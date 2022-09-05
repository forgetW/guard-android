package cn.withub.guard.oneclick;

import static cn.withub.guard.util.Const.NS_ANDROID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.withub.guard.R;
import cn.withub.guard.activity.AuthActivity;
import cn.withub.guard.analyze.Analyzer;
import cn.withub.guard.container.AuthContainer;
import cn.withub.guard.internal.PrimaryButton;
import cn.withub.guard.util.Util;

public class OneClickAuthButton extends PrimaryButton {

    private OneClick oneClick;

    public OneClickAuthButton(@NonNull Context context) {
        this(context, null);
    }

    public OneClickAuthButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public OneClickAuthButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Analyzer.report("OneClickAuthButton");

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "text") == null) {
            setText(R.string.authing_one_click);
        }

        setOnClickListener((v)->{
            startLoadingVisualEffect();
            if (null == oneClick){
                oneClick = new OneClick(context);
            }
            oneClick.start((code, message, userInfo)->{
                stopLoadingVisualEffect();
                if (code == 200 && userInfo != null) {
                    Intent intent = new Intent();
                    intent.putExtra("user", userInfo);
                    ((Activity)getContext()).setResult(AuthActivity.OK, intent);
                    ((Activity)getContext()).finish();
                } else {
                    post(() -> Util.setErrorText(v, message));
                }
            });
        });
    }

}
