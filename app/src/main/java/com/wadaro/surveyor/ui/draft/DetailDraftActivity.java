package com.wadaro.surveyor.ui.draft;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wadaro.surveyor.R;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.database.table.SumarySurveyDB;
import com.wadaro.surveyor.database.table.SurveyDeatailDB;
import com.wadaro.surveyor.database.table.TempDB;
import com.wadaro.surveyor.module.FailedWindow;
import com.wadaro.surveyor.module.Utility;
import com.wadaro.surveyor.ui.assignment.DenahActivity;
import com.wadaro.surveyor.ui.assignment.KeyValView;
import com.wadaro.surveyor.ui.assignment.adapter.OrderHolder;
import com.wadaro.surveyor.ui.survey.KeyValueView;
import com.wadaro.surveyor.ui.survey.SurveyOrderActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class DetailDraftActivity extends MyActivity {

    private LinearLayout lnly_sumary;
    private String soId = "";
    private ArrayList<Bundle> allData = new ArrayList<>();
    private DetailAdapter mAdapter;

    @Override
    protected int setLayout() {
        return R.layout.draft_activity_detail;
    }

    @Override
    protected void initLayout() {
        lnly_sumary = findViewById(R.id.lnly_sumary);

        RecyclerView rcvw_data = findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));
        rcvw_data.setNestedScrollingEnabled(false);
        mAdapter = new DetailAdapter(mActivity, allData);
        rcvw_data.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        soId = getIntent().getStringExtra("so");
        buildSummary();

    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
    }

    private void buildSummary(){
        lnly_sumary.removeAllViews();
        SumarySurveyDB db = new SumarySurveyDB();
        db.getData(mActivity,soId);
        try {
            JSONObject jo = new JSONObject(db.data);
            createKeyVal("Sales Id",jo.getString("sales_id"));
            createKeyVal("Tanggal Kirim",jo.getString("delivery_date"));
            createKeyVal("Catatan",jo.getString("sales_note"));

            buildDetail(jo.getString("sales_id"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        TempDB tempDB = new TempDB();
        ArrayList<TempDB> dbs = tempDB.getAllData(mActivity, soId,TempDB.DETAIL_SURVEY);
        if (dbs.size() == 0){
            FailedWindow failedWindow = new FailedWindow(mActivity);
            failedWindow.setDescription("Anda belum download data hari ini");
            failedWindow.show();
            return;
        }
        try {
            JSONObject data = new JSONObject(dbs.get(0).data);
            buildData(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void buildData(JSONObject obj){
        try {
            Log.d(TAG,"obj "+obj);
            JSONObject data = obj.getJSONObject("data");
            JSONArray details = data.getJSONArray("details");

            DateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            DateFormat f2 = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
            DateFormat f3 = new SimpleDateFormat("HH:mm", new Locale("id"));

            createKeyVal("Nama Surveyor",mUser.name);
            createKeyVal("No.SO",data.getString("sales_id"));
            if (!data.isNull("delivery_date")){
                createKeyVal("Tanggal Kirim", f2.format(Objects.requireNonNull(f1.parse(data.getString("delivery_date") + " 00:00:00"))));
            }
            createKeyVal("Nama Sales Promotor",data.getString("promotor_name"));
            createKeyVal("Keterangan",data.getString("sales_note"));
            addLine();
            createKeyVal("Tanggal Demo",f2.format(Objects.requireNonNull(f1.parse(data.getString("booking_date")))));
            createKeyVal("Jadwal Demo","Demo "+ data.getString("booking_demo"));
            createKeyVal("Jam Demo",f3.format(Objects.requireNonNull(f1.parse(data.getString("booking_date")))));
            addLine();
            createKeyVal("Koordinator",data.getString("coordinator_name"));
            createKeyVal("Alamat",data.getString("coordinator_address"));
            createKeyVal("Catatan Lokasi",data.getString("coordinator_note"));
            createKeyVal("Koordinat",data.getString("coordinator_location"));
            createKeyVal("No.KTP",data.getString("coordinator_ktp"));
            createKeyVal("No.HP",data.getString("coordinator_phone"));


        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void buildDetail(String salesID){
        allData.clear();

        SurveyDeatailDB db = new SurveyDeatailDB();
        ArrayList<SurveyDeatailDB> detailDB = db.getAllBySalesId(mActivity,salesID);
        for (SurveyDeatailDB dtl: detailDB){
            try {
                JSONObject obj = new JSONObject(dtl.data);
                Bundle bundle = new Bundle();
                bundle.putString("consumen_name", obj.getString("consumen_name"));
                bundle.putString("consumen_phone", obj.getString("consumen_phone"));
                bundle.putString("consumen_id", obj.getString("consumen_id"));
                bundle.putString("sales_id", obj.getString("sales_id"));
                bundle.putString("selling_confirmation", obj.getString("selling_confirmation"));
                bundle.putString("spending", obj.getString("spending"));
                bundle.putString("survey_note", obj.getString("survey_note"));
                bundle.putString("survey_result", obj.getString("survey_result"));
                bundle.putString("survey_scoring", obj.getString("survey_scoring"));
                allData.add(bundle);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mAdapter.notifyDataSetChanged();
        mAdapter.setOnSelectedListener(data -> {
            Intent intent = new Intent(mActivity, SurveyOrderActivity.class);
            intent.putExtra("SURVEY_ID", "");
            intent.putExtra("ID", salesID);
            intent.putExtra("CONSUMER", data.getString("consumen_id"));
            startActivity(intent);
        });
    }

    private void createKeyVal(String key, String value){
        KeyValueView kv = new KeyValueView(mActivity, null);
        kv.create(key, value);
        lnly_sumary.addView(kv);
    }


    private void addLine(){
        View view = new View(mActivity);
        view.setBackgroundColor(Color.parseColor("#33000000"));
        LinearLayout.LayoutParams lp_view = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utility.dpToPx(mActivity,1));
        lp_view.topMargin = Utility.dpToPx(mActivity,10);
        lp_view.bottomMargin = Utility.dpToPx(mActivity,10);
        lnly_sumary.addView(view,lp_view);
    }
}
