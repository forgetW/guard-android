package cn.withub.guard.feedback;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import cn.withub.guard.R;
import cn.withub.guard.analyze.Analyzer;

public class FeedbackDescriptionEditText extends EditText {
    public FeedbackDescriptionEditText(Context context) {
        super(context, null);
    }

    public FeedbackDescriptionEditText(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.editTextStyle);
    }

    public FeedbackDescriptionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);

        Analyzer.report("HelpDescriptionEditText");
    }
}
