package com.wadaro.surveyor.ui.assignment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.wadaro.surveyor.R;
import com.wadaro.surveyor.base.Global;
import com.wadaro.surveyor.base.MyFragment;
import com.wadaro.surveyor.component.SelectActivity;
import com.wadaro.surveyor.component.SelectHolder;
import com.wadaro.surveyor.component.SelectView;
import com.wadaro.surveyor.database.MyPreference;
import com.wadaro.surveyor.module.Utility;
import com.wadaro.surveyor.ui.assignment.adapter.AssignmentPagerAdapter;
import com.wadaro.surveyor.ui.assignment.fragment.MapFragment;
import com.wadaro.surveyor.ui.assignment.fragment.TabelFragment;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AssignmentFrg extends MyFragment {

    private EditText edtx_search_00;
    private SelectView slvw_date_00,slvw_month_00;
    private final String[] months = {"Januari","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};

    DateFormat format1 = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
    DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", new Locale("id"));
    public static AssignmentFrg newInstance() {

        Bundle args = new Bundle();

        AssignmentFrg fragment = new AssignmentFrg();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setlayout() {
        return R.layout.ui_databooking_frg_main;
    }

    @Override
    protected void initLayout(View view) {

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.pager);
        slvw_date_00        = view.findViewById(R.id.slvw_date_00);
        slvw_date_00.setHint("Tanggal Denah Lokasi");

        edtx_search_00       = view.findViewById(R.id.edtx_search_00);
        slvw_month_00       = view.findViewById(R.id.slvw_month_00);
        slvw_month_00.setHint("Periode Penugasan");

        TabelFragment tabelFragment = new TabelFragment();
        MapFragment mapFragment = new MapFragment();

        AssignmentPagerAdapter adapter = new AssignmentPagerAdapter(getChildFragmentManager());
        adapter.addFragment(tabelFragment, getString(R.string.tabel_fragment));
        adapter.addFragment(mapFragment, getString(R.string.map_fragment));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        Calendar calendar = Calendar.getInstance();
        int current = calendar.get(Calendar.MONTH);
        slvw_month_00.setValue(new SelectHolder((current+1)+"",months[current]));

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initListener() {
        slvw_date_00.setValue(new SelectHolder(format2.format(new Date()), format1.format(new Date())));
        slvw_date_00.setSelectedListener(data -> openCalendar());

        slvw_month_00.setSelectedListener(data -> chooseMonth());
        edtx_search_00.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Intent intent = new Intent("SEARCH");
                intent.putExtra("text", s.toString());
                mActivity.sendBroadcast(intent);
            }
        });
    }

    private void openCalendar(){
        if (MyPreference.getInt(mActivity, Global.PREF_OFFLINE_MODE) == 1){
            Utility.showToastError(mActivity,"Anda dalam mode offline");
            return;
        }
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String key = format2.format(calendar.getTime());
            String value = format1.format(calendar.getTime());
            slvw_date_00.setValue(new SelectHolder(key, value));
            sendBroadCast();
        });
        datePickerDialog.setThemeDark(false);
        datePickerDialog.showYearPickerFirst(false);
        datePickerDialog.setTitle("Pilih Tanggal");
        datePickerDialog.setCancelColor(Color.WHITE);
        datePickerDialog.setOkColor(Color.WHITE);
        datePickerDialog.show(mActivity.getFragmentManager(),"Tanggal");
//        datePickerDialog.setMaxDate(Calendar.getInstance());
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

    private void sendBroadCast(){
        int month = Integer.parseInt(slvw_month_00.getKey());

        Intent intent = new Intent("PARAMETER");
        intent.putExtra("DATE",slvw_date_00.getKey());
        intent.putExtra("MONTH",month);
        mActivity.sendBroadcast(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1){
            assert data != null;
            String mData = data.getStringExtra("DATA");
            SelectHolder holder = new SelectHolder(mData);
            int id = Integer.parseInt(holder.id);
            slvw_month_00.setValue(new SelectHolder((id+1)+"",months[id]));
            sendBroadCast();
        }
    }
}
