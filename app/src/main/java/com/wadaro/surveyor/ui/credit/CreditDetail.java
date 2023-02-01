package com.wadaro.surveyor.ui.credit;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.ObjectApi;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.module.Utility;
import com.wadaro.surveyor.ui.assignment.KeyValView;
import com.wadaro.surveyor.util.MyCurrency;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreditDetail extends MyActivity {

    private LinearLayout lnly_body_00;
    private DateFormat format1 = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
    private DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", new Locale("id"));
    private Bundle dataPass = new Bundle();

    @Override
    protected int setLayout() {
        return R.layout.credit_activity_detail;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Over Kredit");
        LinearLayout lnly_header_00 = findViewById(R.id.lnly_header_00);
        lnly_header_00.setBackgroundColor(Color.WHITE);
        lnly_body_00 = findViewById(R.id.lnly_body_00);
    }

    @Override
    protected void initData() {
        lnly_body_00.removeAllViews();
        Bundle bundle = getIntent().getExtras();
        if (bundle == null){
            Utility.showToastError(mActivity,"Failed");
            mActivity.finish();
            return;
        }
        PostManager post = new PostManager(mActivity,"over-credit/detail");
        post.addParam(new ObjectApi("billing_id",bundle.getString("billing_id")));
        post.addParam(new ObjectApi("consumen_id",bundle.getString("consumen_id")));
        post.addParam(new ObjectApi("product_id",bundle.getString("product_id")));
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){

                try {
                    dataPass.putString("data", obj.toString());
                    JSONObject data = obj.getJSONObject("data");
                    Log.d(TAG,"data "+data);
                    JSONObject details = data.getJSONObject("details");
                    create("No. TTB",data.getString("sales_receive_id"));
                    create("No. Kwitansi",data.getString("billing_id"));
                    create("Koordinator",data.getString("coordinator_name"));
                    create("Alamat",data.getString("coordinator_address"));
                    create("No. KTP",data.getString("coordinator_ktp"));
                    create("No. HP",data.getString("coordinator_phone"));
                    Date deliveryDate = format2.parse(details.getString("delivery_date"));
                    Date dueDate = format2.parse(data.getString("due_date"));
                    create("Tanggal Kirim",format1.format(deliveryDate));
                    create("Tanggal Jatuh Tempo",format1.format(dueDate));
                    create("Nama Konsumen",details.getString("consumen_name"));
                    create("Nama Barang",details.getString("product_name"));
                    create("Angsuran", MyCurrency.toCurrnecy("Rp",details.getLong("installment")));
                    create("Angsuran Ke",details.getString("installment_period"));
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
            else {
                Utility.showToastError(mActivity,message);
                mActivity.finish();
            }
        });


    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.bbtn_cancel_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.bbtn_process_00).setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, ProcessDetailActivity.class);
            intent.putExtras(dataPass);
            startActivity(intent);
        });
    }

    private void create(String key, String value){
        KeyValView kv_namedb = new KeyValView(mActivity,null);
        kv_namedb.setKey(key);
        kv_namedb.setValue(value);
        lnly_body_00.addView(kv_namedb);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver,new IntentFilter("FINISH"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mActivity.finish();
        }
    };
}
