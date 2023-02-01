package com.wadaro.surveyor.ui.absent;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.Config;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.ObjectApi;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.database.table.UserDB;
import com.wadaro.surveyor.module.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AbsentActivity extends MyActivity {

    private MaterialRippleLayout mrly_absent_00;
    private TextView txvw_absent_00,txvw_name_00,txvw_company_00,txvw_staus_00,txvw_date_00,txvw_branch_00;
    private CircleImageView imvw_profile_00;
    private EditText edtx_note_00;

    @Override
    protected int setLayout() {
        return R.layout.absent_activity_main;
    }

    @Override
    protected void initLayout() {
        RelativeLayout rvly_body_00 = findViewById(R.id.rvly_body_00);
        rvly_body_00.setBackground(Utility.getRectBackground("ffffff",Utility.dpToPx(mActivity, 5)));
        mrly_absent_00 = findViewById(R.id.mrly_absent_00);
        mrly_absent_00.setBackground(Utility.getRectBackground("219A0B",Utility.dpToPx(mActivity, 5)));

        txvw_absent_00  = findViewById(R.id.txvw_absent_00);
        txvw_name_00    = findViewById(R.id.txvw_name_00);
        txvw_company_00 = findViewById(R.id.txvw_company_00);
        imvw_profile_00 = findViewById(R.id.imvw_profile_00);
        txvw_staus_00   = findViewById(R.id.txvw_staus_00);
        txvw_date_00    = findViewById(R.id.txvw_date_00);
        txvw_branch_00    = findViewById(R.id.txvw_branch_00);
        edtx_note_00    = findViewById(R.id.edtx_note_00);

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        txvw_absent_00.setText("CHECK IN");
        mUser = new UserDB();
        mUser.getData(mActivity);
        txvw_name_00.setText(mUser.name);
        txvw_company_00.setText(mUser.company_name);
        txvw_staus_00.setText(mUser.group);
        txvw_branch_00.setText(mUser.branch_name);
        DateFormat format = new SimpleDateFormat("EEEE, dd MMM yyyy", new Locale("id"));
        txvw_date_00.setText(format.format(new Date()));

        Glide.with(mActivity).load(Config.IMAGE_PATH+mUser.photo).into(imvw_profile_00);
        checkStatus();

        new Handler().postDelayed(() -> {
            if (offlineMode){
                mrly_absent_00.setVisibility(View.INVISIBLE);
                Utility.showToastError(mActivity,"Anda dalam mode offline");
            }
        },500);

    }

    @Override
    protected void initListener() {
        findViewById(R.id.mrly_back_00).setOnClickListener(v -> onBackPressed());
        mrly_absent_00.setOnClickListener(v -> {
            String status = txvw_absent_00.getText().toString();
            if (status.equals("CHECK IN")){
                absent("check_in",edtx_note_00.getText().toString(),"");
            }
            else {
                absent("check_out",edtx_note_00.getText().toString(),txvw_absent_00.getTag().toString());
            }
        });
    }

    private void absent(String status, String note, String id){
        PostManager post = new PostManager(mActivity, "employee/attendance");
        post.addParam(new ObjectApi("attendance_type","NORMAL"));
        post.addParam(new ObjectApi("attendance_note",note));
        post.addParam(new ObjectApi("attendance_status",status));
        if (!id.equals("0")){
            post.addParam(new ObjectApi("attendance_id",id));
        }
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                Utility.showToastSuccess(mActivity, message);
                edtx_note_00.setText("");
                checkStatus();
            }
            else {
                Utility.showToastError(mActivity, message);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void checkStatus(){
        PostManager post = new PostManager(mActivity,"employee/attendance/check");
        post.execute("GET");
        post.setOnReceiveListener((obj, code, message) -> {
            mrly_absent_00.setBackground(Utility.getRectBackground("219A0B",Utility.dpToPx(mActivity, 5)));
            txvw_absent_00.setText("CHECK IN");
            txvw_absent_00.setTag(0);
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    txvw_absent_00.setTag(data.getString("attendance_id"));
                    if (data.getString("attendance_status").equals("check_in")){
                        mrly_absent_00.setBackground(Utility.getRectBackground("EC3131",Utility.dpToPx(mActivity, 5)));
                        txvw_absent_00.setText("CHECK OUT");
                    }
                    else if (data.getString("attendance_status").equals("check_out")){
                        mrly_absent_00.setBackground(Utility.getRectBackground("219A0B",Utility.dpToPx(mActivity, 5)));
                        txvw_absent_00.setText("CHECK IN");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
