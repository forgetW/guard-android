package cn.withub.guard.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import cn.withub.guard.R;
import cn.withub.guard.feedback.ImagePickerView;

public class FeedbackActivity extends AuthActivity {

    public static final int SELECT_PICTURE = 1000;

    private ImagePickerView pickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.authing_feedback);
        pickerView = findViewById(R.id.gv_image_picker);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    pickerView.imagePicked(selectedImageUri);
                }
            }
        }
    }
}