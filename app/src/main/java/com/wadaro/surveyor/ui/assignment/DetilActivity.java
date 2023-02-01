package com.wadaro.surveyor.ui.assignment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.Global;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.database.MyPreference;
import com.wadaro.surveyor.database.table.SumarySurveyDB;
import com.wadaro.surveyor.database.table.TempDB;
import com.wadaro.surveyor.module.FailedWindow;
import com.wadaro.surveyor.module.Utility;
import com.wadaro.surveyor.ui.assignment.adapter.OrderAdapter;
import com.wadaro.surveyor.ui.assignment.adapter.OrderHolder;
import com.wadaro.surveyor.ui.draft.DraftSurveyActivity;
import com.wadaro.surveyor.ui.survey.MainSurvey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class DetilActivity extends MyActivity {
    private static final int REQ_EDIt = 11;

    private LinearLayout lnly_body_00,lnly_action_00;
    private ArrayList<OrderHolder> orders = new ArrayList<>();
    private OrderAdapter mAdapter ;
    private TextView txvw_size_00;
    private Button bbtn_process_00;
    private ImageView imvw_coordinator_00,imvw_location_00;

    @Override
    protected int setLayout() {
        return R.layout.assignment_activity_detail;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        lnly_body_00 = findViewById(R.id.lnly_body_00);
        txvw_size_00 = findViewById(R.id.txvw_size_00);
        bbtn_process_00 = findViewById(R.id.bbtn_process_00);
        imvw_coordinator_00 = findViewById(R.id.imvw_coordinator_00);
        imvw_location_00 = findViewById(R.id.imvw_location_00);
        lnly_action_00 = findViewById(R.id.lnly_action_00);
        RecyclerView rcvw_data_00 = findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));
        rcvw_data_00.setNestedScrollingEnabled(false);
        mAdapter = new OrderAdapter(mActivity, orders);
        rcvw_data_00.setAdapter(mAdapter);

        TextView txvw_title_00 = findViewById(R.id.txvw_titile_00);
        txvw_title_00.setText("Penugasan Surveyor");

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        refresh(getIntent().getStringExtra("ID"));
    }

    @SuppressLint("SetTextI18n")
    private void refresh(String id){
        lnly_body_00.removeAllViews();

        if (offlineMode){
            SumarySurveyDB surveyDB = new SumarySurveyDB();
            surveyDB.getData(mActivity,id);
            if (!surveyDB.id.isEmpty()){
                bbtn_process_00.setText("Lihat Draft");
            }

            TempDB tempDB = new TempDB();
            ArrayList<TempDB> dbs = tempDB.getAllData(mActivity, id,TempDB.DETAIL_SURVEY);
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

        lnly_action_00.setVisibility(View.VISIBLE);
        String param = "?sales_id="+id;
        PostManager post = new PostManager(mActivity,"assignment/survey/detail"+param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                buildData(obj);
            }
        });
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

            create("Nama Surveyor",mUser.name);
            create("No.SO",data.getString("sales_id"));
            if (!data.isNull("delivery_date")){
                create("Tanggal Kirim", f2.format(Objects.requireNonNull(f1.parse(data.getString("delivery_date") + " 00:00:00"))));
            }
            create("Nama Sales Promotor",data.getString("promotor_name"));
            create("Keterangan",data.getString("sales_note"));
            addLine();
            create("Tanggal Demo",f2.format(Objects.requireNonNull(f1.parse(data.getString("booking_date")))));
            create("Jadwal Demo","Demo "+ data.getString("booking_demo"));
            create("Jam Demo",f3.format(Objects.requireNonNull(f1.parse(data.getString("booking_date")))));
            addLine();
            create("Koordinator",data.getString("coordinator_name"));
            create("Alamat",data.getString("coordinator_address"));
            create("Catatan Lokasi",data.getString("coordinator_note"));
            create("Koordinat",data.getString("coordinator_location"));
            create("No.KTP",data.getString("coordinator_ktp"));
            create("No.HP",data.getString("coordinator_phone"));

            String photo_coordinator;
            String photo_location ;
            if (data.has("photo")){
                photo_coordinator = data.getJSONObject("photo").getString("coordinator");
                photo_location = data.getJSONObject("photo").getString("location");
            }
            else {
                photo_coordinator = data.getString("photo_coordinator");
                photo_location = data.getString("photo_location");
            }

            Glide.with(mActivity).load(photo_coordinator).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Utility.showToastError(mActivity,"Foto Koordinator tidak tersedia");
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    imvw_coordinator_00.setOnClickListener(v -> {
                        Intent intent = new Intent(mActivity, DenahActivity.class);
                        try {
                            intent.putExtra("url", data.getJSONObject("photo").getString("coordinator"));
                            intent.putExtra("title", "Foto Koordinator");
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                    return false;
                }
            }).into(imvw_coordinator_00);
            Glide.with(mActivity).load(photo_location).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Utility.showToastError(mActivity,"Foto Lokasi tidak tersedia");
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    imvw_location_00.setOnClickListener(v -> {
                        Intent intent = new Intent(mActivity, DenahActivity.class);
                        try {
                            intent.putExtra("url", data.getJSONObject("photo").getString("location"));
                            intent.putExtra("title", "Foto Lokasi");
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    });
                    return false;
                }
            }).into(imvw_location_00);

            orders.clear();
            for (int i=0; i<details.length(); i++){
                JSONObject datil = details.getJSONObject(i);
                OrderHolder holder = new OrderHolder();
                holder.name = datil.getString("consumen_name");
                holder.goods = datil.getString("product_name");
                holder.price = datil.has("selling_price") ? datil.getString("selling_price") : "0";
                holder.installment = datil.getString("installment");
                holder.qty = datil.getString("qty");
                holder.photo = datil.getString("consumen_picture");
                orders.add(holder);
            }
            mAdapter.notifyDataSetChanged();
            txvw_size_00.setText("Total JP ada "+ orders.size()+" orang");
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_EDIt && resultCode == RESULT_OK){
            assert data != null;
            new Handler().postDelayed(() -> {
                Intent intent = new Intent("REFRESH");
                sendBroadcast(intent);
                mActivity.finish();
            },200);

        }
        else if (requestCode == REQ_EDIt && resultCode == RESULT_CANCELED) {
            mActivity.finish();
        }
    }

    @Override
    protected void initListener() {
        bbtn_process_00.setOnClickListener(v -> {
            if (bbtn_process_00.getText().equals("Lihat Draft")){
                Intent intent = getIntent();
                intent.setClass(mActivity, DraftSurveyActivity.class);
                startActivity(intent);
                mActivity.finish();
                return;
            }
            Intent intent = getIntent();
            intent.setClass(mActivity, MainSurvey.class);
            startActivity(intent);
            mActivity.finish();
        });
        findViewById(R.id.bbtn_cancel_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        mAdapter.setOnSelectedListener(new OrderAdapter.OnSelectedListener() {
            @Override
            public void onSelected(OrderHolder data) {

            }

            @Override
            public void onViewPhoto(String data) {
                Intent intent = new Intent(mActivity, DenahActivity.class);
                if (!data.isEmpty()){
                    intent.putExtra("url", data);
                    intent.putExtra("title", "Foto KTP");
                    startActivity(intent);
                }

            }
        });
    }

    private void create(String key, String value){
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

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter("FINISH_SURVEY"));
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
            if (intent.getAction().equals("FINISH_SURVEY") || intent.getAction().equals("FINISH_DTL_SURVEY")){
                mActivity.finish();
            }
        }
    };


}
