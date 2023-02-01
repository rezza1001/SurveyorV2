package com.wadaro.surveyor.ui.product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductActivity extends MyActivity {


    private ProductAdapter mAdapter;
    private final ArrayList<Bundle> allData = new ArrayList<>();

    @Override
    protected int setLayout() {
        return R.layout.product_activity_main;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        RecyclerView rcvw_data_00 = findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new GridLayoutManager(mActivity,2));

        mAdapter = new ProductAdapter(mActivity, allData);
        rcvw_data_00.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        request();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        mAdapter.setOnSelectedListener(data -> {
            Intent intent = new Intent(mActivity, DetailProductActivity.class);
            intent.putExtra("data", data);
            startActivity(intent);
        });

    }
    private void request(){
        allData.clear();
        PostManager post = new PostManager(mActivity, "product-sales");
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONArray data = obj.getJSONArray("data");
                    for (int i=0; i<data.length(); i++){
                        JSONObject jo = data.getJSONObject(i);
                        Bundle bundle = new Bundle();
                        bundle.putString("product_name", jo.getString("product_name"));
                        bundle.putString("product_id", jo.getString("product_id"));
                        bundle.putString("price", jo.getString("price"));
                        bundle.putString("image", jo.getString("product_photo"));
                        allData.add(bundle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mAdapter.notifyDataSetChanged();
        });
    }


}
