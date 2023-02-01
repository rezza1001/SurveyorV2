package com.wadaro.surveyor.ui.report;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyFragment;
import com.wadaro.surveyor.ui.report.adapter.BookingAdapter;
import com.wadaro.surveyor.ui.report.adapter.BookingHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;


public class TabelFragment extends MyFragment {

    private ArrayList<BookingHolder> allData = new ArrayList<>();
    private BookingAdapter mAdapter;

    private TextView txvw_size_00;
    private ArrayList<String> dataMaps = new ArrayList<>();

    @Override
    protected int setlayout() {
        return R.layout.fragment_laporan_tabel;
    }

    @Override
    protected void initLayout(View view) {
        RecyclerView rcvw_data_00 = view.findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));
        rcvw_data_00.setNestedScrollingEnabled(false);

        mAdapter = new BookingAdapter(mActivity, allData);
        rcvw_data_00.setAdapter(mAdapter);

        txvw_size_00 = view.findViewById(R.id.txvw_size_00);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnSelectedListener(data -> {
            Intent intent = new Intent(mActivity, ReportDetailActivity.class);
            intent.putExtra("DATA", data.data.toString());
            intent.putExtra("ID", data.booking_id);
            startActivity(intent);
        });
    }


    @SuppressLint("SetTextI18n")
    private void loadData(String date, String demo){
        String param = "?booking_date="+date+"&booking_demo="+demo+"";
        PostManager post = new PostManager(mActivity,"booking/find"+param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            allData.clear();
            dataMaps.clear();
            if (code == ErrorCode.OK){
                try {
                    JSONArray data = obj.getJSONObject("data").getJSONArray("booking");
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    for (int i=0; i<data.length(); i++){
                        JSONObject objData = data.getJSONObject(i);
                        BookingHolder book = new BookingHolder();
                        book.booking_demo = objData.getString("booking_demo");
                        book.booking_id = objData.getString("booking_id");
                        book.coordinator_name = objData.getString("coordinator_name");
                        book.booking_date = df.parse(objData.getString("booking_date"));
                        book.sales = "-";
                        book.status = objData.getString("booking_status");
                        book.data = objData;
                        allData.add(book);
                        if (objData.getString("coordinator_location").split(",").length> 1){
                            String name = objData.getString("coordinator_name");
                            String latitude = objData.getString("coordinator_location").split(",")[0];
                            String longitude = objData.getString("coordinator_location").split(",")[1];
                            dataMaps.add(name+"::"+longitude+"::"+latitude);
                        }
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
            mAdapter.notifyDataSetChanged();
            txvw_size_00.setText("Total ada "+allData.size()+" titik booking");
            if (dataMaps.size() > 0){
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent("LOAD_DATA");
                    intent.putStringArrayListExtra("data",dataMaps );
                    Objects.requireNonNull(getActivity()).sendBroadcast(intent);
                },1000);

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("PARAMETER");
        mActivity.registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("PARAMETER")){
            loadData(intent.getStringExtra("DATE"),intent.getStringExtra("DEMO"));
        }
        }
    };
}
