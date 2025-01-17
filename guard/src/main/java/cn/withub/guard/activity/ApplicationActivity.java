package cn.withub.guard.activity;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.withub.guard.Authing;
import cn.withub.guard.R;
import cn.withub.guard.data.Application;
import cn.withub.guard.data.UserInfo;

public class ApplicationActivity extends BaseAuthActivity {

    private List<Application> applications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authing_applications);

        UserInfo userInfo = Authing.getCurrentUser();
        if (userInfo != null) {
            applications = userInfo.getApplications();
        }

        if (applications != null) {
            ListView listView = findViewById(R.id.lv_apps);
            ApplicationAdapter adapter = new ApplicationAdapter();
            listView.setAdapter(adapter);
        }
    }

    private class ApplicationAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return applications.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(ApplicationActivity.this).inflate(R.layout.authing_app_item, parent, false);
            }

            Application data = applications.get(position);
            TextView tv = view.findViewById(R.id.tv_app_name);
            tv.setText(data.getName());
            tv = view.findViewById(R.id.tv_app_id);
            tv.setText(data.getId());
            return view;
        }
    }
}