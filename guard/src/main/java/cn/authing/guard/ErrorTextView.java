package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ErrorTextView extends androidx.appcompat.widget.AppCompatTextView {

    public ErrorTextView(@NonNull Context context) {
        this(context, null);
    }

    public ErrorTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ErrorTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // hide by default
        setVisibility(View.GONE);

        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "textColor") == null) {
            setTextColor(context.getColor(R.color.authing_error));
        }
    }
}
