package com.wadaro.surveyor.ui.survey;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.ObjectApi;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.component.SelectActivity;
import com.wadaro.surveyor.component.SelectHolder;
import com.wadaro.surveyor.component.SelectView;
import com.wadaro.surveyor.module.Utility;
import com.wadaro.surveyor.util.SpellingNumber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class AddOrderActivity extends MyActivity {
    private static final int REQ_NAMEJP = 2;
    private static final int REQ_GOODS = 3;
    private static final int REQ_QTY = 4;

    private TextView txvw_title_00;
    private SelectView slvw_jpname_00,slvw_goods_00,slvw_qty_00;
    private ArrayList<String> listProducts = new ArrayList<>();
    private ArrayList<String> listConsumers = new ArrayList<>();
    String tmpID = "";

    @Override
    protected int setLayout() {
        return R.layout.survey_activity_addorder;
    }

    @Override
    protected void initLayout() {
        txvw_title_00 = findViewById(R.id.txvw_titile_00);
        slvw_jpname_00 = findViewById(R.id.slvw_jpname_00);
        slvw_goods_00 = findViewById(R.id.slvw_goods_00);
        slvw_qty_00 = findViewById(R.id.slvw_qty_00);

        slvw_jpname_00.setHint("Nama JP / Calon Konsumen");
        slvw_goods_00.setHint("Nama Barang");
        slvw_qty_00.setHint("Quantity");

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        txvw_title_00.setText("Tambah Pesanan");
        if (Objects.requireNonNull(getIntent().getAction()).endsWith("EDIT")){
            txvw_title_00.setText("Ubah Pesanan");
            OrderHolder holder = new OrderHolder();
            holder.unPack(getIntent().getStringExtra("DATA"));
            tmpID = holder.tmpId;
            slvw_goods_00.setValue(new SelectHolder(holder.goods, holder.goodsName));
            slvw_jpname_00.setValue(new SelectHolder(holder.customer, holder.customerName));
            slvw_qty_00.setValue(new SelectHolder(holder.qty, holder.qty+" ( "+SpellingNumber.convert(Integer.parseInt(holder.qty),"ID")+" )"));
        }

        String param = "?sales_id="+getIntent().getStringExtra("ID");
        PostManager post = new PostManager(mActivity,"process-survey"+param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                listProducts.clear();
                listConsumers.clear();
                try {
                    JSONObject data = obj.getJSONObject("data");
                    JSONArray products = data.getJSONArray("products");
                    for (int i=0; i<products.length(); i++){
                        JSONObject prod = products.getJSONObject(i);
                        SelectHolder select = new SelectHolder();
                        select.id = prod.getString("product_id");
                        select.value = prod.getString("product_name");
                        listProducts.add(select.toString());
                    }
                    JSONArray consumers = data.getJSONArray("consumers");
                    for (int i=0; i<consumers.length(); i++){
                        JSONObject consumer = consumers.getJSONObject(i);
                        SelectHolder select = new SelectHolder();
                        select.id = consumer.getString("consumen_id");
                        select.value = consumer.getString("consumen_name");
                        listConsumers.add(select.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void initListener() {
        slvw_jpname_00.setSelectedListener(data -> {
            Intent intent = getIntent();
            intent.setClass(mActivity, FindJPActivity.class);
            intent.putStringArrayListExtra("DATA",listConsumers);
            startActivityForResult(intent, REQ_NAMEJP);
        });
        slvw_goods_00.setSelectedListener(data -> {
            Intent intent = new Intent(mActivity, SelectActivity.class);
            intent.putStringArrayListExtra("DATA",listProducts);
            startActivityForResult(intent, REQ_GOODS);
        });
        slvw_qty_00.setSelectedListener(data -> {
            Intent intent = new Intent(mActivity, SelectActivity.class);
            ArrayList<String> find = new ArrayList<>();
            for (int i=1; i<=2; i++){
                find.add(new SelectHolder(i+"",i+" ( "+SpellingNumber.convert(i,"ID")+" )").toString());
            }
            intent.putStringArrayListExtra("DATA",find);
            startActivityForResult(intent, REQ_QTY);
        });

        findViewById(R.id.bbtn_save_00).setOnClickListener(v -> {
            if (Objects.requireNonNull(getIntent().getAction()).endsWith("EDIT")){
                delete();
            }
            else {
                save();
            }
        });
        findViewById(R.id.bbtn_cancel_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_NAMEJP && resultCode == RESULT_OK){
            assert data != null;
            slvw_jpname_00.setValue(new SelectHolder(data.getStringExtra("DATA")));
        }
        else if (requestCode == REQ_GOODS && resultCode == RESULT_OK){
            assert data != null;
            slvw_goods_00.setValue(new SelectHolder(data.getStringExtra("DATA")));
        }
        else if (requestCode == REQ_QTY && resultCode == RESULT_OK){
            assert data != null;
            slvw_qty_00.setValue(new SelectHolder(data.getStringExtra("DATA")));
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void save(){
        if (slvw_jpname_00.getValue().isEmpty()){
            Utility.showToastError(mActivity,slvw_jpname_00.getHint()+" harus diisi!");
            return;
        }
        if (slvw_goods_00.getValue().isEmpty()){
            Utility.showToastError(mActivity,slvw_goods_00.getHint()+" harus diisi!");
            return;
        }
        if (slvw_qty_00.getValue().isEmpty()){
            Utility.showToastError(mActivity,slvw_qty_00.getHint()+" harus diisi!");
            return;
        }

        PostManager post = new PostManager(mActivity,"process-survey/add-product");
        post.addParam(new ObjectApi("sales_id",getIntent().getStringExtra("ID")));
        post.addParam(new ObjectApi("consumen_id",slvw_jpname_00.getKey()));
        post.addParam(new ObjectApi("consumen_name",slvw_jpname_00.getValue()));
        post.addParam(new ObjectApi("consumen_address",getIntent().getStringExtra("COOR_ADDRESS")));
        post.addParam(new ObjectApi("product_id",slvw_goods_00.getKey()));
        post.addParam(new ObjectApi("type", "0"));
        post.addParam(new ObjectApi("qty", slvw_qty_00.getKey()));
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                Utility.showToastSuccess(mActivity, message);
                setResult(RESULT_OK);
                mActivity.finish();
            }
            else {
                Utility.showToastError(mActivity, message);
            }
        });
    }

    private void delete(){
        PostManager post = new PostManager(mActivity,"process-survey/delete-product/"+tmpID);
        post.execute("DELETE");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                save();
            }
            else {
                Utility.showToastError(mActivity, message);
            }
        });
    }
}
