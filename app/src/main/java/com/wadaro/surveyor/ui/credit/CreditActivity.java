package com.wadaro.surveyor.ui.credit;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.component.SelectActivity;
import com.wadaro.surveyor.component.SelectHolder;
import com.wadaro.surveyor.component.SelectView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreditActivity extends MyActivity {

    private final String[] months = {"Januari","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};

    private final ArrayList<Bundle> listData = new ArrayList<>();
    private CreditAdapter mAdapter;
    private SelectView slvw_date_00;
    private final DateFormat format1 = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
    private final DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", new Locale("id"));

    @Override
    protected int setLayout() {
        return R.layout.credit_activity_main;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Over Kredit");
        LinearLayout lnly_header_00 = findViewById(R.id.lnly_header_00);
        lnly_header_00.setBackgroundColor(Color.WHITE);

        RecyclerView rcvw_data_00 = findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new CreditAdapter(listData);
        rcvw_data_00.setAdapter(mAdapter);

        slvw_date_00 = findViewById(R.id.slvw_date_00);
        slvw_date_00.setHint("Pilih Bulan");
    }

    @Override
    protected void initData() {
        Calendar calendar = Calendar.getInstance();
        int current = calendar.get(Calendar.MONTH);
        slvw_date_00.setValue(new SelectHolder((current+1)+"",months[current]));
        request();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        mAdapter.setOnSelectedListener(data -> {
            Intent intent = new Intent(mActivity, CreditDetail.class);
            intent.putExtras(data);
            startActivity(intent);
        });

        slvw_date_00.setSelectedListener(data -> chooseMonth());
    }
    private void request(){
        listData.clear();
        int month = Integer.parseInt(slvw_date_00.getKey()) + 1;
        PostManager post = new PostManager(mActivity,"over-credit?month="+month);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONArray data = obj.getJSONArray("data");
                    for (int i=0; i<data.length(); i++){
                        JSONObject jo = data.getJSONObject(i);
                        Bundle bundle = new Bundle();
                        bundle.putString("billing_id",jo.getString("billing_id"));
                        bundle.putString("consumen_id",jo.getString("consumen_id"));
                        bundle.putString("product_id",jo.getString("product_id"));
                        bundle.putString("nottb",jo.getString("sales_receive_id"));
                        bundle.putString("coordinator",jo.getString("coordinator_name"));
                        bundle.putInt("installment",jo.getInt("installment_period"));
                        bundle.putLong("total",jo.getLong("installment"));
                        bundle.putString("payment_date",jo.getString("payment_date").split(" ")[0]);
                        listData.add(bundle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    private void chooseMonth(){
        ArrayList<String> holders = new ArrayList<>();
        for (int i=0; i<12; i++){
            SelectHolder holder = new SelectHolder();
            holder.id = (i)+"";
            holder.value = months[i];
            holders.add(holder.toString());
        }
        Intent intent = new Intent(mActivity, SelectActivity.class);
        intent.putStringArrayListExtra("DATA",holders);
        startActivityForResult(intent, 1);
    }

    private void openCalendar(){
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String key = format2.format(calendar.getTime());
            String value = format1.format(calendar.getTime());
            slvw_date_00.setValue(new SelectHolder(key, value));
            request();
        });
        datePickerDialog.setThemeDark(false);
        datePickerDialog.showYearPickerFirst(false);
        datePickerDialog.setTitle("Pilih Tanggal");
        datePickerDialog.setCancelColor(Color.WHITE);
        datePickerDialog.setOkColor(Color.WHITE);
        datePickerDialog.show(mActivity.getFragmentManager(),"Tanggal");
        datePickerDialog.setMaxDate(Calendar.getInstance());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1){
            String mData = data.getStringExtra("DATA");
            SelectHolder holder = new SelectHolder(mData);
            int id = Integer.parseInt(holder.id);
            slvw_date_00.setValue(new SelectHolder(id+"",months[id]));
            request();
        }
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
            request();
        }
    };

}
