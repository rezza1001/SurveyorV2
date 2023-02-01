package com.wadaro.surveyor.ui.rekap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyFragment;
import com.wadaro.surveyor.component.SelectHolder;
import com.wadaro.surveyor.component.SelectView;
import com.wadaro.surveyor.module.Utility;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RecapFragment extends MyFragment {
    private static final int REQ_DATERANGE = 1;

    private SelectView slvw_date_00,slvw_send_00;
    private TextView txvw_total_00, txvw_process_00, txvw_pending_00,txvw_totalprod_00;
    private View view_send_00,view_demo_00;
    private ImageView imvw_demo_00,imvw_send_00;
    private LinearLayout lnly_send_00,lnly_demo_00;
    private MaterialRippleLayout mrly_promotor;
    JSONArray promotorRecap = new JSONArray();

    private final DateFormat format1 = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
    private final DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", new Locale("id"));

    private ArrayList<Bundle> products = new ArrayList<>();
    private ProductAdapter mAdapter;

    public static RecapFragment newInstance() {

        Bundle args = new Bundle();

        RecapFragment fragment = new RecapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setlayout() {
        return R.layout.recap_fragment_main;
    }

    @Override
    protected void initLayout(View view) {
        slvw_date_00 = view.findViewById(R.id.slvw_date_00);
        slvw_send_00 = view.findViewById(R.id.slvw_send_00);
        view_send_00 = view.findViewById(R.id.view_send_00);
        view_demo_00 = view.findViewById(R.id.view_demo_00);
        imvw_demo_00 = view.findViewById(R.id.imvw_demo_00);
        imvw_send_00 = view.findViewById(R.id.imvw_send_00);
        lnly_send_00 = view.findViewById(R.id.lnly_send_00);
        lnly_demo_00 = view.findViewById(R.id.lnly_demo_00);
        txvw_totalprod_00 = view.findViewById(R.id.txvw_totalprod_00);
        mrly_promotor = view.findViewById(R.id.mrly_promotor);

        slvw_date_00.setHint("Tanggal Demo");
        slvw_send_00.setHint("Tanggal Kirim");

//        slvw_date_00.hideHint();
//        slvw_send_00.hideHint();
        view_demo_00.setVisibility(View.GONE);
        view_send_00.setVisibility(View.GONE);
        lnly_send_00.setVisibility(View.GONE);
        lnly_demo_00.setVisibility(View.GONE);

        Calendar calendar = Calendar.getInstance();
        String time = format1.format(calendar.getTime());
        String id = format2.format(calendar.getTime());

        slvw_date_00.setValue(new SelectHolder(id+","+id,time+" - "+time));

        slvw_send_00.setValue(new SelectHolder(id,time));
        requestSurvey();

        txvw_total_00 = view.findViewById(R.id.txvw_total_00);
        txvw_process_00 = view.findViewById(R.id.txvw_process_00);
        txvw_pending_00 = view.findViewById(R.id.txvw_pending_00);

        RecyclerView rcvw_data_00 = view.findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new ProductAdapter(products);
        rcvw_data_00.setAdapter(mAdapter);

        mrly_promotor.setBackground(Utility.getRectBackground("00A4F9", Utility.dpToPx(mActivity,4)));

//        selectDemo();
    }

    @Override
    protected void initListener() {
        slvw_date_00.setSelectedListener(data -> {
            Intent intent = new Intent(mActivity, DateRangeActivity.class);
            startActivityForResult(intent,REQ_DATERANGE);
        });

        slvw_send_00.setSelectedListener(data -> openSenDate());
        view_send_00.setOnClickListener(v -> {

        });
        view_demo_00.setOnClickListener(v -> {

        });

        mrly_promotor.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, DetailRecapUnitActivity.class);
            intent.putExtra("data",promotorRecap.toString());
            startActivity(intent);
        });

//        lnly_demo_00.setOnClickListener(v -> selectDemo());
//
//        lnly_send_00.setOnClickListener(v -> selectDelivery());
    }


    private void openSenDate(){
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String time = format1.format(calendar.getTime());
            String id = format2.format(calendar.getTime());
            slvw_send_00.setValue(new SelectHolder(id,time));
            requestSurvey();
        });
        datePickerDialog.setThemeDark(false);
        datePickerDialog.showYearPickerFirst(false);
        datePickerDialog.setTitle("Pilih Tanggal Kirim");
        datePickerDialog.setCancelColor(Color.WHITE);
        datePickerDialog.setOkColor(Color.WHITE);
        datePickerDialog.show(mActivity.getFragmentManager(),"Tanggal");
//        datePickerDialog.setMaxDate(Calendar.getInstance());
    }

    @SuppressLint("SetTextI18n")
    private void requestSurvey(){
        String surveyStart = slvw_date_00.getKey().split(",")[0];
        String surveyEnd = slvw_date_00.getKey().split(",")[1];

        PostManager post  = new PostManager(mActivity,"survey/report/recap?survey_start="+surveyStart+"&survey_end="+surveyEnd+"&delivery_date="+slvw_send_00.getKey());
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    JSONObject rekap = data.getJSONObject("survey_recap");
                    promotorRecap = data.getJSONArray("promotor_recap");
                    int totalSo = rekap.getInt("total_so");
                    int process = rekap.getInt("proses_survey");
                    int pending = rekap.getInt("pending_survey");

                    txvw_total_00.setText(totalSo+"");
                    txvw_process_00.setText(process+"");
                    txvw_pending_00.setText(pending+"");

                    int mTotal = 0;
                    JSONArray product_recap = data.getJSONArray("product_recap");
                    for (int i=0; i<product_recap.length(); i++){
                        Bundle bundle = new Bundle();
                        bundle.putInt("no", (i+1));
                        bundle.putString("product", product_recap.getJSONObject(i).getString("product_name"));
                        bundle.putString("unit", product_recap.getJSONObject(i).getString("product_qty"));
                        bundle.putString("qty", "0");
                        bundle.putString("value", "10000");
                        bundle.putString("promotor", "Promotor Test");
                        products.add(bundle);

                        mTotal = mTotal + product_recap.getJSONObject(i).getInt("product_qty");
                    }
                    mAdapter.notifyDataSetChanged();
                    txvw_totalprod_00.setText(mTotal+"");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_DATERANGE && resultCode == -1){
            if (data != null){
                String start = data.getStringExtra("START");
                String end = data.getStringExtra("END");
                String val = start +" - "+ end;
                try {
                    Date dateStart = format1.parse(Objects.requireNonNull(start));
                    Date dateEnd = format1.parse(Objects.requireNonNull(end));

                    String key = format2.format(Objects.requireNonNull(dateStart))+","+format2.format(Objects.requireNonNull(dateEnd));
                    slvw_date_00.setValue(new SelectHolder(key,val));
                    requestSurvey();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

        }
    }

//    private void selectDemo(){
//        view_send_00.setVisibility(View.VISIBLE);
//        view_demo_00.setVisibility(View.GONE);
//        imvw_demo_00.setImageResource(R.drawable.ic_radio_selected);
//        imvw_send_00.setImageResource(R.drawable.ic_radio_unchecked);
//    }
//
//    private void selectDelivery(){
//        view_send_00.setVisibility(View.GONE);
//        view_demo_00.setVisibility(View.VISIBLE);
//        imvw_demo_00.setImageResource(R.drawable.ic_radio_unchecked);
//        imvw_send_00.setImageResource(R.drawable.ic_radio_selected);
//    }
}
