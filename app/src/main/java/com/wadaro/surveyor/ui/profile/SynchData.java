package com.wadaro.surveyor.ui.profile;

import android.content.Context;
import android.os.Handler;

import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.Global;
import com.wadaro.surveyor.component.WarningWindow;
import com.wadaro.surveyor.database.MyPreference;
import com.wadaro.surveyor.database.table.ProcessSurveyDB;
import com.wadaro.surveyor.database.table.SumarySurveyDB;
import com.wadaro.surveyor.database.table.SurveyDeatailDB;
import com.wadaro.surveyor.database.table.TempDB;
import com.wadaro.surveyor.module.FileProcessing;
import com.wadaro.surveyor.module.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SynchData {

    private final Context context;
    String today;
    private final DownloadDialog downloadStatus;
    int maxData = 0;
    int month = 1;

    public SynchData(Context context, Calendar callDAte){
        this.context = context;


        DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", new Locale("id"));
        today = format2.format(callDAte.getTime());
        month = callDAte.get(Calendar.MONTH)+1;
//        today = "2020-10-19";
        downloadStatus = new DownloadDialog(context);


        WarningWindow warningWindow = new WarningWindow(context);
        warningWindow.show("Perhatian","Pastikan anda sudah mengupload data sebelumnya, karena data draft akan di hapus dan pastikan anda memiliki jaringan yang bagus untuk download data");
        warningWindow.setOnSelectedListener(status -> {
            if (status == 2){

                FileProcessing.clearImage(context,"/Wadaro/survey/draft");
                ProcessSurveyDB processSurveyDB = new ProcessSurveyDB();
                processSurveyDB.clearData(context);

                SumarySurveyDB surveyDB = new SumarySurveyDB();
                surveyDB.clearData(context);

                SurveyDeatailDB surveyDeatailDB = new SurveyDeatailDB();
                surveyDeatailDB.clearData(context);

                TempDB tempDB = new TempDB();
                tempDB.clearData(context);

                downloadStatus.show();
                downloadStatus.setTitleCustom("Download data Tanggal "+today);
                getDataAssignment();
            }
        });

    }


    private void getDataAssignment(){
        PostManager post = new PostManager(context,"assignment/survey/offline?date="+today+"&month="+month);
        post.showloading(false);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            MyPreference.delete(context, Global.PREF_DATA_ASSIGNMENT);
            if (code == ErrorCode.OK){
                try {
                    MyPreference.save(context,Global.PREF_DATA_ASSIGNMENT, obj.getJSONObject("data").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                downloadStatus.setCompleteStep(0);
            }
            loadDataDetailAssignment();
        });
    }

    private void getRecapData(){
        PostManager post  = new PostManager(context,"survey/report/recap?survey_date="+today+"&delivery_date="+today);
        post.showloading(false);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            MyPreference.delete(context, Global.PREF_DATA_RECAP);
            if (code == ErrorCode.OK){
                MyPreference.save(context,Global.PREF_DATA_RECAP, obj.toString());
            }
            MyPreference.save(context, Global.PREF_SYNCH_DATE, today);
            downloadStatus.dismiss();
//            loadDataDetailAssignment();
        });
    }

    private ArrayList<String> salesIDs = new ArrayList<>();
    private void loadDataDetailAssignment(){
        salesIDs.clear();
        String dataAssignment = MyPreference.getString(context,Global.PREF_DATA_ASSIGNMENT);
        if (dataAssignment.isEmpty()){
            downloadStatus.dismiss();
            return;
        }
        try {
            JSONObject obj = new JSONObject(dataAssignment);
            JSONArray booking = obj.getJSONArray("booking");
            for (int i=0; i<booking.length(); i++){
               salesIDs.add(booking.getJSONObject(i).getString("sales_id"));
            }
            maxData = salesIDs.size();
            if (maxData == 0){
                downloadStatus.dismiss();
                Utility.showToastError(context,"Data tidak ditemukan");
                return;
            }
            loadDetailAssign();
        } catch (JSONException e) {
            e.printStackTrace();
            downloadStatus.setErrorStep(1);
            if (maxData == 0){
                downloadStatus.dismiss();
                Utility.showToastError(context,"Data tidak ditemukan");
            }
        }
    }

    private void loadDetailAssign(){
        int current = maxData - salesIDs.size();
        downloadStatus.updateProgress(current, maxData);

        if (salesIDs.size() == 0){
            downloadStatus.setCompleteStep(1);
            loadProcessSurvey();
            return;
        }
        String param = "?sales_id="+salesIDs.get(0);
        PostManager post = new PostManager(context,"assignment/survey/detail"+param);
        post.showloading(false);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (salesIDs.size() > 0){
                TempDB tempDB = new TempDB();
                tempDB.id =salesIDs.get(0);
                tempDB.data = obj.toString();
                tempDB.keydata = TempDB.DETAIL_SURVEY;
                tempDB.insert(context);
                salesIDs.remove(0);
                loadDetailAssign();
            }
            else {
                loadProcessSurvey();
            }

        });
    }

    private void loadProcessSurvey(){
        salesIDs.clear();
        String dataAssignment = MyPreference.getString(context,Global.PREF_DATA_ASSIGNMENT);
        if (dataAssignment.isEmpty()){
            downloadStatus.dismiss();
            return;
        }
        try {
            JSONObject obj = new JSONObject(dataAssignment);
            JSONArray booking = obj.getJSONArray("booking");
            for (int i=0; i<booking.length(); i++){
                salesIDs.add(booking.getJSONObject(i).getString("sales_id"));
            }
            processSurvey();
        } catch (JSONException e) {
            e.printStackTrace();
            downloadStatus.setErrorStep(2);
        }
    }

    private final ArrayList<String> paramProcess = new ArrayList<>();
    private void processSurvey(){
        int current = maxData - salesIDs.size();
        downloadStatus.updateProgress(current, maxData);

        if (salesIDs.size() == 0){
            maxData = paramProcess.size();
            downloadStatus.setCompleteStep(2);
            loadDetailSurvey();
            return;
        }
        String param = "?sales_id="+salesIDs.get(0);
        PostManager post = new PostManager(context,"process-survey"+param);
        post.showloading(false);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (salesIDs.size() > 0){
                if (code == ErrorCode.OK){
                    try {
                        JSONObject data = obj.getJSONObject("data");
                        JSONObject sales = data.getJSONObject("sales");
                        JSONArray orderDetails = data.getJSONArray("order_details");
                        for (int i=0; i<orderDetails.length(); i++){
                            JSONObject dtl  = orderDetails.getJSONObject(i);
                            String id       = dtl.getString("id");
                            String consumen = dtl.getString("consumen_id");
                            String salesid  = sales.getString("sales_id");
                            paramProcess.add("detail?id="+id+"&consumen_id="+consumen+"&sales_id="+salesid);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    TempDB tempDB = new TempDB();
                    tempDB.id =salesIDs.get(0);
                    tempDB.data = obj.toString();
                    tempDB.keydata = TempDB.PROCESS_SURVEY;
                    tempDB.insert(context);
                    salesIDs.remove(0);
                    new Handler().postDelayed(this::processSurvey,500);

                }
                else {
                    Utility.showToastError(context,"Gagal "+message);
                }

            }
            else {
                maxData = paramProcess.size();
               loadDetailSurvey();
            }

        });
    }

    private void loadDetailSurvey(){
        int current = maxData - paramProcess.size();
        downloadStatus.updateProgress(current, maxData);

        if (paramProcess.size() == 0){
            downloadStatus.setCompleteStep(3);
            return;
        }
        String param = paramProcess.get(0);
        PostManager post = new PostManager(context,"process-survey/"+param);
        post.showloading(false);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (paramProcess.size() > 0){
                if (code == ErrorCode.OK){
                    TempDB tempDB   = new TempDB();
                    tempDB.id       = param;
                    tempDB.data     = obj.toString();
                    tempDB.keydata  = TempDB.PROCESS_SURVEY_DTL;
                    tempDB.insert(context);
                    paramProcess.remove(0);
                }
                else {
                    Utility.showToastError(context,"Gagal "+message);
                }
                loadDetailSurvey();

            }
            else {
                downloadStatus.dismiss();
            }
        });
    }



}
