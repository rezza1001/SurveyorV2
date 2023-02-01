package com.wadaro.surveyor.ui.survey;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class SummaryActivity extends MyActivity {

    private LinearLayout lnly_body_00;
    private ArrayList<GoodsHolder> mListGoods = new ArrayList<>();
    private GoodsDtlAdapter mAdapter;

    @Override
    protected int setLayout() {
        return R.layout.survey_activity_summary;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_title_00 = findViewById(R.id.txvw_titile_00);
        txvw_title_00.setText("Data Hasil Survey");

        lnly_body_00 = findViewById(R.id.lnly_body_00);
        mAdapter = new GoodsDtlAdapter(mActivity, mListGoods);

        RecyclerView rcvw_goods_00 = findViewById(R.id.rcvw_goods_00);
        rcvw_goods_00.setLayoutManager(new LinearLayoutManager(mActivity));
        rcvw_goods_00.setAdapter(mAdapter);

    }

    @Override
    protected void initData() {
        lnly_body_00.removeAllViews();
        request();

    }

    @Override
    protected void initListener() {
        findViewById(R.id.bbtn_finish_00).setOnClickListener(v -> {
            sendBroadcast(new Intent("FINISH_DTL_SURVEY"));
            mActivity.finish();
        });

        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.bbtn_edit_00).setOnClickListener(v -> onBackPressed());
    }

    private void request(){
        mListGoods.clear();

        PostManager post = new PostManager(mActivity,"survey/"+getIntent().getStringExtra("SURVEY_ID")+"/result");
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    DateFormat format2 = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
                    JSONObject data = obj.getJSONObject("data");
                    addInfo("No.SO",data.getString("sales_id"));
                    addInfo("Tanggal Kiriman",format2.format(Objects.requireNonNull(format1.parse(data.getString("delivery_date")))));
                    addInfo("Kode Sales",data.getString("code_sales"));
                    addInfo("Nama Sales Promotor",data.getString("promotor_name"));
                    addInfo("Keterangan",data.getString("sales_note"));
                    addLine();
                    addInfo("Nama Pemesan",data.getString("consumen_name"));
                    addInfo("Alamat",data.getString("consumen_address"));
                    addInfo("No. KTP",data.getString("consumen_nik"));
                    addInfo("No. HP",data.getString("consumen_phone"));
                    addLine();
                    addInfo("Penilaian",data.getString("survey_result"));
                    addLine();

                    JSONArray details = data.getJSONArray("details");

                    for (int i=0; i<details.length(); i++ ){
                        JSONObject detail = details.getJSONObject(i);
                        GoodsHolder holder = new GoodsHolder();
                        holder.name = detail.getString("product_name");
                        holder.qty = detail.getInt("qty");
                        holder.price = detail.getLong("selling_price");
                        mListGoods.add(holder);
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addInfo(String key, String value){
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
}
