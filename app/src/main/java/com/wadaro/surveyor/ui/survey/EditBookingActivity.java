package com.wadaro.surveyor.ui.survey;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.Config;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.FormPost;
import com.wadaro.surveyor.api.ImageDownloader;
import com.wadaro.surveyor.api.ObjectApi;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.module.FailedWindow;
import com.wadaro.surveyor.module.FileProcessing;
import com.wadaro.surveyor.module.GPSTracker;
import com.wadaro.surveyor.module.ImageResizer;
import com.wadaro.surveyor.module.SuccessWindow;
import com.wadaro.surveyor.module.Utility;

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
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class EditBookingActivity extends MyActivity {

    private static final int REQ_EXISTING_NIK = 11;
    private static final int TAKE_PHOTO_COORDINATOR = 12;
    private static final int TAKE_PHOTO_LOCATION = 13;

    private TextView txvw_network_00,txvw_longlat_00;
    private Switch swtc_network_00;
    private ImageButton imbt_calendar_00, imbt_time_00;
    private TextView txtDate, txtTime;
    private Button bbtn_check_00,bbtn_save_00,bbtn_tagaddress_00;
    private EditText edtx_nik_00;
    private EditText edtx_name_00,edtx_nickname_00,edtx_phone_00,edtx_address_00,edtx_note_00;
    private Spinner spnr_city_00,spnr_book_00;
    private RelativeLayout rvly_coordinator_00,rvly_location_00;
    private RoundedImageView imvw_coordinator_00,imvw_location_00;

    private ArrayList<String> city_list = new ArrayList<>();
    private ArrayList<String> shedule_list = new ArrayList<>();
    private HashMap<String,String> shcduleHmaps = new HashMap<>();
    private ArrayAdapter<String> adapter_city;
    private ArrayAdapter<String> adapter_schedule;
    private HashMap<String,String> cityMaps = new HashMap<>();

    private int mYear, mMonth, mDay, mHour, mMinute;
    private GPSTracker gpsTracker ;
    private String mIdentity = "";
    private String mNik      = "";
    private String bookingID = "";

    private static final String photo_path      = "/Wadaro/survey/";
    
    @Override
    protected int setLayout() {
        return R.layout.ui_booking_activity_edit;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        gpsTracker = new GPSTracker(mActivity);
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Edit Booking");
        txvw_network_00 = findViewById(R.id.txvw_network_00);
        imbt_calendar_00= findViewById(R.id.imbt_calendar_00);
        imbt_time_00    = findViewById(R.id.imbt_time_00);
        bbtn_check_00   = findViewById(R.id.bbtn_check_00);
        txtDate         = findViewById(R.id.tvDisplayTanggalDemo);
        txtTime         = findViewById(R.id.tvDisplayJamDemo);
        edtx_nik_00     = findViewById(R.id.edtx_nik_00);
        edtx_name_00    = findViewById(R.id.edtx_name_00);
        edtx_phone_00   = findViewById(R.id.edtx_phone_00);
        edtx_nickname_00= findViewById(R.id.edtx_nickname_00);
        edtx_address_00 = findViewById(R.id.edtx_address_00);
        edtx_note_00    = findViewById(R.id.edtx_note_00);
        spnr_city_00    = findViewById(R.id.spnr_city_00);
        txvw_longlat_00 = findViewById(R.id.txvw_longlat_00);
        bbtn_save_00    = findViewById(R.id.bbtn_save_00);
        swtc_network_00 = findViewById(R.id.swtc_network_00);
        bbtn_tagaddress_00 = findViewById(R.id.bbtn_tagaddress_00);
        spnr_book_00 = findViewById(R.id.spnr_book_00);
        rvly_coordinator_00 = findViewById(R.id.rvly_coordinator_00);
        imvw_coordinator_00 = findViewById(R.id.imvw_coordinator_00);
        rvly_location_00 = findViewById(R.id.rvly_location_00);
        imvw_location_00 = findViewById(R.id.imvw_location_00);

        adapter_city = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, city_list);
        adapter_city.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnr_city_00.setAdapter(adapter_city);

        adapter_schedule = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, shedule_list);
        adapter_schedule.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnr_book_00.setAdapter(adapter_schedule);

        edtx_address_00.setText(gpsTracker.getAddress());
        txvw_longlat_00.setText(gpsTracker.getLongitude()+","+gpsTracker.getLatitude());

//        default
        swtc_network_00.setChecked(true);
        txvw_network_00.setText("Online");
    }

    @Override
    protected void initData() {
        FileProcessing.createFolder(mActivity,photo_path);
        FileProcessing.clearImage(mActivity,photo_path);
        getData();

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        imbt_calendar_00.setOnClickListener(v -> {
             DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(mActivity),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });
        imbt_time_00.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity, (view, hourOfDay, minute) -> {
                txtTime.setText(hourOfDay + ":" + minute);
                mMinute = minute;
                mHour = hourOfDay;

            }, mHour, mMinute, false);
            timePickerDialog.show();
        });
        bbtn_check_00.setOnClickListener(v -> checkNik());

        swtc_network_00.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                txvw_network_00.setTextColor(getResources().getColor(R.color.colorAccent));
                txvw_network_00.setText("Online");
            } else {
                txvw_network_00.setTextColor(getResources().getColor(R.color.dialog_background));
                txvw_network_00.setText("Offline");
            }
        });

        bbtn_tagaddress_00.setOnClickListener(v -> {
            gpsTracker = new GPSTracker(mActivity);
            edtx_address_00.setText(gpsTracker.getAddress());
            txvw_longlat_00.setText(gpsTracker.getLongitude()+","+gpsTracker.getLatitude());
        });

        rvly_coordinator_00.setOnClickListener(v -> openCamera("coordinator", TAKE_PHOTO_COORDINATOR));
        rvly_location_00.setOnClickListener(v -> openCamera("location", TAKE_PHOTO_LOCATION));

        bbtn_save_00.setOnClickListener(v -> audit());
    }

    private void getData(){
        city_list.clear();
        shedule_list.clear();
        PostManager post = new PostManager(mActivity,"booking/create");
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    createAddress(data.getJSONArray("address"));
                    createSchedule(data.getJSONArray("schedule_demo"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getDataBooking(getIntent().getStringExtra("ID"));

            }
        });
    }

    private void createAddress(JSONArray address) throws JSONException {
        for (int i=0; i<address.length(); i++){
            JSONObject areaObj = address.getJSONObject(i);
            city_list.add(areaObj.getString("text"));
            cityMaps.put(areaObj.getString("text"),areaObj.getString("id"));
        }
        adapter_city.notifyDataSetChanged();
    }

    private void createSchedule(JSONArray schedules) throws JSONException {
        shcduleHmaps.clear();
        for (int i=0; i<schedules.length(); i++){
            JSONObject schedule = schedules.getJSONObject(i);
            shedule_list.add(schedule.getString("text"));
            shcduleHmaps.put(schedule.getString("text"),schedule.getString("id"));
        }
        adapter_schedule.notifyDataSetChanged();
    }

    private void checkNik(){
        if (edtx_nik_00.getText().toString().isEmpty()){
            Utility.showToastError(mActivity,"NIK/No.KTP harus diisi!");
            return;
        }

        PostManager post = new PostManager(mActivity,"coordinator/"+edtx_nik_00.getText().toString()+"/check-nik");
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    Intent intent = new Intent(mActivity, CekNIKActivity.class);
                    intent.putExtra("DATA",data.toString());
                    startActivityForResult(intent, REQ_EXISTING_NIK);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_EXISTING_NIK && resultCode == -1){
            try {
                assert data != null;
                JSONObject obj = new JSONObject(Objects.requireNonNull(data.getStringExtra("DATA")));
                edtx_name_00.setText(obj.getString("coordinator_name"));
                edtx_nickname_00.setText(obj.getString("coordinator_alias_name"));
                edtx_phone_00.setText(obj.getString("coordinator_phone"));
                edtx_address_00.setText(obj.getString("coordinator_location"));
                edtx_note_00.setText(obj.getString("coordinator_note"));
                mIdentity   = obj.getString("coordinator_ktp");
                mNik        = obj.getString("coordinator_id");
                city_list.add(obj.getString("coordinator_address"));
                adapter_city.notifyDataSetChanged();
                spnr_city_00.setSelection(city_list.size()-1,true);
//                company_id  = obj.getString("company_id");
//                branch_id   = obj.getString("branch_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == TAKE_PHOTO_COORDINATOR && resultCode == -1){
            FileProcessing.deleteImage(mActivity,photo_path,"photo_coordinator.jpg");
            Utility.showToastSuccess(mActivity,"Photo saved to draf");
            Glide.with(mActivity).load(FileProcessing.openImage(mActivity,photo_path,"coordinator.jpg")).into(imvw_coordinator_00);
            ImageResizer resizer = new ImageResizer(mActivity);
            resizer.compress(photo_path+"coordinator.jpg",photo_path,"photo_coordinator.jpg");
        }
        else if (requestCode == TAKE_PHOTO_LOCATION && resultCode == -1){
            FileProcessing.deleteImage(mActivity,photo_path,"photo_location.jpg");
            Utility.showToastSuccess(mActivity,"Photo saved to draf");
            Glide.with(mActivity).load(FileProcessing.openImage(mActivity,photo_path,"location.jpg")).into(imvw_location_00);
            ImageResizer resizer = new ImageResizer(mActivity);
            resizer.compress(photo_path+"location.jpg",photo_path,"photo_location.jpg");
        }
    }

    private void saveToServer(String id, String identity, String cName, String cNick, String cPhone,
                              String cAddress, String note, String date, String longlat, String booking, String tagaddress){

        File sd     = FileProcessing.getMainPath(mActivity);

        FormPost post = new FormPost(mActivity,"booking/update?booking_id="+bookingID);
        post.addParam(new ObjectApi("booking_demo",shcduleHmaps.get(booking)));
        post.addParam(new ObjectApi("booking_date",date));
        post.addParam(new ObjectApi("coordinator_ktp",identity));
        post.addParam(new ObjectApi("coordinator_name",cName));
        post.addParam(new ObjectApi("coordinator_alias_name",cNick));
        post.addParam(new ObjectApi("coordinator_phone",cPhone));
        post.addParam(new ObjectApi("coordinator_address",tagaddress));
        post.addParam(new ObjectApi("coordinator_location",longlat));
        post.addParam(new ObjectApi("coordinator_note",note));
        post.addParam(new ObjectApi("sales_area_id",cityMaps.get(cAddress)));
        post.addImage("photo_coordinator",sd.getAbsolutePath()+photo_path+"photo_coordinator.jpg");
        post.addImage("photo_location",sd.getAbsolutePath()+photo_path+"photo_location.jpg");
        post.execute();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                SuccessWindow successWindow = new SuccessWindow(mActivity);
                successWindow.setDescription("Data berhasil disimpan!");
                successWindow.show();
                successWindow.setOnFinishListener(() -> {
                    clearField();
                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    mActivity.finish();
                });
            }
            else {
                FailedWindow failedWindow = new FailedWindow(mActivity);
                failedWindow.setDescription(message);
                failedWindow.show();
            }
        });

    }

    private void audit(){
        String booking_demo = (String) spnr_book_00.getSelectedItem();
        String nik = edtx_nik_00.getText().toString();
        String name = edtx_name_00.getText().toString();
        String nick = edtx_nickname_00.getText().toString();
        String phone = edtx_phone_00.getText().toString();
        String address = spnr_city_00.getSelectedItem().toString();
        String tagAddress = edtx_address_00.getText().toString();
        String longlat = txvw_longlat_00.getText().toString();
        String note = edtx_note_00.getText().toString();
        String date = mYear+"-"+(mMonth+1)+"-"+mDay+" "+mHour+":"+mMinute+":00";
        if (nik.isEmpty()){
            Utility.showToastError(mActivity,"No. KTP/NIK harus diisi!");
            return;
        }
        if (name.isEmpty()){
            Utility.showToastError(mActivity,"Nama harus diisi!");
            return;
        }
        if (phone.isEmpty()){
            Utility.showToastError(mActivity,"No. Telepon harus diisi!");
            return;
        }
        if (address.isEmpty()){
            Utility.showToastError(mActivity,"Area harus diisi!");
            return;
        }

        if (FileProcessing.openImage(mActivity,photo_path+"photo_coordinator.jpg") == null){
            Utility.showToastError(mActivity,"Silahkan masukan foto koordinator!");
            return;
        }
        if (FileProcessing.openImage(mActivity,photo_path+"photo_location.jpg") == null){
            Utility.showToastError(mActivity,"Silahkan masukan foto lokasi!");
            return;
        }

        if (mNik.isEmpty()){
            mNik = nik;
            mIdentity = nik;
        }

        if (nick.isEmpty()){
            nick = "-";
        }
        if (note.isEmpty()){
            note = "-";
        }

        saveToServer(mNik,mIdentity,name,nick,phone,address,note,date,longlat, booking_demo,tagAddress);
    }

    private void clearField(){
        edtx_nik_00.setText(null);
        edtx_name_00.setText(null);
        edtx_nickname_00.setText(null);
        edtx_address_00.setText(null);
        edtx_note_00.setText(null);
        edtx_phone_00.setText(null);
        FileProcessing.clearImage(mActivity,photo_path);
        Glide.with(mActivity).load(0).into(imvw_coordinator_00);
        Glide.with(mActivity).load(0).into(imvw_location_00);
        imvw_coordinator_00.setImageResource(0);
        imvw_location_00.setImageResource(0);
    }

    private void openCamera(String name, int reqID){
        String[] PERMISSIONS = {Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!hasPermissions(mActivity, PERMISSIONS)){
            ActivityCompat.requestPermissions(mActivity, PERMISSIONS, 101);
        }
        else {
            String mediaPath = FileProcessing.getMainPath(mActivity).getAbsolutePath()+photo_path;
            String file =mediaPath+ name+".jpg";
            File newfile = new File(file);
            try {
                newfile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Uri outputFileUri = FileProcessing.getUriFormFile(mActivity, newfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            startActivityForResult(cameraIntent, reqID);
        }
    }
    private boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private void getDataBooking(String id){
        String param = "?booking_id="+id;
        PostManager post = new PostManager(mActivity,"booking/detail"+param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    JSONObject booking = data.getJSONObject("booking");
                    JSONObject coordinator = booking.getJSONObject("coordinator");
                    DateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    DateFormat f2 = new SimpleDateFormat("dd-MM-yyyy", new Locale("id"));
                    DateFormat f3 = new SimpleDateFormat("HH:mm", new Locale("id"));
                    Date booking_date = f1.parse(booking.getString("date"));
                    Calendar calendar = Calendar.getInstance();
                    assert booking_date != null;
                    calendar.setTime(booking_date);

                    txtDate.setText(f2.format(calendar.getTime()));
                    mYear   = calendar.get(Calendar.YEAR);
                    mMonth  = calendar.get(Calendar.MONTH);
                    mDay    = calendar.get(Calendar.DAY_OF_MONTH);

                    txtTime.setText(f3.format(calendar.getTime()));
                    mMinute = calendar.get(Calendar.MINUTE);
                    mHour   = calendar.get(Calendar.HOUR);

                    edtx_nik_00.setText(coordinator.getString("ktp"));
                    edtx_name_00.setText(coordinator.getString("name"));
                    edtx_nickname_00.setText(coordinator.getString("alias_name"));
                    edtx_address_00.setText(coordinator.getString("address"));
                    edtx_phone_00.setText(coordinator.getString("phone"));

                    int demo = Integer.parseInt(booking.getString("demo"))-1;
                    spnr_book_00.setSelection(demo, true);

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            JSONObject obj = new JSONObject(Objects.requireNonNull(getIntent().getStringExtra("DATA")));
            ImageDownloader downloader1 = new ImageDownloader(mActivity, Config.IMAGE_PATH+obj.getString("photo_coordinator"),photo_path,"photo_coordinator.jpg");
            downloader1.execute();
            downloader1.setOnDownloadListener(new ImageDownloader.OnDownloadListener() {
                @Override
                public void onStart() {

                }
                @Override
                public void onFinih(String path, String id) {
                    String mediaPath = FileProcessing.getMainPath(mActivity)+photo_path;
                    new Handler().postDelayed(() -> {
                        Glide.with(mActivity).load(FileProcessing.openImageReal(mediaPath+"photo_coordinator.jpg")).into(imvw_coordinator_00);
                    },500);
                }
            });

            ImageDownloader downloader2 = new ImageDownloader(mActivity, Config.IMAGE_PATH+obj.getString("photo_location"),photo_path,"photo_location.jpg");
            downloader2.execute();
            downloader2.setOnDownloadListener(new ImageDownloader.OnDownloadListener() {
                @Override
                public void onStart() {

                }
                @Override
                public void onFinih(String path, String id) {
                    String mediaPath = FileProcessing.getMainPath(mActivity).getAbsolutePath()+photo_path;
                    new Handler().postDelayed(() -> Glide.with(mActivity).load(FileProcessing.openImageReal(mediaPath+"photo_location.jpg")).into(imvw_location_00),500);
                }
            });

            String areaID = obj.getString("sales_area_id");
            int position = 0;
            for (String area: city_list){
                if (cityMaps.get(area).equals(areaID)){
                    spnr_city_00.setSelection(position, true);
                    position ++;
                }
            }
            edtx_note_00.setText(obj.getString("coordinator_note"));
            bookingID = obj.getString("booking_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
