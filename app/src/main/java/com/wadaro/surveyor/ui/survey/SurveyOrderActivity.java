package com.wadaro.surveyor.ui.survey;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.Config;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.FormPost;
import com.wadaro.surveyor.api.ObjectApi;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.component.SelectHolder;
import com.wadaro.surveyor.component.SelectView;
import com.wadaro.surveyor.component.WarningWindow;
import com.wadaro.surveyor.database.table.QuestionsDB;
import com.wadaro.surveyor.database.table.SurveyDeatailDB;
import com.wadaro.surveyor.database.table.TempDB;
import com.wadaro.surveyor.module.FailedWindow;
import com.wadaro.surveyor.module.FileProcessing;
import com.wadaro.surveyor.module.ImageResizer;
import com.wadaro.surveyor.module.SuccessWindow;
import com.wadaro.surveyor.module.Utility;
import com.wadaro.surveyor.module.find.FindHolder;
import com.wadaro.surveyor.module.find.FindWindow;
import com.wadaro.surveyor.util.KtpValidator;
import com.wadaro.surveyor.util.OperatorPrifix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SurveyOrderActivity extends MyActivity {

    private static final int REQ_EXISTING_NIK = 11;
    private static final int TAKE_PHOTO_COORDINATOR = 12;
    private static final int RQE_GOODS = 10;

    private Button bbtn_check_00,bbtn_save_00,bbtn_cancel_00;
    private EditText edtx_nik_00,edtx_cost_00,edtx_note_00;
    private EditText edtx_name_00,edtx_phone_00,edtx_address_00;
    private RelativeLayout rvly_coordinator_00,lnly_reason_00;
    private RoundedImageView imvw_coordinator_00;
    private SelectView slvw_allowbuy_00,slvw_reff_00;
    private SelectView slvw_home_00,slvw_job_00,slvw_arthome_00,slvw_honesty_00,slvw_responsibility_00,slvw_people_00,slvw_point_00,slvw_reason_00;
    private LinearLayout lnly_yes_00,lnly_action_00,lnly_status_00,lnly_note_00,lnly_photo_00,lnly_body_00;
    private TextView txviw_point_00,txviw_status_00,txvw_foto_00;
    private ImageView imvw_add_00,imvw_icon_01;
    private RelativeLayout rvly_nik_00;

    private String mNik         = "";
    private String salesID      = "";
    private String surveyID      = "";
    private String consumerID   = "";
    private String saveSurveyID   = "";
    private final HashMap<String, QuestionsDB> mPoint = new HashMap<>();
    ArrayList<FindHolder> reasonCancel = new ArrayList<>();


    private final ArrayList<GoodsHolder> goodsHolders = new ArrayList<>();
    private GoodsAdapter mGoodsAdapter;

    private static final String photo_path      = "/Wadaro/survey/";

    @Override
    protected int setLayout() {
        return R.layout.survey_activity_ordersurvey;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_title_00 = findViewById(R.id.txvw_titile_00);
        txvw_title_00.setText("Survey");

        bbtn_check_00   = findViewById(R.id.bbtn_check_00);
        edtx_nik_00     = findViewById(R.id.edtx_nik_00);
        edtx_name_00    = findViewById(R.id.edtx_name_00);
        edtx_phone_00   = findViewById(R.id.edtx_phone_00);
        edtx_address_00 = findViewById(R.id.edtx_address_00);
        bbtn_save_00    = findViewById(R.id.bbtn_save_00);
        bbtn_cancel_00  = findViewById(R.id.bbtn_cancel_00);
        imvw_add_00  = findViewById(R.id.imvw_add_00);
        rvly_coordinator_00 = findViewById(R.id.rvly_coordinator_00);
        imvw_coordinator_00 = findViewById(R.id.imvw_coordinator_00);
        slvw_allowbuy_00 = findViewById(R.id.slvw_allowbuy_00);
        lnly_reason_00  = findViewById(R.id.lnly_reason_00);
        slvw_home_00  = findViewById(R.id.slvw_home_00);
        slvw_job_00  = findViewById(R.id.slvw_job_00);
        slvw_arthome_00  = findViewById(R.id.slvw_arthome_00);
        slvw_honesty_00  = findViewById(R.id.slvw_honesty_00);
        slvw_responsibility_00  = findViewById(R.id.slvw_responsibility_00);
        slvw_people_00  = findViewById(R.id.slvw_people_00);
        lnly_yes_00  = findViewById(R.id.lnly_yes_00);
        edtx_cost_00  = findViewById(R.id.edtx_cost_00);
        slvw_point_00  = findViewById(R.id.slvw_point_00);
        edtx_note_00  = findViewById(R.id.edtx_note_00);
        lnly_action_00  = findViewById(R.id.lnly_action_00);
        txviw_point_00  = findViewById(R.id.txviw_point_00);
        txviw_status_00  = findViewById(R.id.txviw_status_00);
        lnly_status_00  = findViewById(R.id.lnly_status_00);
        lnly_note_00  = findViewById(R.id.lnly_note_00);
        slvw_reason_00  = findViewById(R.id.slvw_reason_00);
        lnly_photo_00  = findViewById(R.id.lnly_photo_00);
        lnly_body_00  = findViewById(R.id.lnly_body_00);
        slvw_reff_00  = findViewById(R.id.slvw_reff_00);
        rvly_nik_00  = findViewById(R.id.rvly_nik_00);
        txvw_foto_00  = findViewById(R.id.txvw_foto_00);
        imvw_icon_01  = findViewById(R.id.imvw_icon_01);

        slvw_allowbuy_00.setHint("Bersedia Membeli Barang");
        slvw_home_00.setHint("Tempat Tinggal");
        slvw_job_00.setHint("Pekerjaan");
        slvw_arthome_00.setHint("Kelengkapan Peralatan RT");
        slvw_honesty_00.setHint("Kejujuran");
        slvw_responsibility_00.setHint("Tanggung Jawab");
        slvw_people_00.setHint("Penilaian Masyarakat");
        slvw_point_00.setHint("Penilaian");
        slvw_reason_00.setHint("Alasan");
        slvw_reff_00.setHint("Referensi");
        lnly_yes_00.setVisibility(View.GONE);
        lnly_reason_00.setVisibility(View.GONE);
        lnly_action_00.setVisibility(View.INVISIBLE);

        RecyclerView rcvw_goods_00 = findViewById(R.id.rcvw_goods_00);
        rcvw_goods_00.setLayoutManager(new LinearLayoutManager(mActivity));
        rcvw_goods_00.setNestedScrollingEnabled(false);

        mGoodsAdapter = new GoodsAdapter(mActivity, goodsHolders);
        rcvw_goods_00.setAdapter(mGoodsAdapter);
        lnly_status_00.setVisibility(View.GONE);
        lnly_photo_00.setVisibility(View.GONE);
        lnly_body_00.setVisibility(View.VISIBLE);
        rvly_nik_00.setVisibility(View.GONE);
        imvw_coordinator_00.setTag(0);
    }

    @Override
    protected void initData() {
        if (offlineMode){
            imvw_add_00.setVisibility(View.INVISIBLE);
        }
        FileProcessing.createFolder(mActivity,photo_path);
        FileProcessing.clearImage(mActivity,photo_path);
        salesID = getIntent().getStringExtra("ID");
        surveyID = getIntent().getStringExtra("SURVEY_ID");
        consumerID = getIntent().getStringExtra("CONSUMER");
        refresh();
    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
            alertDialog.setTitle("Anda yakin untuk keluar dari halaman ini?");
            alertDialog.setNegativeButton("Ya", (dialog, which) -> onBackPressed());
            alertDialog.show();
        });
        bbtn_check_00.setOnClickListener(v -> checkNik());
        rvly_coordinator_00.setOnClickListener(v -> openCamera("coordinator", TAKE_PHOTO_COORDINATOR));

        slvw_allowbuy_00.setSelectedListener(data -> {
            ArrayList<FindHolder> holders = new ArrayList<>();
            holders.add(new FindHolder("YA","YA"));
            holders.add(new FindHolder("TIDAK","TIDAK"));
            FindWindow findWindow = new FindWindow(mActivity);
            findWindow.setHeaderTitle(slvw_allowbuy_00.getHint());
            findWindow.show(holders);
            findWindow.setOnSelectedListener(data1 -> {
                slvw_allowbuy_00.setValue(new SelectHolder(data1.getKey(),data1.getValue()));
                if (data1.getKey().equals("YA")){
                    rvly_nik_00.setVisibility(View.VISIBLE);
                    lnly_yes_00.setVisibility(View.VISIBLE);
                    lnly_photo_00.setVisibility(View.VISIBLE);
                    lnly_reason_00.setVisibility(View.GONE);
                }
                else {
                    rvly_nik_00.setVisibility(View.GONE);
                    lnly_yes_00.setVisibility(View.GONE);
                    lnly_photo_00.setVisibility(View.GONE);
                    lnly_reason_00.setVisibility(View.VISIBLE);
                }
                lnly_action_00.setVisibility(View.VISIBLE);
            });
        });

        slvw_reff_00.setSelectedListener(data -> {
            ArrayList<FindHolder> holders = new ArrayList<>();
            holders.add(new FindHolder("koordinator","Koordinator"));
            holders.add(new FindHolder("diri sendiri","Diri Sendiri"));
            FindWindow findWindow = new FindWindow(mActivity);
            findWindow.setHeaderTitle(slvw_reff_00.getHint());
            findWindow.show(holders);
            findWindow.setOnSelectedListener(data1 -> slvw_reff_00.setValue(new SelectHolder(data1.getKey(),data1.getValue())));
        });

        slvw_reason_00.setSelectedListener(data -> {

            FindWindow findWindow = new FindWindow(mActivity);
            findWindow.setHeaderTitle(slvw_reason_00.getHint());
            findWindow.show(reasonCancel);
            findWindow.setOnSelectedListener(data1 -> slvw_reason_00.setValue(new SelectHolder(data1.getKey(),data1.getValue())));
        });


        slvw_home_00.setSelectedListener(data -> {
            FindWindow findWindow = new FindWindow(mActivity);
            findWindow.setHeaderTitle(slvw_home_00.getHint());
            findWindow.show(getQuestions("Status Tempat Tinggal"));
            findWindow.setOnSelectedListener(data1 -> {
                slvw_home_00.setValue(new SelectHolder(data1.getKey(),data1.getValue()));
                checkPoint(data1.getKey());
            });
        });

        slvw_job_00.setSelectedListener(data -> {

            FindWindow findWindow = new FindWindow(mActivity);
            findWindow.setHeaderTitle(slvw_job_00.getHint());
            findWindow.show(getQuestions("Pekerjaan"));
            findWindow.setOnSelectedListener(data1 -> {
                slvw_job_00.setValue(new SelectHolder(data1.getKey(),data1.getValue()));
                checkPoint(data1.getKey());
            });
        });


        slvw_arthome_00.setSelectedListener(data -> {
            FindWindow findWindow = new FindWindow(mActivity);
            findWindow.setHeaderTitle(slvw_arthome_00.getHint());
            findWindow.show(getQuestions("Kelengkapan Peralatan Rumah Tangga"));
            findWindow.setOnSelectedListener(data1 -> {
                slvw_arthome_00.setValue(new SelectHolder(data1.getKey(),data1.getValue()));
                checkPoint(data1.getKey());
            });
        });
        slvw_honesty_00.setSelectedListener(data -> {
            FindWindow findWindow = new FindWindow(mActivity);
            findWindow.setHeaderTitle(slvw_honesty_00.getHint());
            findWindow.show(getQuestions("Kejujuran"));
            findWindow.setOnSelectedListener(data1 -> {
                slvw_honesty_00.setValue(new SelectHolder(data1.getKey(),data1.getValue()));
                checkPoint(data1.getKey());
            });
        });
        slvw_responsibility_00.setSelectedListener(data -> {
            FindWindow findWindow = new FindWindow(mActivity);
            findWindow.setHeaderTitle(slvw_responsibility_00.getHint());
            findWindow.show(getQuestions("Tanggung Jawab"));
            findWindow.setOnSelectedListener(data1 -> {
                slvw_responsibility_00.setValue(new SelectHolder(data1.getKey(),data1.getValue()));
                checkPoint(data1.getKey());
            });
        });
        slvw_people_00.setSelectedListener(data -> {
            FindWindow findWindow = new FindWindow(mActivity);
            findWindow.setHeaderTitle(slvw_people_00.getHint());
            findWindow.show(getQuestions("Penilaian Masyarakat"));
            findWindow.setOnSelectedListener(data1 -> {
                slvw_people_00.setValue(new SelectHolder(data1.getKey(),data1.getValue()));
                checkPoint(data1.getKey());
            });
        });
        slvw_point_00.setSelectedListener(data -> {
            ArrayList<FindHolder> holders = new ArrayList<>();
            holders.add(new FindHolder("DITERIMA","Diterima"));
            holders.add(new FindHolder("DITOLAK","Ditolak"));
            FindWindow findWindow = new FindWindow(mActivity);
            findWindow.setHeaderTitle(slvw_point_00.getHint());
            findWindow.show(holders);
            findWindow.setOnSelectedListener(data1 -> slvw_point_00.setValue(new SelectHolder(data1.getKey(),data1.getValue())));
        });

        imvw_add_00.setOnClickListener(v -> {
            Intent intent = new Intent("ADD");
            intent.putExtra("ID", salesID);
            intent.putExtra("CONSUMER", consumerID);
            intent.putExtra("CONSUMER_NAME", edtx_name_00.getText().toString());
            intent.putExtra("CONSUMER_ADDRESS", edtx_address_00.getText().toString());
            intent.setClass(mActivity, AddGoodsActivity.class);
            startActivityForResult(intent, RQE_GOODS);
        });
        mGoodsAdapter.setOnSelectedListener((data, action) -> {
            if (action.equals("Delete")){
                delete(data.tmpId);
            }
            else if (action.equals("Edit")){
                Intent intent = new Intent(mActivity, EditGoodsActivity.class);
                intent.putExtra("ID", salesID);
                intent.putExtra("SALES_DETAIL_ID", data.tmpId);
                startActivityForResult(intent ,RQE_GOODS);
            }
        });

        bbtn_cancel_00.setOnClickListener(v -> onBackPressed());
        bbtn_save_00.setOnClickListener(v -> audit());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_EXISTING_NIK && resultCode == -1){
            try {
                assert data != null;
                JSONObject obj = new JSONObject(Objects.requireNonNull(data.getStringExtra("DATA")));
                edtx_name_00.setText(obj.getString("consumen_name"));
                edtx_phone_00.setText(obj.getString("consumen_phone"));
                edtx_address_00.setText(obj.isNull("consumen_address")?"":obj.getString("consumen_address"));
                mNik        = obj.getString("consumen_nik");
                lnly_body_00.setVisibility(View.VISIBLE);
                lnly_body_00.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_up_in));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == TAKE_PHOTO_COORDINATOR && resultCode == -1){
            Utility.showToastSuccess(mActivity,"Photo saved to draf");
            Glide.with(mActivity).load(FileProcessing.openImage(mActivity,photo_path,"coordinator.jpg")).into(imvw_coordinator_00);
            imvw_coordinator_00.setTag(1);
            ImageResizer resizer = new ImageResizer(mActivity);
            resizer.compress(photo_path+"coordinator.jpg",photo_path,"coordinator_compress.jpg");
        }
        else if (requestCode == RQE_GOODS && resultCode == RESULT_OK){
            refresh();
        }
    }

    private ArrayList<FindHolder> getQuestions(String question){
        ArrayList<FindHolder> holders = new ArrayList<>();
        QuestionsDB db = new QuestionsDB();
        for (QuestionsDB qustionDB: db.getQuestion(mActivity,question)){
            holders.add(new FindHolder(qustionDB.id, qustionDB.answer));
        }
        return holders;
    }

    @SuppressLint("SetTextI18n")
    private void checkPoint(String id){
        QuestionsDB questionsDB = new QuestionsDB();
        questionsDB.getData(mActivity, id);
        mPoint.put(questionsDB.question,questionsDB);
        int point = 0;
        for (Map.Entry me : mPoint.entrySet()) {
            point += Objects.requireNonNull(mPoint.get(me.getKey())).scoring;
        }
        txviw_point_00.setText(""+point);
        if (point >= 25){
            txviw_point_00.setTextColor(Color.parseColor("#028410"));
            txviw_status_00.setTextColor(Color.parseColor("#028410"));
            lnly_status_00.setVisibility(View.VISIBLE);
            lnly_note_00.setVisibility(View.GONE);
            txviw_status_00.setText("LOLOS SURVEY");
        }
        else if (point < 17){
            txviw_point_00.setTextColor(Color.RED);
            lnly_note_00.setVisibility(View.GONE);
            slvw_point_00.setValue(new SelectHolder("PILIH","Pilih"));
            lnly_status_00.setVisibility(View.VISIBLE);
            txviw_status_00.setTextColor(Color.RED);
            txviw_status_00.setText("DITOLAK");
            slvw_point_00.setReadOnly(true);
        }
        else {
            slvw_point_00.setReadOnly(false);
            txviw_point_00.setTextColor(Color.RED);
            lnly_status_00.setVisibility(View.GONE);
            lnly_note_00.setVisibility(View.VISIBLE);
        }
    }

    private void audit(){
        JSONArray surveyDtl = new JSONArray();
        String nik          = edtx_nik_00.getText().toString();
        String name         = edtx_name_00.getText().toString();
        String phone        = edtx_phone_00.getText().toString();
        String address      = edtx_address_00.getText().toString();
        String accepted     = "BATAL";
        String acceptedNote = "-";
        String cost         = "0";
        String reason ;
        String reference    = "-";
        String point        = "0";
        if (name.isEmpty()){
            Utility.showToastError(mActivity,"Nama harus diisi!");
            return;
        }
        if (address.isEmpty() || address.equals("-")){
            Utility.showToastError(mActivity,"Alamat harus diisi!");
            return;
        }
        if (phone.isEmpty()){
            Utility.showToastError(mActivity,"No. Telepon harus diisi!");
            return;
        }
        OperatorPrifix prifix = new OperatorPrifix();
        prifix.getInfo(phone);
        if (!prifix.isValidated()){
            Utility.showToastError(mActivity,"No. Telepon tidak valid!");
            return;
        }
        if (FileProcessing.openImage(mActivity,photo_path+"coordinator_compress.jpg") == null && lnly_photo_00.getVisibility() == View.VISIBLE
                && imvw_coordinator_00.getTag().toString().equals("0")){
            Utility.showToastError(mActivity,"Silahkan masukan foto KTP/SIM/KK!");
            return;
        }

        if (rvly_nik_00.getVisibility() == View.VISIBLE){
            if (nik.isEmpty()){
                Utility.showToastError(mActivity,"No. KTP/NIK harus diisi!");
                return;
            }
            mNik = nik;
            KtpValidator ktpValidator = new KtpValidator();
            if (!ktpValidator.valid(mNik)){
                Utility.showToastError(mActivity,"No. KTP/NIK tidak vaid!");
                return;
            }
        }

        if (lnly_yes_00.getVisibility() == View.VISIBLE){
            if (slvw_job_00.getKey().isEmpty()){
                Utility.showToastError(mActivity,"Silahkan pilih pekerjaan!");
                return;
            }
            if (slvw_responsibility_00.getKey().isEmpty()){
                Utility.showToastError(mActivity,"Silahkan pilih Tanggung jawab!");
                return;
            }
            if (slvw_arthome_00.getKey().isEmpty()){
                Utility.showToastError(mActivity,"Silahkan pilih Kelengkapan peralatan RT!");
                return;
            }
            if (slvw_honesty_00.getKey().isEmpty()){
                Utility.showToastError(mActivity,"Silahkan pilih Kejujuran!");
                return;
            }
            if (slvw_people_00.getKey().isEmpty()){
                Utility.showToastError(mActivity,"Silahkan pilih Penilaian Masyarakat");
                return;
            }

            for (Map.Entry me : mPoint.entrySet()) {
                JSONObject data = new JSONObject();
                try {
                    QuestionsDB question = (QuestionsDB) me.getValue();
                    data.put("question",question.question);
                    data.put("answer",question.answer);
                    data.put("scoring",question.scoring);
                    surveyDtl.put(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            point = txviw_point_00.getText().toString();
            if (Integer.parseInt(point) >= 25){
                accepted = "DITERIMA";
            }
        }

        if (lnly_note_00.getVisibility() == View.VISIBLE){
            accepted = slvw_point_00.getKey();
            acceptedNote = edtx_note_00.getText().toString();
            if (accepted.isEmpty()){
                Utility.showToastError(mActivity,"Penilaian Harus diisi!");
                return;
            }
            else if (accepted.equals("PILIH")){
                Utility.showToastError(mActivity,"Penilaian Harus dipilih!");
                return;
            }
            if (acceptedNote.isEmpty()){
                Utility.showToastError(mActivity,"Catatan Penilaian Harus diisi!");
                return;
            }
        }

        if (lnly_reason_00.getVisibility() == View.VISIBLE){
            reason = slvw_reason_00.getValue();
            if (reason.isEmpty()){
                Utility.showToastError(mActivity,"Alasan harus diisi!");
                return;
            }
            acceptedNote = reason;
        }
        else {
            cost = edtx_cost_00.getText().toString();
            if (cost.isEmpty()){
                Utility.showToastError(mActivity,"Jumlah pendapatan harus diisi!");
                return;
            }
        }

        if (!slvw_reff_00.getValue().isEmpty()){
            reference = slvw_reff_00.getKey();
        }

        if (offlineMode){
            saveToDraft(cost,accepted,point,acceptedNote,name,prifix.getPhoneNumber(),reference,surveyDtl);
            return;
        }

        File sd     = FileProcessing.getMainPath(mActivity);
        FormPost post = new FormPost(mActivity,"process-survey/detail/save-survey");
        post.addParam(new ObjectApi("sales_id",getIntent().getStringExtra("ID")));
        post.addParam(new ObjectApi("selling_confirmation",slvw_allowbuy_00.getKey()));
        post.addParam(new ObjectApi("spending",cost));
        post.addParam(new ObjectApi("survey_result",accepted));
        post.addParam(new ObjectApi("survey_scoring",point));
        post.addParam(new ObjectApi("survey_note",acceptedNote));
        post.addParam(new ObjectApi("consumen_id",consumerID));
        post.addParam(new ObjectApi("consumen_name", name));
        post.addParam(new ObjectApi("consumen_phone", prifix.getPhoneNumber()));
        post.addParam(new ObjectApi("consumen_ktp", mNik));
        post.addParam(new ObjectApi("consumen_address", edtx_address_00.getText().toString()));
        post.addParam(new ObjectApi("reference", reference));
        if (!saveSurveyID.isEmpty() && !saveSurveyID.equals("null")){
            post.addParam(new ObjectApi("action", "edit"));
            post.addParam(new ObjectApi("survey_id", saveSurveyID));
        }
        post.addParam("survey_detail",surveyDtl);
        if (lnly_photo_00.getVisibility() == View.VISIBLE){
            if (FileProcessing.openImage(mActivity,sd.getAbsolutePath()+photo_path+"coordinator_compress.jpg") != null){
                post.addImage("consumen_picture",sd.getAbsolutePath()+photo_path+"coordinator_compress.jpg");
            }
        }
        post.execute();
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                SuccessWindow successWindow = new SuccessWindow(mActivity);
                successWindow.setDescription("Data berhasil disimpan!");
                successWindow.show();
                successWindow.setOnFinishListener(() -> {
                    try {
                        if (obj.isNull("data")){
                            sendBroadcast(new Intent("FINISH_DTL_SURVEY"));
                            mActivity.finish();
                            return;
                        }
                        JSONObject data = obj.getJSONObject("data");
                        Intent intent = new Intent(mActivity, SummaryActivity.class);
                        intent.putExtra("SURVEY_ID", data.getString("survey_id"));
                        saveSurveyID = data.getString("survey_id");
                        startActivity(intent);
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

    private void saveToDraft(String cost, String accepted, String point, String acceptedNote, String name, String phone, String reference, JSONArray surveyDtl){
        File sd     = FileProcessing.getMainPath(mActivity);
        FormPost post = new FormPost(mActivity,"process-survey/detail/save-survey");
        post.addParam(new ObjectApi("sales_id",getIntent().getStringExtra("ID")));
        post.addParam(new ObjectApi("selling_confirmation",slvw_allowbuy_00.getKey()));
        post.addParam(new ObjectApi("spending",cost));
        post.addParam(new ObjectApi("survey_result",accepted));
        post.addParam(new ObjectApi("survey_scoring",point));
        post.addParam(new ObjectApi("survey_note",acceptedNote));
        post.addParam(new ObjectApi("consumen_id",consumerID));
        post.addParam(new ObjectApi("consumen_name", name));
        post.addParam(new ObjectApi("consumen_phone", phone));
        post.addParam(new ObjectApi("consumen_ktp", mNik));
        post.addParam(new ObjectApi("consumen_address", edtx_address_00.getText().toString()));
        post.addParam(new ObjectApi("reference", reference));
        post.addParam("survey_detail",surveyDtl);
        if (lnly_photo_00.getVisibility() == View.VISIBLE){
            String id = System.currentTimeMillis()+".jpg";
            FileProcessing fileProcessing = new FileProcessing();
            if (FileProcessing.openImage(mActivity,photo_path+"coordinator_compress.jpg") != null){
                fileProcessing.saveToTmp(mActivity,FileProcessing.openImage(mActivity,photo_path+"coordinator_compress.jpg"),photo_path+"/draft/",id);
                post.addImage("consumen_picture",sd.getAbsolutePath()+photo_path+"draft/"+id);
                FileProcessing.deleteImage(sd.getAbsolutePath()+photo_path+"coordinator_compress.jpg");
            }

        }

        String reff = "consumen_id="+consumerID+"&sales_id="+salesID;
        SurveyDeatailDB db = new SurveyDeatailDB();
        db.id = db.getNextID(mActivity) + 1;
        db.data = post.getData().toString();
        db.images = post.getImages().toString();
        db.reffId = reff;
        db.insert(mActivity);

        new Handler().postDelayed(() -> {
            Utility.showToastSuccess(mActivity,"Data disimpan ke draft");
            sendBroadcast(new Intent("FINISH_DTL_SURVEY"));
            mActivity.finish();
        },1000);


    }

    private void openCamera(String name, int reqID){
        String[] PERMISSIONS = {Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!hasPermissions(mActivity, PERMISSIONS)){
            ActivityCompat.requestPermissions(Objects.requireNonNull(mActivity), PERMISSIONS, 101);
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

    private void refresh(){
        String param = "?id="+surveyID+"&consumen_id="+consumerID+"&sales_id="+salesID;
        if (offlineMode){
            TempDB tempDB = new TempDB();
            tempDB.getData(mActivity,"detail"+param);
            Log.d(TAG,"DATA TEMP : "+ tempDB.data);
            try {
                JSONObject obj = new JSONObject(tempDB.data);
                buildData(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        PostManager post = new PostManager(mActivity,"process-survey/detail"+param);
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                buildData(obj);
            }
        });
    }

    private void buildData(JSONObject obj){
        QuestionsDB db = new QuestionsDB();
        db.clearData(mActivity);
        goodsHolders.clear();
        try {
            JSONObject data = obj.getJSONObject("data");
            JSONArray orders = data.getJSONArray("orders");
            JSONObject consumen = data.getJSONObject("consumen");
            JSONObject question = data.getJSONObject("question");
            saveQuestion(question);
            putCancelNote(data.getJSONArray("cancel_note"));
            saveSurveyID = data.getString("survey_id");

            edtx_name_00.setText(consumen.getString("consumen_name"));
            edtx_address_00.setText(consumen.isNull("consumen_address")?"":consumen.getString("consumen_address"));

            if (!consumen.getString("consumen_nik").equals("null") && !consumen.isNull("consumen_nik")){
                edtx_nik_00.setText(consumen.getString("consumen_nik"));
            }
            edtx_phone_00.setText(consumen.getString("consumen_phone"));
            if (consumen.has("consumen_picture") && !consumen.isNull("consumen_picture") && !consumen.getString("consumen_picture").equals("null")){
                imvw_coordinator_00.setTag(1);
                Glide.with(mActivity).load(Config.IMAGE_PATH_PROMOTOR+consumen.getString("consumen_picture")).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imvw_coordinator_00.setTag(1);
                        txvw_foto_00.setText("Foto Tersedia");
                        imvw_icon_01.setImageResource(R.drawable.ic_check);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imvw_coordinator_00.setTag(1);
                        return false;
                    }
                }).into(imvw_coordinator_00);
            }
            int allQty = 0;
            for (int i=0; i<orders.length(); i++){
                JSONObject order = orders.getJSONObject(i);
                GoodsHolder holder = new GoodsHolder();
                holder.tmpId    = order.getString("sales_detail_id");
                holder.name     = order.getString("product_name");
                holder.qty     = order.getInt("qty");
                holder.price     = order.getInt("selling_price");
                goodsHolders.add(holder);
                allQty = allQty + holder.qty;
            }
            if (allQty >= 2){
                imvw_add_00.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mGoodsAdapter.notifyDataSetChanged();
    }

    private void saveQuestion(JSONObject question) throws JSONException {
        ArrayList<QuestionsDB> questionsDBS = new ArrayList<>();
        questionsDBS.addAll(buildQuestion(question.getJSONArray("kejujuran")));
        questionsDBS.addAll(buildQuestion(question.getJSONArray("tanggung_jawab")));
        questionsDBS.addAll(buildQuestion(question.getJSONArray("tempat_tinggal")));
        questionsDBS.addAll(buildQuestion(question.getJSONArray("pekerjaan")));
        questionsDBS.addAll(buildQuestion(question.getJSONArray("kelengkapan_rt")));
        questionsDBS.addAll(buildQuestion(question.getJSONArray("penilaian_masyarakan")));
        QuestionsDB db = new QuestionsDB();
        db.insertBulk(mActivity, questionsDBS);

    }

    private void putCancelNote(JSONArray ja) throws JSONException {
        reasonCancel.clear();
        for (int i=0; i<ja.length(); i++){
            JSONObject obj = ja.getJSONObject(i);
            reasonCancel.add(new FindHolder(obj.getString("status_id"), obj.getString("status_name")));
        }
    }

    private ArrayList<QuestionsDB> buildQuestion(JSONArray data){
        ArrayList<QuestionsDB> questionsDBS = new ArrayList<>();
        try {
            for (int i=0; i<data.length(); i++){
                JSONObject jo = data.getJSONObject(i);
                QuestionsDB db = new QuestionsDB();
                db.id = jo.getString("id");
                db.group = jo.getString("group");
                db.question = jo.getString("question");
                db.answer = jo.getString("answer");
                db.scoring = jo.getInt("scoring");
                questionsDBS.add(db);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questionsDBS;
    }

    private void checkNik(){
        if (offlineMode){
            Utility.showToastError(mActivity,"Anda dalam mode offline!");
            return;
        }
        if (edtx_nik_00.getText().toString().isEmpty()){
            Utility.showToastError(mActivity,"NIK/No.KTP harus diisi!");
            return;
        }
        KtpValidator ktpValidator = new KtpValidator();
        if (!ktpValidator.valid(edtx_nik_00.getText().toString().trim())){
            Utility.showToastError(mActivity,"NIK/No.KTP tidak valid!");
            return;
        }

        PostManager post = new PostManager(mActivity,"process-survey/check-nik/"+edtx_nik_00.getText().toString()+"");
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
            else {
                WarningWindow warningWindow = new WarningWindow(mActivity);
                warningWindow.show("Nik Tidak Tersedia", "Nik yang anda masukan tidak tersedia, apakah anda akan melanjutkan survey?");
                warningWindow.setOnSelectedListener(status -> {
                    if (status == 1){
                        mActivity.finish();
                    }
                    else {
//                        edtx_name_00.setText(null);
//                        edtx_phone_00.setText(null);
//                        edtx_address_00.setText(null);
                        lnly_body_00.setVisibility(View.VISIBLE);
                        lnly_body_00.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.push_up_in));
                    }
                });
            }
        });
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

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        mActivity.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter("FINISH_DTL_SURVEY"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("FINISH_DTL_SURVEY")){
                mActivity.finish();
            }
        }
    };
}
