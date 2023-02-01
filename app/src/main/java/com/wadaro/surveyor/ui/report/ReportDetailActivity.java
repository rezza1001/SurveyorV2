package com.wadaro.surveyor.ui.report;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.module.Utility;
import com.wadaro.surveyor.ui.assignment.KeyValView;
import com.wadaro.surveyor.ui.assignment.adapter.OrderAdapter;
import com.wadaro.surveyor.ui.assignment.adapter.OrderHolder;

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

public class ReportDetailActivity extends MyActivity {


    private LinearLayout lnly_body_00;
    private ArrayList<OrderHolder> allConsumers = new ArrayList<>();
    private OrderAdapter mAdapter ;

    @Override
    protected int setLayout() {
        return R.layout.activity_laporan_hasil_booking_detil;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_title_00 = findViewById(R.id.txvw_titile_00);
        txvw_title_00.setText("Detail Laporan");

        lnly_body_00 = findViewById(R.id.lnly_body_00);
        RecyclerView rcvw_data_00 = findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));
        rcvw_data_00.setNestedScrollingEnabled(false);
        mAdapter = new OrderAdapter(mActivity, allConsumers);
        rcvw_data_00.setAdapter(mAdapter);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        lnly_body_00.removeAllViews();
        String param = "?booking_id=" + getIntent().getStringExtra("ID");
        PostManager post = new PostManager(mActivity, "booking/detail" + param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK) {
                try {
                    JSONObject intent = new JSONObject(Objects.requireNonNull(getIntent().getStringExtra("DATA")));

                    JSONObject data = obj.getJSONObject("data");
                    JSONObject booking = data.getJSONObject("booking");
                    JSONObject branch = booking.getJSONObject("branch");
                    JSONObject coordinator = booking.getJSONObject("coordinator");
                    DateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    DateFormat f2 = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
                    DateFormat f3 = new SimpleDateFormat("HH:mm", new Locale("id"));
                    Date booking_date = f1.parse(booking.getString("date"));

                    create("Nama DB / KACAB",booking.getJSONObject("booker").getString("name"));
                    create("NIK",booking.getJSONObject("booker").getString("id"));
                    create("Nama KACAB",branch.getString("manager_name"));
                    create("Nama KAWIL",branch.getString("regional_manager_name"));
                    create("Cabang",branch.getString("name"));
                    create("PT",intent.getString("company_name"));
                    View view = new View(mActivity);
                    view.setBackgroundColor(Color.parseColor("#33000000"));
                    LinearLayout.LayoutParams lp_view = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utility.dpToPx(mActivity,1));
                    lp_view.topMargin = Utility.dpToPx(mActivity,10);
                    lp_view.bottomMargin = Utility.dpToPx(mActivity,10);
                    lnly_body_00.addView(view,lp_view);
                    create("Status",booking.getString("status"));
                    create("Jadwal Demo","Demo "+ booking.getString("demo"));
                    assert booking_date != null;
                    create("Tanggal Booking",f2.format(booking_date));
                    create("Jam Demo",f3.format(booking_date));

                    if (booking.has("promotor") && !booking.isNull("promotor")){
                        JSONObject promotor = booking.getJSONObject("promotor");
                        create("Sales Promotor",promotor.isNull("name")? "-":booking.getString("name"));
                    }

                    create("Koordinator",coordinator.getString("name"));
                    create("Alamat",coordinator.getString("address"));
                    create("Koordinat",coordinator.getString("location"));
                    create("No KTP",coordinator.getString("ktp"));
                    create("Nomor HP",coordinator.getString("phone"));


                    JSONArray consumer = data.getJSONArray("consumers");
                    allConsumers.clear();
                    for (int i = 0; i < consumer.length(); i++) {
                        JSONObject csm = consumer.getJSONObject(i);
//                        allConsumers.add(new OrderHolder(csm.getString("name"), csm.getString("phone"), csm.getString("ktp")));
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
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

        @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
    }
}
