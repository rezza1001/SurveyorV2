package com.wadaro.surveyor.ui.assignment.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.Config;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.FormPost;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.Global;
import com.wadaro.surveyor.base.MyFragment;
import com.wadaro.surveyor.database.MyPreference;
import com.wadaro.surveyor.module.FailedWindow;
import com.wadaro.surveyor.module.FileProcessing;
import com.wadaro.surveyor.module.SuccessWindow;
import com.wadaro.surveyor.module.Utility;
import com.wadaro.surveyor.ui.assignment.DenahActivity;
import com.wadaro.surveyor.ui.assignment.DetilActivity;
import com.wadaro.surveyor.ui.assignment.adapter.AssignmentAdapter;
import com.wadaro.surveyor.ui.assignment.adapter.AssignmentHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class TabelFragment extends MyFragment {

    private final ArrayList<AssignmentHolder> filterData = new ArrayList<>();
    private ArrayList<AssignmentHolder> allData = new ArrayList<>();
    private AssignmentAdapter mAdapkter;
    private TextView txvw_size_00;
    private RoundedImageView imvw_denah_00;
    private static final String photo_path      = "/Wadaro/survey/";

    DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", new Locale("id"));
    String paramDate = "";
    int month = 1;
    private ArrayList<String> dataMaps = new ArrayList<>();

    public static TabelFragment newInstance() {
        Bundle args = new Bundle();

        TabelFragment fragment = new TabelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setlayout() {
        return R.layout.fragment_tabel;
    }

    @Override
    protected void initLayout(View view) {

        RecyclerView rcvw_data_00 = view.findViewById(R.id.rcvw_data_00);
        rcvw_data_00.setLayoutManager(new LinearLayoutManager(mActivity));
//        rcvw_data_00.setNestedScrollingEnabled(false);

        mAdapkter = new AssignmentAdapter(mActivity, filterData);
        rcvw_data_00.setAdapter(mAdapkter);

        txvw_size_00 = view.findViewById(R.id.txvw_size_00);
        imvw_denah_00 = view.findViewById(R.id.imvw_denah_00);
    }

    @Override
    protected void initListener() {
        mAdapkter.setOnSelectedListener(data -> {
            Intent intent = new Intent(mActivity, DetilActivity.class);
            intent.putExtra("DATA", data.data.toString());
            intent.putExtra("ID", data.no_so);
            startActivity(intent);
        });

        imvw_denah_00.setOnClickListener(v -> {
            if (imvw_denah_00.getTag() == null){
                Utility.showToastError(mActivity,"Gambar tidak tersedia!");
                return;
            }
            Intent intent = new Intent(mActivity, DenahActivity.class);
            intent.putExtra("url", imvw_denah_00.getTag().toString());
            intent.putExtra("title", "Denah");
            startActivity(intent);
        });

        Objects.requireNonNull(getView()).findViewById(R.id.bbtn_upload_00).setOnClickListener(v -> performFileSearch());
    }

    @Override
    protected void initData() {
        FileProcessing.createFolder(mActivity,photo_path);
        FileProcessing.clearImage(mActivity,photo_path);
        paramDate = format2.format(new Date());
        month = Calendar.getInstance().get(Calendar.MONTH)+1;
        loadData();
    }

    @SuppressLint("SetTextI18n")
    private void loadData(){
        Log.d("TabelFragment","load "+paramDate);
        filterData.clear();

        if (MyPreference.getInt(mActivity, Global.PREF_OFFLINE_MODE) == 1){
            String data = MyPreference.getString(mActivity, Global.PREF_DATA_ASSIGNMENT);
            if (data.isEmpty()){
                FailedWindow failedWindow = new FailedWindow(mActivity);
                failedWindow.setDescription("Anda belum download data hari ini");
                failedWindow.show();
                return;
            }
            try {
                JSONObject obj = new JSONObject();
                obj.put("data",new JSONObject(data));
                buildData(ErrorCode.OK, obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        PostManager post = new PostManager(mActivity,"assignment/survey?date="+paramDate+"&month="+month);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
           buildData(code, obj);
        });
    }

    @SuppressLint("SetTextI18n")
    private void buildData(int code, JSONObject obj){
        allData.clear();
        dataMaps.clear();
        if (code == ErrorCode.OK){
            try {
//                FileProcessing.stringToFile(obj.toString(),"/Wadaro/survey/","data.txt");
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                JSONObject data = obj.getJSONObject("data");
                JSONArray booking = data.getJSONArray("booking");
                for (int i=0; i<booking.length(); i++){
                    JSONObject objData = booking.getJSONObject(i);
                    AssignmentHolder book = new AssignmentHolder();
                    book.no_so = objData.getString("sales_id");
                    book.coordinator_name = objData.getString("coordinator_name");
                    book.so_date = df.parse(objData.getString("sales_date"));
                    if (!objData.isNull("delivery_date")){
                        book.send_date = df.parse(objData.getString("delivery_date")+" 00:00:00");
                    }
                    else {
                        book.send_date = null;
                    }
                    book.data = objData;
                    allData.add(book);
                    mAdapkter.notifyDataSetChanged();

                    if (objData.getString("coordinator_location").split(",").length> 1){
                        String demo = objData.getString("booking_demo");
                        String latitude = objData.getString("coordinator_location").split(",")[0];
                        String longitude = objData.getString("coordinator_location").split(",")[1];
                        dataMaps.add(demo+"::"+longitude+"::"+latitude);
                    }
                }
                if (!data.isNull("denah") && !data.getString("denah").equals("null")){
                    Glide.with(mActivity).load(Config.IMAGE_PATH_BOOKER+data.getString("denah")).into(imvw_denah_00);
                    imvw_denah_00.setTag(Config.IMAGE_PATH_BOOKER+data.getString("denah"));
                }
                else {
                    Glide.with(mActivity).load("").into(imvw_denah_00);
                    imvw_denah_00.setImageResource(0);
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
        filter("");
        txvw_size_00.setText("Total ada "+ allData.size()+" titik survey");
        if (dataMaps.size() > 0){
            new Handler().postDelayed(() -> {
                Intent intent = new Intent("LOAD_DATA");
                intent.putStringArrayListExtra("data",dataMaps );
                if (isAdded()){
                    Objects.requireNonNull(getActivity()).sendBroadcast(intent);
                }
            },1000);
        }
    }

    private void filter(String text){
        filterData.clear();
        if (text.isEmpty()){
            filterData.addAll(allData);
        }
        else {
            for (AssignmentHolder holder : allData){
                if (holder.coordinator_name.toUpperCase().contains(text.toUpperCase())){
                    filterData.add(holder);
                }
            }
        }
        mAdapkter.notifyDataSetChanged();
    }

    private static final int READ_REQUEST_CODE = 42;
    private void performFileSearch() {

        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                Uri uri = resultData.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(),uri);
                    FileProcessing fp = new FileProcessing();
                    fp.saveToTmp(mActivity,bitmap,photo_path,"denah.jpeg");
                    fp.setOnSavedListener((path, name) -> uploadDenah());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("PARAMETER");
        intentFilter.addAction("REFRESH");
        intentFilter.addAction("FINISH_SURVEY");
        intentFilter.addAction("SEARCH");
        mActivity.registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mActivity.unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void uploadDenah(){
        if (FileProcessing.openImage(mActivity,photo_path,"denah.jpeg") == null){
            Utility.showToastError(mActivity,"Silahkan pilih Gambar Denah!");
            return;
        }
        File sd     = FileProcessing.getMainPath(mActivity);
        FormPost post = new FormPost(mActivity,"assignment/survey/upload-denah");
        post.addImage("photo_denah",sd.getAbsolutePath()+photo_path+"denah.jpeg");
        post.execute();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                SuccessWindow successWindow = new SuccessWindow(mActivity);
                successWindow.setDescription("Denah berhasil di upload!");
                successWindow.show();
                successWindow.setOnFinishListener(() -> {
                    FileProcessing.clearImage(mActivity,photo_path);
                    try {
                        JSONObject data = obj.getJSONObject("data");
                        Glide.with(mActivity).load(Config.IMAGE_PATH_BOOKER+data.getString("photo_denah")).into(imvw_denah_00);
                        imvw_denah_00.setTag(Config.IMAGE_PATH_BOOKER+data.getString("photo_denah"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
            else {
                FailedWindow failedWindow = new FailedWindow(mActivity);
                failedWindow.setDescription(message);
                failedWindow.show();
            }
        });
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case "REFRESH":
                case "FINISH_SURVEY":
                    loadData();
                    break;
                case "SEARCH":
                    filter(Objects.requireNonNull(intent.getStringExtra("text")));
                    break;
                case "PARAMETER":
                    paramDate = intent.getStringExtra("DATE");
                    month = intent.getIntExtra("MONTH", 1);
                    loadData();
                    break;
            }
        }
    };

}
