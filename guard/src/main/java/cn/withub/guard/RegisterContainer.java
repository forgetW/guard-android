package cn.withub.guard;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import cn.withub.guard.analyze.Analyzer;

public class RegisterContainer extends LinearLayout {

    private RegisterType type;

    public RegisterContainer(Context context) {
        this(context, null);
    }

    public RegisterContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RegisterContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public enum RegisterType {
        EByPhoneCodePassword,
        EByEmailPassword,
        EByEmailCode,
    }

    public RegisterContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        Analyzer.report("RegisterContainer");

        setOrientation(VERTICAL);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RegisterContainer);
        int t = array.getInt(R.styleable.RegisterContainer_typeGuard,2);
        if (t == 2) {
            type = RegisterType.EByPhoneCodePassword;
        } else if (t == 3) {
            type = RegisterType.EByEmailPassword;
        } else if (t == 4) {
            type = RegisterType.EByEmailCode;
        }
        array.recycle();
    }


    public RegisterType getType() {
        return type;
    }
}
