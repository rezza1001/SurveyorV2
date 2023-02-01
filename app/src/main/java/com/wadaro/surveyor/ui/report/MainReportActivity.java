package com.wadaro.surveyor.ui.report;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.wadaro.surveyor.R;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.ui.assignment.adapter.AssignmentPagerAdapter;
import com.wadaro.surveyor.ui.assignment.fragment.MapFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainReportActivity extends MyActivity {

    private Spinner spnr_demo_00;
    private RelativeLayout lnly_date_00;
    private TextView txvw_date_00,txvw_branch_00;

    private HashMap<String,String> MAPS_DEMO = new HashMap<>();

    @Override
    protected int setLayout() {
        return  R.layout.activity_laporan_hasil_booking;
    }

    @Override
    protected void initLayout() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.pager);

        TabelFragment tabelFragment = new TabelFragment();
        MapFragment mapFragment = new MapFragment();

        AssignmentPagerAdapter adapter = new AssignmentPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(tabelFragment, getString(R.string.tabel_fragment));
        adapter.addFragment(mapFragment, getString(R.string.map_fragment));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        ArrayList<String> tempDemo = new ArrayList<>();
        tempDemo.add("Semua Demo");
        tempDemo.add("Demo 1");
        tempDemo.add("Demo 2");
        tempDemo.add("Demo 3");
        tempDemo.add("Demo 4");

        MAPS_DEMO.put("Semua Demo","Semua");
        MAPS_DEMO.put("Demo 1","1");
        MAPS_DEMO.put("Demo 2","2");
        MAPS_DEMO.put("Demo 3","3");
        MAPS_DEMO.put("Demo 4","4");

        spnr_demo_00 = findViewById(R.id.spDemo);
        lnly_date_00 = findViewById(R.id.lnly_date_00);
        txvw_date_00 = findViewById(R.id.txvw_date_00);
        txvw_branch_00 = findViewById(R.id.txvw_branch_00);

        ArrayAdapter<String> adapterSpinnerDemo = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tempDemo);

        adapterSpinnerDemo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnr_demo_00.setAdapter(adapterSpinnerDemo);
    }

    @Override
    protected void initData() {
        txvw_branch_00.setText(mUser.branch_name);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initListener() {
        Calendar calendar = Calendar.getInstance();
        txvw_date_00.setText(calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DATE));
        lnly_date_00.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, (view, year, monthOfYear, dayOfMonth) -> {
                txvw_date_00.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                sendBroadCast();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            datePickerDialog.show();
        });

        spnr_demo_00.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sendBroadCast();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
    }

    private void sendBroadCast(){
        Intent intent = new Intent("PARAMETER");
        intent.putExtra("DATE",txvw_date_00.getText().toString());
        intent.putExtra("DEMO",MAPS_DEMO.get(spnr_demo_00.getSelectedItem()));
        mActivity.sendBroadcast(intent);
    }
}
