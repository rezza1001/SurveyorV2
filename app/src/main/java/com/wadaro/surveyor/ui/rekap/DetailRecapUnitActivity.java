package com.wadaro.surveyor.ui.rekap;

import android.content.Intent;
import android.os.Bundle;

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

public class DetailRecapUnitActivity extends MyActivity {

    private ArrayList<Bundle> promotorList = new ArrayList<>();
    private PromotorAdapter adapter;

    @Override
    protected int setLayout() {
        return R.layout.rekap_activity_unitpromotor;
    }

    @Override
    protected void initLayout() {
        RecyclerView rcvw_data = findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));

        adapter = new PromotorAdapter(promotorList);
        rcvw_data.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        promotorList.clear();
        String sData = getIntent().getStringExtra("data");
        try {
            JSONArray ja = new JSONArray(sData);
            for (int i=0; i<ja.length(); i++){
                String promotor = ja.getJSONObject(i).getString("promotor_id");
                if (ja.getJSONObject(i).has("promotor_name")){
                    promotor = ja.getJSONObject(i).getString("promotor_name");
                }
                JSONArray detail = new JSONArray(ja.getJSONObject(i).getString("details"));
                int qty = detail.length();
                Bundle bundle = new Bundle();
                bundle.putString("promotor", promotor);
                bundle.putString("data", detail.toString());
                bundle.putInt("qty", qty);
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
        adapter.setOnSelectedListener(data -> {
            Intent intent = new Intent(mActivity, ProductPromotorActivity.class);
            intent.putExtra("data", data.getString("data"));
            intent.putExtra("promotor", data.getString("promotor"));
            startActivity(intent);
        });
    }
}
