package com.wadaro.surveyor.ui.draft;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.FormPost;
import com.wadaro.surveyor.api.LoadingWindow;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.database.table.SumarySurveyDB;
import com.wadaro.surveyor.database.table.SurveyDeatailDB;
import com.wadaro.surveyor.module.SuccessWindow;
import com.wadaro.surveyor.module.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DraftSurveyActivity extends MyActivity {

    private DraftAdapter mAdapter;
    private ArrayList<Bundle> allData = new ArrayList<>();
    private ArrayList<SurveyDeatailDB> detailSurvey = new ArrayList<>();
    private ArrayList<SumarySurveyDB> sumarySurvey = new ArrayList<>();
    private LoadingWindow loadingWindow;
    @Override
    protected int setLayout() {
        return R.layout.draft_activity_activitydraft;
    }

    @Override
    protected void initLayout() {

        RecyclerView rcvw_data_00 = findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));

        mAdapter = new DraftAdapter(mActivity, allData);
        rcvw_data_00.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        allData.clear();
        detailSurvey.clear();
        SumarySurveyDB db = new SumarySurveyDB();
        for (SumarySurveyDB mDb : db.getAllData(mActivity)){
            sumarySurvey.add(mDb);
            try {
                JSONObject data = new JSONObject(mDb.data);
                Bundle bundle = new Bundle();
                bundle.putString("SO",data.getString("sales_id"));
                bundle.putString("delivery_date",data.getString("delivery_date"));
                bundle.putString("delivery_note",data.getString("sales_note"));
                allData.add(bundle);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        SurveyDeatailDB surveyDeatailDB = new SurveyDeatailDB();
        detailSurvey = surveyDeatailDB.getData(mActivity);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initListener() {
        mAdapter.setOnSelectedListener(data -> {
            Intent intent = new Intent(mActivity, DetailDraftActivity.class);
            intent.putExtra("so",data.getString("SO"));
            startActivity(intent);
        });

        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.mrly_action_00).setOnClickListener(v -> {
            loadingWindow = new LoadingWindow(mActivity);
            loadingWindow.show();
            saveDetail();
        });
    }

    private void saveDetail(){
        if (detailSurvey.size() > 0 ){
            SurveyDeatailDB mData = detailSurvey.get(0);
            saveDtlAPI(mData);
        }
        else {
            saveSummary();
        }

    }

    private void saveDtlAPI(SurveyDeatailDB mData){
        FormPost post = new FormPost(mActivity,"process-survey/detail/save-survey");
        try {
            post.setData(new JSONObject(mData.data));
            post.setImages(new JSONObject(mData.images));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        post.showLoading(false);
        post.execute();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                detailSurvey.remove(0);
                saveDetail();
            }
            else {
                loadingWindow.dismiss();
                Utility.showToastError(mActivity, message);
            }
        });
    }

    private void saveSummary(){
        if (sumarySurvey.size() > 0 ){
            SumarySurveyDB mData = sumarySurvey.get(0);
            saveSummaryAPI(mData);
        }
        else {
            loadingWindow.dismiss();
            SuccessWindow successWindow = new SuccessWindow(mActivity);
            successWindow.setDescription("Data berhasil disimpan");
            successWindow.show();
            successWindow.setOnFinishListener(() -> {
                SurveyDeatailDB db = new SurveyDeatailDB();
                db.clearData(mActivity);
                SumarySurveyDB db1 = new SumarySurveyDB();
                db1.clearData(mActivity);

                mActivity.finish();
            });
        }
    }

    private void saveSummaryAPI( SumarySurveyDB surveyDB){
        PostManager post = new PostManager(mActivity,"process-survey/save");
        try {
            post.setData(new JSONObject(surveyDB.data));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        post.showloading(false);
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                sumarySurvey.remove(0);
                allData.remove(0);
                mAdapter.notifyDataSetChanged();
                saveSummary();
            }
            else {
                loadingWindow.dismiss();
                Utility.showToastError(mActivity, message);
            }
        });
    }


}
