package cn.withub.guard.complete;

import static cn.withub.guard.flow.AuthFlow.KEY_EXTENDED_FIELDS;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import cn.withub.guard.Authing;

import cn.withub.guard.Authing;
import cn.withub.guard.MandatoryField;
import cn.withub.guard.activity.AuthActivity;
import cn.withub.guard.analyze.Analyzer;
import cn.withub.guard.data.ExtendedField;
import cn.withub.guard.flow.AuthFlow;
import cn.withub.guard.util.Util;

public class UserInfoCompleteContainer extends LinearLayout {
    public UserInfoCompleteContainer(Context context) {
        this(context, null);
    }

    public UserInfoCompleteContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserInfoCompleteContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public UserInfoCompleteContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        Analyzer.report("UserInfoCompleteContainer");

        setOrientation(VERTICAL);

        if (context instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) context;
            AuthFlow flow = activity.getFlow();
            Object o = flow.getData().get(KEY_EXTENDED_FIELDS);
            if (o instanceof List) {
                List<?> extendedFields = (List<?>)o;
                for (Object obj : extendedFields) {
                    ExtendedField field = (ExtendedField)obj;
                    View view = null;
                    if ("text".equals(field.getInputType())) {
                        view = inflateItem(flow.getUserInfoCompleteItemNormal(), field);
                    } else if ("email".equals(field.getInputType())) {
                        view = inflateItem(flow.getUserInfoCompleteItemEmail(), field);
                    } else if ("phone".equals(field.getInputType())) {
                        view = inflateItem(flow.getUserInfoCompleteItemPhone(), field);
                    } else if ("select".equals(field.getInputType())) {
                        view = inflateItem(flow.getUserInfoCompleteItemSelect(), field);
                    } else if ("datetime".equals(field.getInputType())) {
                        view = inflateItem(flow.getUserInfoCompleteItemDatePicker(), field);
                    }

                    if (view != null) {
                        addView(view);
                    }
                }
            }
        }
    }

    private View inflateItem(int layoutId, ExtendedField field) {
        if (layoutId == 0) {
            return null;
        }

        View view = View.inflate(getContext(), layoutId, null);
        if (!(view instanceof UserInfoFieldForm)) {
            return view;
        }

        UserInfoFieldForm form = (UserInfoFieldForm) view;
        form.setField(field);
        if (form.getChildCount() == 0) {
            return form;
        }

        View child = form.getChildAt(0);
        if (child instanceof MandatoryField) {
            MandatoryField label = (MandatoryField) child;
            if (!field.isRequired()) {
                label.setAsteriskPosition(0);
            }
            setLabelName(label, field);
        }
        return view;
    }

    private void setLabelName(MandatoryField label, ExtendedField field){
        label.setMandatoryText(field.getLabel());
        String name = field.getName();
        if (Util.isNull(name)){
            return;
        }

        String language = Util.getAppLanguage();
        Authing.getPublicConfig(config -> {
            JSONObject extendedFieldsI18n = config.getExtendedFieldsI18n();
            if (extendedFieldsI18n == null){
                return;
            }
            if (extendedFieldsI18n.has(name)){
                try {
                    JSONObject nameObj = extendedFieldsI18n.getJSONObject(name);
                    if (nameObj.has(language)){
                        JSONObject languageObj = nameObj.getJSONObject(language);
                        boolean enabled = languageObj.getBoolean("enabled");
                        if (enabled){
                            label.setMandatoryText(languageObj.getString("value"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public List<ExtendedField> getValues() {
        List<ExtendedField> fields = new ArrayList<>();
        for (int i = 0;i < getChildCount();++i) {
            View v = getChildAt(i);
            if (v instanceof UserInfoFieldForm) {
                UserInfoFieldForm form = (UserInfoFieldForm) v;
                ExtendedField field = form.getFieldWithValue();
                fields.add(field);
            }
        }
        return fields;
    }
}
