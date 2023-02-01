package com.wadaro.surveyor.ui.rekap;

import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;

import com.balysv.materialripple.MaterialRippleLayout;
import com.savvi.rangedatepicker.CalendarPickerView;
import com.wadaro.surveyor.R;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.module.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateRangeActivity extends MyActivity {

    private MaterialRippleLayout mrly_action_00;
    private CalendarPickerView clvw_calendar_00;

    @Override
    protected int setLayout() {
        return (R.layout.report_mutation_activity_daterange);
    }

    @Override
    protected void initLayout() {
        mrly_action_00          = findViewById(R.id.mrly_action_00);

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1, getResources().getDisplayMetrics());
        int round = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 4, getResources().getDisplayMetrics());

        mrly_action_00.setBackground(Utility.getLineBackground(px,"097bff",round,round,round,round));
        clvw_calendar_00    = findViewById(R.id.clvw_calendar_00);


    }

    @Override
    protected void initData() {
        ArrayList<Integer> list = new ArrayList<>();
        final Calendar max = Calendar.getInstance();
        final Calendar min = Calendar.getInstance();
        int type = getIntent().getIntExtra("TYPE", 0);
        Log.d(TAG, "TYPE "+ type);
        if (type == 1){
            Log.d(TAG,"TO NEXT DATE");
            max.add(Calendar.YEAR,1);
        }
        else {
            Log.d(TAG,"TO BACK DATE");
            max.add(Calendar.DATE,1);
            min.add(Calendar.YEAR, -10);
        }
        clvw_calendar_00.init(min.getTime(), max.getTime(), new SimpleDateFormat("MMMM, yyyy",new Locale("id"))) //
                .inMode(CalendarPickerView.SelectionMode.RANGE) //
                .withSelectedDate(Utility.getCurTimeServer().getTime())
                .withDeactivateDates(list)
                .withHighlightedDates(new ArrayList<>());

    }

    @Override
    protected void initListener() {
        findViewById(R.id.mrly_back_00).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            mActivity.finish();
        });

        mrly_action_00.setOnClickListener(v -> {
            DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy",new Locale("id"));
            Intent intent = new Intent();
            JSONArray dates = new JSONArray();
            for (Date date : clvw_calendar_00.getSelectedDates()){
                dates.put(dateFormat.format(date));
            }
            JSONObject djo = new JSONObject();
            try {
                djo.put("DATES",dates);
                intent.putExtra("START",dates.getString(0));
                intent.putExtra("END",dates.getString(dates.length()-1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.putExtra("DATES",djo.toString());
            setResult(RESULT_OK, intent);
            mActivity.finish();
        });
    }
}
