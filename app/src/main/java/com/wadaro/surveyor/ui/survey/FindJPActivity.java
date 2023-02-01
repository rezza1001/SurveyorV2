package com.wadaro.surveyor.ui.survey;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.component.SelectAdapter;
import com.wadaro.surveyor.component.SelectHolder;

import java.util.ArrayList;

public class FindJPActivity extends MyActivity {
    private static int REQ_NEW = 121;

    private SelectAdapter mAdapter;
    private EditText edtx_search_00;
    private ArrayList<SelectHolder> allData = new ArrayList<>();
    private ArrayList<SelectHolder> filterData = new ArrayList<>();

    @Override
    protected int setLayout() {
        return (R.layout.ui_survey_activity_findjp);
    }

    @Override
    protected void initLayout() {
        RelativeLayout rvly_header_10 = findViewById(R.id.rvly_header_10);
        rvly_header_10.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        RecyclerView rcvw_gov_00 = findViewById(R.id.rcvw_gov_00);
        mAdapter    = new SelectAdapter(mActivity,filterData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rcvw_gov_00.setLayoutManager(mLayoutManager);
        rcvw_gov_00.setItemAnimator(new DefaultItemAnimator());
        rcvw_gov_00.setAdapter(mAdapter);

        edtx_search_00  = findViewById(R.id.edtx_search_00);
        edtx_search_00.setHint("Cari JP...");

        ImageView imvw_back_00 = findViewById(R.id.imvw_back_00);
        imvw_back_00.setColorFilter(Color.WHITE);
    }

    @Override
    protected void initData() {
        allData.clear();
        filterData.clear();

        ArrayList<String> data = getIntent().getStringArrayListExtra("DATA");
        assert data != null;
        for (String sData : data){
            SelectHolder select = new SelectHolder(sData);
            allData.add(select);
        }
        filter("");

    }


    @Override
    protected void initListener() {
        edtx_search_00.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        mAdapter.setOnSelectedListener((pData, position) -> {
            Intent intent = new Intent();
            intent.putExtra("DATA", pData.toString());
            setResult(RESULT_OK, intent);
            finish();
        });

        findViewById(R.id.mrly_back_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.mrly_add_00).setOnClickListener(v -> {
            Intent intent = getIntent();
            intent.setClass(mActivity, AddJpActivity.class);
            startActivityForResult(intent, REQ_NEW);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_NEW && resultCode == RESULT_OK){
            assert data != null;
            new Handler().postDelayed(() -> {
                Intent intent = getIntent();
                intent.putExtra("DATA", new SelectHolder(data.getStringExtra("KEY"),data.getStringExtra("VALUE")).toString());
                setResult(RESULT_OK, intent);
                finish();
            }, 500);
        }
    }

    private void filter(String data){
        mAdapter.setKey(data);
        if (data.length() >= 1 ){
            filterData.clear();

            for (SelectHolder opt: allData){
                if (opt.value.toUpperCase().contains(data.toUpperCase())){
                    filterData.add(opt);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
        else {
            filterData.clear();
            filterData.addAll(allData);
            mAdapter.notifyDataSetChanged();
        }
    }
}
