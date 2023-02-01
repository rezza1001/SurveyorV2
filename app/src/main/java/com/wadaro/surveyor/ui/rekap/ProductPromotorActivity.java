package com.wadaro.surveyor.ui.rekap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.module.Utility;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Mochamad Rezza Gumilang on 01/Nov/2021.
 * Class Info :
 */

public class ProductPromotorActivity extends MyActivity {

    private TextView txvw_name;
    private ArrayList<Bundle> promotorList = new ArrayList<>();
    private PromotorProductAdapter adapter;

    @Override
    protected int setLayout() {
        return R.layout.rekap_activity_productpromotor;
    }

    @Override
    protected void initLayout() {
        txvw_name = findViewById(R.id.txvw_name);

        RecyclerView rcvw_data = findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));

        adapter = new PromotorProductAdapter(promotorList);
        rcvw_data.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void initData() {
        promotorList.clear();
        txvw_name.setText(getIntent().getStringExtra("promotor"));

        String sData = getIntent().getStringExtra("data");
        try {
            JSONArray ja = new JSONArray(sData);
            for (int i=0; i<ja.length(); i++){

                Bundle bundle = new Bundle();
                bundle.putString("product", ja.getJSONObject(i).getString("product_name"));
                bundle.putString("price",  ja.getJSONObject(i).getString("selling_price"));
                bundle.putString("qty",  ja.getJSONObject(i).getString("qty"));
                bundle.putString("total",  ja.getJSONObject(i).getString("subtotal"));
                promotorList.add(bundle);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.mrly_back_00).setOnClickListener(v -> onBackPressed());
    }
}
