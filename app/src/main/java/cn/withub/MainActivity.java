package cn.withub;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import cn.withub.guard.activity.UserProfileActivity;
import cn.withub.guard.network.AuthClient;
import cn.withub.guard.util.ALog;

public class MainActivity extends UserProfileActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> logout());
        }
        if (deleteButton != null) {
            deleteButton.setOnClickListener(v -> delete());
        }
    }

    private void logout() {
        long now = System.currentTimeMillis();
//        Push.unregister(this, ((ok, msg) -> {
//            ALog.d("MainActivity", "Push.unregister cost:" + (System.currentTimeMillis() - now));
            AuthClient.logout((code, message, data)->{
                Intent intent = new Intent(this, SampleListActivity.class);
                startActivity(intent);
                finish();
            });
//        }));
    }

    private void delete() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(cn.withub.guard.R.string.authing_delete_account).setMessage(cn.withub.guard.R.string.authing_delete_account_tip)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteConfirmed())
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void deleteConfirmed() {
//        Push.unregister(this, ((ok, msg) -> AuthClient.deleteAccount((code, message, data) -> {
//            if (code == 200) {
//                finish();
//            } else {
//                runOnUiThread(()-> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
//            }
//        })));
    }
}
