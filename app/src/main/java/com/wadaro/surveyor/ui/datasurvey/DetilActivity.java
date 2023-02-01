package com.wadaro.surveyor.ui.datasurvey;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.module.Utility;
import com.wadaro.surveyor.ui.assignment.KeyValView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class DetilActivity extends MyActivity {
    private static final int REQ_EDIt = 11;

    private LinearLayout lnly_body_00;
    private ArrayList<OrderHolder> orders = new ArrayList<>();
    private OrderAdapter mAdapter ;
    private TextView txvw_size_00;
    private Button bbtn_process_00;

    @Override
    protected int setLayout() {
        return R.layout.datasurvey_activity_detail;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        lnly_body_00 = findViewById(R.id.lnly_body_00);
        txvw_size_00 = findViewById(R.id.txvw_size_00);
        bbtn_process_00 = findViewById(R.id.bbtn_process_00);
        RecyclerView rcvw_data_00 = findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));
        rcvw_data_00.setNestedScrollingEnabled(false);
        mAdapter = new OrderAdapter(mActivity, orders);
        rcvw_data_00.setAdapter(mAdapter);

        TextView txvw_title_00 = findViewById(R.id.txvw_titile_00);
        txvw_title_00.setText("Detail Survey");

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        refresh(getIntent().getStringExtra("ID"));
    }

    @SuppressLint("SetTextI18n")
    private void refresh(String id){
        lnly_body_00.removeAllViews();
        PostManager post = new PostManager(mActivity,"survey/"+ id+"/result");
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    JSONArray details = data.getJSONArray("details");

                    DateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    DateFormat f2 = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
                    DateFormat f3 = new SimpleDateFormat("HH:mm", new Locale("id"));

                    create("Nama Surveyor",mUser.name);
                    create("No.SO",data.getString("sales_id"));
                    create("Tanggal Kirim", f2.format(Objects.requireNonNull(f1.parse(data.getString("delivery_date")+" 00:00:00"))));
                    create("Nama Sales Promotor",data.getString("promotor_name"));
                    create("Keterangan",data.getString("sales_note"));
                    create("Kode Sales",data.getString("code_sales"));
                    create("Koordinator",data.getString("coordinator_name"));
                    addLine();
                    create("Hasil Survey",data.getString("survey_result"));
                    create("Nilai Survey",data.getString("survey_scoring"));
                    create("Catatan Survey",data.getString("survey_note"));


                    orders.clear();
                    for (int i=0; i<details.length(); i++){
                        JSONObject datil = details.getJSONObject(i);
                        OrderHolder holder = new OrderHolder();
                        holder.name = datil.getString("consumen_name");
                        holder.goods = datil.getString("product_name");
                        holder.price = datil.getString("selling_price");
                        holder.qty = datil.getString("qty");
                        orders.add(holder);
                    }
                    mAdapter.notifyDataSetChanged();
                    txvw_size_00.setText("Total data "+ orders.size()+"");
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_EDIt && resultCode == RESULT_OK){
            assert data != null;
            new Handler().postDelayed(() -> {
                Intent intent = new Intent("REFRESH");
                sendBroadcast(intent);
                mActivity.finish();
            },200);

        }
        else if (requestCode == REQ_EDIt && resultCode == RESULT_CANCELED) {
            mActivity.finish();
        }
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
    }

    private void create(String key, String value){
        KeyValView kv_namedb = new KeyValView(mActivity,null);
        kv_namedb.setKey(key);
        kv_namedb.setValue(value);
        lnly_body_00.addView(kv_namedb);
    }

    private void addLine(){
        View view = new View(mActivity);
        view.setBackgroundColor(Color.parseColor("#33000000"));
        LinearLayout.LayoutParams lp_view = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utility.dpToPx(mActivity,1));
        lp_view.topMargin = Utility.dpToPx(mActivity,10);
        lp_view.bottomMargin = Utility.dpToPx(mActivity,10);
        lnly_body_00.addView(view,lp_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter("FINISH_SURVEY"));
        registerReceiver(receiver, new IntentFilter("FINISH_DTL_SURVEY"));
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
            if (intent.getAction().equals("FINISH_SURVEY") || intent.getAction().equals("FINISH_DTL_SURVEY")){
                mActivity.finish();
            }
        }
    };


}
