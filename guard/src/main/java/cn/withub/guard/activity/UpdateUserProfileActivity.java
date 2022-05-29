package cn.withub.guard.activity;

import android.os.Bundle;

import org.json.JSONObject;

import cn.withub.guard.Authing;
import cn.withub.guard.R;
import cn.withub.guard.data.UserInfo;
import cn.withub.guard.internal.EditTextLayout;
import cn.withub.guard.internal.LoadingButton;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.util.Util;

public class UpdateUserProfileActivity extends BaseAuthActivity {

    private String key;
    private UserInfo.CustomData data;
    private LoadingButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_user_profile_update);

        key = getIntent().getStringExtra("key");
        String label = getIntent().getStringExtra("label");
        data = (UserInfo.CustomData) getIntent().getSerializableExtra("data");


        EditTextLayout et = findViewById(R.id.et_value);
        if (label != null) {
            et.getEditText().setHint(label);
        }

        if (key != null) {
            UserInfo userInfo = Authing.getCurrentUser();
            if(userInfo != null){
                String v = userInfo.getMappedData(key);
                et.getEditText().setText(Util.isNull(v) ? "" : v);
            }
        } else if (data != null) {
            et.getEditText().setText(data.getValue());
        }

        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> {
            try {
                btnSubmit.startLoadingVisualEffect();
                if (data != null) {
                    updateCustomData(et.getEditText().getText().toString());
                } else {
                    updateUserInfo(et.getEditText().getText().toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateUserInfo(String value) throws Exception {
        JSONObject object = new JSONObject();
        object.put(key, value);
        AuthClient.updateProfile(object, (code, message, res) -> {
            btnSubmit.stopLoadingVisualEffect();
            if (code == 200) {
                finish();
            } else {
                Util.setErrorText(btnSubmit, message);
            }
        });
    }

    private void updateCustomData(String value) throws Exception {
        JSONObject object = new JSONObject();
        object.put(data.getKey(), value);
        AuthClient.setCustomUserData(object, (code, message, res) -> {
            btnSubmit.stopLoadingVisualEffect();
            if (code == 200) {
                UserInfo user = Authing.getCurrentUser();
                user.setCustomData(data.getKey(), value);
                finish();
            } else {
                Util.setErrorText(btnSubmit, message);
            }
        });
    }
}