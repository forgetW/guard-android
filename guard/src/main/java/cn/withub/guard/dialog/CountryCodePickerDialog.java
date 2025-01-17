package cn.withub.guard.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import cn.withub.guard.R;
import cn.withub.guard.data.Country;


public class CountryCodePickerDialog extends Dialog implements View.OnClickListener,
        LetterSideBar.OnTouchingLetterChangedListener, AdapterView.OnItemClickListener {

    private CountryCodePickerAdapter mAdapter;
    private List<Country> mDataList;

    private boolean IS_SEARCH_ICON = true;

    private ImageView imgSearch;
    private ImageView imgBack;
    private TextView txtTittle;
    private EditText etSearch;
    private TextView txtCancel;
    private ListView lvArea;
    private LetterSideBar sbIndex;
    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!"".equals(s.toString().trim())) {
                filterContacts(s.toString().trim());
                sbIndex.setVisibility(View.GONE);
            } else {
                sbIndex.setVisibility(View.VISIBLE);
                mAdapter.updateListView(mDataList);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private OnDialogClickListener mListener;

    public CountryCodePickerDialog(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public CountryCodePickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView(context);
    }

    protected CountryCodePickerDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.height = d.getHeight();
        p.width = d.getWidth();
        getWindow().setAttributes(p);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.authing_country_code_picker_dialog, null);
        setContentView(view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        imgSearch = findViewById(R.id.img_search);
        imgBack = findViewById(R.id.img_back);
        txtTittle = findViewById(R.id.txt_tittle);
        etSearch = findViewById(R.id.et_search);
        txtCancel = findViewById(R.id.txt_cancel);
        lvArea = findViewById(R.id.lv_area);
        sbIndex = findViewById(R.id.sb_index);

        imgSearch.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        txtCancel.setOnClickListener(this);

        lvArea.setVerticalScrollBarEnabled(false);
        lvArea.setFastScrollEnabled(false);
        lvArea.setOnItemClickListener(this);

        sbIndex.setOnTouchingLetterChangedListener(this);

        etSearch.addTextChangedListener(mTextWatcher);
    }

    public void setOnDialogClickListener(OnDialogClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onTouchingLetterChanged(String str) {
        if (mDataList.size() > 0) {
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).getFirstSpell().compareToIgnoreCase(str) == 0) {
                    lvArea.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<Country> bs = mAdapter.getList();
        Country bean = bs.get(position);
        if (mListener != null) {
            mListener.onItemSelected(bean);
        }
        dismiss();
    }

    public void setData(List<Country> dataList) {
        if (dataList == null) {
            return;
        }
        if (mDataList != null) {
            mDataList.clear();
        }
        mDataList = dataList;
        dataList.sort(new Country.ComparatorPY());
        mAdapter = new CountryCodePickerAdapter(dataList);
        lvArea.setAdapter(mAdapter);
    }

    private void filterContacts(String filterStr) {
        ArrayList<Country> filters = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {
            Country country = mDataList.get(i);
            if (country == null) {
                continue;
            }
            if (isStrInString(country.getNamePy(), filterStr)
                    || country.getName().contains(filterStr)
                    || isStrInString(country.getEnName(), filterStr)
                    || country.getCode().contains(filterStr)) {
                filters.add(country);
            }
        }
        mAdapter.updateListView(filters);
    }

    public boolean isStrInString(String bigStr, String smallStr) {
        return bigStr.toUpperCase().contains(smallStr.toUpperCase());
    }

    private void changeMode() {
        if (IS_SEARCH_ICON) {
            imgSearch.setVisibility(View.VISIBLE);
            imgBack.setVisibility(View.VISIBLE);
            etSearch.setText("");
            etSearch.setVisibility(View.GONE);
            txtTittle.setVisibility(View.VISIBLE);
            txtCancel.setVisibility(View.GONE);
        } else {
            imgSearch.setVisibility(View.GONE);
            imgBack.setVisibility(View.GONE);
            etSearch.setText("");
            etSearch.setVisibility(View.VISIBLE);
            etSearch.setFocusable(true);
            txtTittle.setVisibility(View.GONE);
            txtCancel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_back) {
            dismiss();
        } else if (v.getId() == R.id.img_search
                || v.getId() == R.id.txt_cancel) {
            IS_SEARCH_ICON = !IS_SEARCH_ICON;
            changeMode();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!IS_SEARCH_ICON){
            IS_SEARCH_ICON = true;
            changeMode();
            return;
        }
        dismiss();
    }

    public interface OnDialogClickListener {

        void onItemSelected(Country country);

    }
}
