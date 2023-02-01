package com.wadaro.surveyor.ui.datasurvey;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyFragment;
import com.wadaro.surveyor.component.MyDateSpinner;
import com.wadaro.surveyor.component.SelectHolder;
import com.wadaro.surveyor.component.SelectView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataSurveyFragment extends MyFragment {

    private SelectView slvw_date_00,slvw_date_11;
    private TextView txvw_size_00;

    private DateFormat format1 = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
    private DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", new Locale("id"));

    private ArrayList<SurveyHolder> mListSurvey = new ArrayList<>();
    private SurveyAdapter mAdapter ;

    public static DataSurveyFragment newInstance() {
        Bundle args = new Bundle();
        DataSurveyFragment fragment = new DataSurveyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setlayout() {
        return R.layout.datasurvey_fragment_main;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout(View view) {
        txvw_size_00 = view.findViewById(R.id.txvw_size_00);
        slvw_date_00 = view.findViewById(R.id.slvw_date_00);
        slvw_date_11 = view.findViewById(R.id.slvw_date_11);
        slvw_date_00.setHint("Tanggal Demo");
        slvw_date_11.setHint("Tanggal Kirim");

        RecyclerView rcvw_demo_00 = view.findViewById(R.id.rcvw_demo_00);
        rcvw_demo_00.setLayoutManager(new LinearLayoutManager(mActivity));
        rcvw_demo_00.setNestedScrollingEnabled(false);

        mAdapter = new SurveyAdapter(mActivity, mListSurvey);
        rcvw_demo_00.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        slvw_date_00.setSelectedListener(data -> {
            MyDateSpinner myDateSpinner = new MyDateSpinner(mActivity);
            myDateSpinner.show();
            myDateSpinner.setOnForceDismissListener((year, month, date) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month-1);
                calendar.set(Calendar.DATE,date);
                slvw_date_00.setValue(new SelectHolder(format2.format(calendar.getTime()),format1.format(calendar.getTime())));
                slvw_date_11.setValue(new SelectHolder("",""));
                requestSurvey("booking_date");
            });
        });
        slvw_date_11.setSelectedListener(data -> {
            MyDateSpinner myDateSpinner = new MyDateSpinner(mActivity);
            myDateSpinner.show();
            myDateSpinner.setOnForceDismissListener((year, month, date) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month-1);
                calendar.set(Calendar.DATE,date);
                slvw_date_11.setValue(new SelectHolder(format2.format(calendar.getTime()),format1.format(calendar.getTime())));
                slvw_date_00.setValue(new SelectHolder("",""));
                requestSurvey("delivery_date");
            });
        });

        mAdapter.setOnSelectedListener(data -> {
            Intent intent = new Intent(mActivity, DetilActivity.class);
            intent.putExtra("ID", data.surveyID);
            startActivity(intent);
        });
    }

    @Override
    protected void initData() {
        slvw_date_00.setValue(new SelectHolder(format2.format(new Date()), format1.format(new Date())));
        requestSurvey("booking_date");
    }

    @SuppressLint("SetTextI18n")
    private void requestSurvey(String searchBy){
        String date = slvw_date_00.getKey();
        if (!searchBy.equals("booking_date")){
            date = slvw_date_11.getKey();
        }
        mListSurvey.clear();
        PostManager post  = new PostManager(mActivity,"survey/report?"+searchBy+"="+date);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONArray survey = obj.getJSONArray("data");

                    for (int i=0; i<survey.length(); i++){
                        JSONObject surveyObj = survey.getJSONObject(i);
                        SurveyHolder holder = new SurveyHolder();
                        holder.coordinator  = surveyObj.getString("coordinator_name");
                        holder.so           = surveyObj.getString("sales_id");
                        holder.demoDate     = surveyObj.getString("booking_date").split(" ")[0];
                        holder.deliveryDate = surveyObj.getString("survey_date").split(" ")[0];
                        holder.jp           = surveyObj.getString("consumen_name");
                        holder.status       = surveyObj.getString("survey_result");
                        holder.surveyID       = surveyObj.getString("survey_id");
                        mListSurvey.add(holder);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mAdapter.notifyDataSetChanged();
            txvw_size_00.setText("Total ada "+mListSurvey.size()+" titik survey");
        });
    }
}
