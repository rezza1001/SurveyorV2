package com.wadaro.surveyor.ui.survey;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.ObjectApi;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.Global;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.component.InputBasicView;
import com.wadaro.surveyor.component.SelectActivity;
import com.wadaro.surveyor.component.SelectHolder;
import com.wadaro.surveyor.component.SelectView;
import com.wadaro.surveyor.component.WarningWindow;
import com.wadaro.surveyor.database.MyPreference;
import com.wadaro.surveyor.database.table.SumarySurveyDB;
import com.wadaro.surveyor.database.table.SurveyDeatailDB;
import com.wadaro.surveyor.database.table.TempDB;
import com.wadaro.surveyor.module.FailedWindow;
import com.wadaro.surveyor.module.Utility;
import com.wadaro.surveyor.ui.assignment.KeyValView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainSurvey extends MyActivity {

    private static final int REQ_ADD_ORDER = 2;
    private static final int REQ_SURVEY = 3;
    private static final int REQ_DATE = 4;
    private LinearLayout lnly_body_00;
    private SelectView slvw_senddate_00;
    private InputBasicView input_note_00;
    private ImageView imvw_add_00;
    private final ArrayList<OrderHolder> orders = new ArrayList<>();
    private OrderAdapter mAdapter;
    private String salesID = "";
    private String bokingID = "";
    private String mCoorAddress = "-";
    private final ArrayList<String> deliveryDate = new ArrayList<>();
    private Button bbtn_save_00;

    @Override
    protected int setLayout() {
        return R.layout.survey_activity_main;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_title_00 = findViewById(R.id.txvw_titile_00);
        txvw_title_00.setText("Survey");

        imvw_add_00     = findViewById(R.id.imvw_add_00);
        lnly_body_00     = findViewById(R.id.lnly_body_00);
        slvw_senddate_00 = findViewById(R.id.slvw_senddate_00);
        input_note_00 = findViewById(R.id.input_note_00);
        bbtn_save_00 = findViewById(R.id.bbtn_save_00);
        slvw_senddate_00.setHint("Tanggal Kirim");
        input_note_00.setLabel("Keterangan");

        mAdapter = new OrderAdapter(mActivity, orders);
        RecyclerView rcvw_order_00 = findViewById(R.id.rcvw_order_00);
        rcvw_order_00.setLayoutManager(new LinearLayoutManager(mActivity));
        rcvw_order_00.setNestedScrollingEnabled(false);
        rcvw_order_00.setAdapter(mAdapter);

        bbtn_save_00.setTag(0);
        bbtn_save_00.setBackgroundColor(Color.LTGRAY);
    }

    @Override
    protected void initData() {
        salesID = getIntent().getStringExtra("ID");
        if (offlineMode){
            imvw_add_00.setVisibility(View.INVISIBLE);
        }
        refresh();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        slvw_senddate_00.setSelectedListener(data -> {
            Intent intent = new Intent(mActivity, SelectActivity.class);
            intent.putStringArrayListExtra("DATA",deliveryDate);
            startActivityForResult(intent, REQ_DATE);
        });

        imvw_add_00.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, AddOrderActivity.class);
            intent.putExtra("ID", salesID);
            intent.putExtra("BOOKING_ID", bokingID);
            intent.putExtra("COOR_ADDRESS", mCoorAddress);
            intent.setAction("ADD");
            startActivityForResult(intent, REQ_ADD_ORDER);
        });

        bbtn_save_00.setOnClickListener(v -> {
            if (bbtn_save_00.getTag().toString().equals("0")){
                Utility.showToastError(mActivity,"Anda belum melakukan survey");
                return;
            }
            WarningWindow warningWindow = new WarningWindow(mActivity);
            warningWindow.show("Anda Yakin?", "Anda akan menyelesaikan survey ini!");
            warningWindow.setOnSelectedListener(status -> {
                if (status == 2){
                    saveSurvey();
                }
            });
        });

        mAdapter.setOnSelectedListener((data, action) -> {
            if (action.equals("Delete")){
                if (data.survey.equals("1")){
                    return;
                }
                delete(data.tmpId);
            }
            else if (action.equals("Edit")){
                if (data.survey.equals("1")){
                    return;
                }
                Intent intent = new Intent(mActivity, AddOrderActivity.class);
                intent.setAction("EDIT");
                intent.putExtra("ID", salesID);
                intent.putExtra("BOOKING_ID", bokingID);
                intent.putExtra("COOR_ADDRESS", mCoorAddress);
                intent.putExtra("DATA", data.pack().toString());
                startActivityForResult(intent, REQ_ADD_ORDER);
            }
            else {
                Intent intent = new Intent(mActivity, SurveyOrderActivity.class);
                intent.putExtra("SURVEY_ID", data.tmpId);
                intent.putExtra("ID", salesID);
                intent.putExtra("CONSUMER", data.customer);
                startActivityForResult(intent, REQ_SURVEY);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == REQ_DATE && resultCode == RESULT_OK){
            assert data != null;
            slvw_senddate_00.setValue(new SelectHolder(data.getStringExtra("DATA")));
        }
        else if (requestCode == REQ_ADD_ORDER && resultCode == RESULT_OK){
            refresh();
        }
        else if (requestCode == REQ_SURVEY && resultCode == RESULT_OK){
            refresh();
        }
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


    private void refresh(){
        lnly_body_00.removeAllViews();
        deliveryDate.clear();
        if (offlineMode){
            mAdapter.disableEdit();
            TempDB tempDB = new TempDB();
            ArrayList<TempDB> dbs = tempDB.getAllData(mActivity, salesID,TempDB.PROCESS_SURVEY);
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
            return;
        }

        String param = "?sales_id="+salesID;
        PostManager post = new PostManager(mActivity,"process-survey"+param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
               buildData(obj);
            }
        });
    }

    private void buildData(JSONObject obj){
        try {
            JSONObject data = obj.getJSONObject("data");
            JSONObject sales = data.getJSONObject("sales");
            JSONArray details = data.getJSONArray("order_details");
            JSONArray delivery = data.getJSONArray("delivery_plan");

            DateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            DateFormat f2 = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
            lnly_body_00.removeAllViews();
            addInfo("Nama Sales Promotor",mUser.name);
            addInfo("Nama DB/KCAB","-");
            addInfo("Nama KCAB",sales.getString("branch_manager_name"));
            addInfo("Nama KAWIL",sales.getString("regional_manager_name"));
            addInfo("Cabang",mUser.branch_name);
            addInfo("PT",mUser.company_name);
            addLine();
            addInfo("Tanggal Demo",f2.format(Objects.requireNonNull(f1.parse(sales.getString("booking_date")))));
            addInfo("Jadwal Demo",sales.getString("booking_demo"));
            addInfo("Koordinator",sales.getString("coordinator_name"));
            addInfo("Alamat",sales.getString("coordinator_address"));
            addInfo("Koordinat", sales.getString("coordinator_location"));
            addInfo("No.KTP",sales.getString("coordinator_ktp"));
            addInfo("No.HP",sales.getString("coordinator_phone"));
            addLine();
            addInfo("No. Sales Order",sales.getString("sales_id"));
            mCoorAddress = sales.getString("coordinator_address");
            bokingID = sales.getString("booking_id");
            input_note_00.setValue(sales.getString("sales_note"));


            orders.clear();
            int countSurvey = 0;
            for (int i=0; i<details.length(); i++){
                JSONObject datil = details.getJSONObject(i);
                OrderHolder holder = new OrderHolder();
                holder.customerName = datil.getString("consumen_name");
                holder.customer = datil.getString("consumen_id");
                holder.goodsName = datil.getString("product_name");
                holder.goods = datil.getString("product_id");
                holder.qty = datil.getString("qty");
                holder.tmpId = datil.getString("id");
                if (!datil.isNull("sales_detail_status")){
                    holder.survey = "1";
                    countSurvey ++;
                }
                if (offlineMode){
                    String reff = "consumen_id="+holder.customer +"&sales_id="+salesID;
                    SurveyDeatailDB deatailDB = new SurveyDeatailDB();
                    deatailDB.getData(mActivity, reff);
                    if (!deatailDB.reffId.isEmpty()){
                        holder.survey = "1";
                        countSurvey ++;
                    }
                }
                orders.add(holder);
            }
            Log.d(TAG,"countSurvey "+countSurvey);
            if (countSurvey == details.length()){
                if (bbtn_save_00.getTag().toString().equals("0")){
                    bbtn_save_00.setTag(1);
                    bbtn_save_00.setBackgroundResource(R.drawable.ripple_btn);
                }
            }
            mAdapter.notifyDataSetChanged();
            DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", new Locale("id"));
            DateFormat format2 = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));

            Log.d(TAG,"delivery "+delivery.toString());

            String salesDlive = format2.format(Objects.requireNonNull(format1.parse(sales.getString("delivery_date"))));
            Log.d(TAG,"delivery_date "+salesDlive);

//            String valueDumy = format2.format(new Date());
//            SelectHolder dumyholder = new SelectHolder(format1.format(new Date()),valueDumy);
//            deliveryDate.add(dumyholder.toString());

            for (int i=0; i<delivery.length(); i++){
                JSONObject delyveryDate = delivery.getJSONObject(i);
                String value = format2.format(Objects.requireNonNull(format1.parse(delyveryDate.getString("start"))));
                SelectHolder holder = new SelectHolder(delyveryDate.getString("start"),value);
                deliveryDate.add(holder.toString());
                if (value.equals(salesDlive)){
                    slvw_senddate_00.setValue(holder);
                }
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void delete(String id){
        PostManager post = new PostManager(mActivity,"process-survey/delete-product/"+id);
        post.execute("DELETE");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                Utility.showToastSuccess(mActivity,message);
                refresh();
            }
            else {
                Utility.showToastError(mActivity, message);
            }
        });
    }

    private void saveSurvey(){
        if (slvw_senddate_00.getKey().isEmpty()){
            Utility.showToastError(mActivity,"Pilih Tanggal Kirim!");
            return;
        }
        DateFormat format1 = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
        DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", new Locale("id"));
        String dateSend = "";
        try {
            dateSend = format2.format(Objects.requireNonNull(format1.parse(slvw_senddate_00.getValue())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        PostManager post = new PostManager(mActivity,"process-survey/save");
        post.addParam(new ObjectApi("sales_id",salesID));
        post.addParam(new ObjectApi("delivery_date",dateSend));
        post.addParam(new ObjectApi("sales_code","-"));
        post.addParam(new ObjectApi("sales_note", input_note_00.getValue().isEmpty()? "-" : input_note_00.getValue()));
        if (offlineMode){
            SumarySurveyDB surveyDB = new SumarySurveyDB();
            surveyDB.id = salesID;
            surveyDB.data = post.getData().toString();
            surveyDB.insert(mActivity);

            Utility.showToastSuccess(mActivity,"Data disimpan ke draft");
            sendBroadcast(new Intent("FINISH_SURVEY"));
            mActivity.finish();
        }
        else {
            post.execute("POST");
            post.setOnReceiveListener((obj, code, message) -> {
                if (code == ErrorCode.OK){
                    sendBroadcast(new Intent("FINISH_SURVEY"));
                    mActivity.finish();
                }
                else {
                    Utility.showToastError(mActivity, message);
                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter("FINISH_DTL_SURVEY"));
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
            if (Objects.equals(intent.getAction(), "FINISH_DTL_SURVEY")){
                refresh();
            }
        }
    };

}
