package com.wadaro.surveyor.ui.survey;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.widget.ImageView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.ObjectApi;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.component.InputBasicView;
import com.wadaro.surveyor.module.Utility;
import com.wadaro.surveyor.util.OperatorPrifix;
import com.wadaro.surveyor.util.KtpValidator;

import org.json.JSONException;
import org.json.JSONObject;

public class AddJpActivity extends MyActivity {

    InputBasicView input_name_00, input_identity_00,input_phone_00,input_address_00;
    @Override
    protected int setLayout() {
        return R.layout.survey_activity_addjp;
    }

    @Override
    protected void initLayout() {
        ImageView imvw_back_00 = findViewById(R.id.imvw_back_00);
        imvw_back_00.setColorFilter(Color.WHITE);

        input_name_00 = findViewById(R.id.input_name_00);
        input_identity_00 = findViewById(R.id.input_identity_00);
        input_phone_00 = findViewById(R.id.input_phone_00);
        input_address_00 = findViewById(R.id.input_address_00);

        input_name_00.setLabel("Nama");
        input_identity_00.setLabel("Nomor KTP");
        input_phone_00.setLabel("Nomor Telepon");
        input_address_00.setLabel("Alamat");

        input_identity_00.setMandatory();
        input_name_00.setMandatory();
        input_phone_00.setMandatory();
        input_phone_00.setInputType(InputType.TYPE_CLASS_PHONE, InputType.TYPE_CLASS_PHONE);
        input_identity_00.setInputType(InputType.TYPE_CLASS_NUMBER, InputType.TYPE_CLASS_NUMBER);
        input_address_00.setInputType(InputType.TYPE_CLASS_TEXT, InputType.TYPE_TEXT_FLAG_MULTI_LINE);

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

        findViewById(R.id.bbtn_save_00).setOnClickListener(v -> {
            save();
        });
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
    }



    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void save(){
        if (!input_name_00.isValid()){
            Utility.showToastError(mActivity, "Nama harus di isi!");
            return;
        }
        KtpValidator ktpValidator = new KtpValidator();
        if (!ktpValidator.valid(input_identity_00.getValue())){
            Utility.showToastError(mActivity,"Nomor KTP tidak valid");
            return;
        }

        if (!input_phone_00.isValid()){
            Utility.showToastError(mActivity, "Nomor Telepon harus di isi!");
            return;
        }

        if (!input_address_00.isValid()){
            Utility.showToastError(mActivity, "Alamat harus di isi!");
            return;
        }
        OperatorPrifix prifix = new OperatorPrifix();
        prifix.getInfo(input_phone_00.getValue());
        if (!prifix.isValidated()){
            Utility.showToastError(mActivity, "Nomor Telepon tidak valid!");
            return;
        }
        String name = input_name_00.getValue();
        String identiy = input_identity_00.getValue();
        String phone = prifix.getPhoneNumber();

        PostManager post = new PostManager(mActivity,"consumer-lead");
        post.addParam(new ObjectApi("booking_id",getIntent().getStringExtra("BOOKING_ID")));
        post.addParam(new ObjectApi("name",name));
        post.addParam(new ObjectApi("phone",phone));
        post.addParam(new ObjectApi("ktp",identiy));
        post.addParam(new ObjectApi("address",input_address_00.getValue()));
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                try {
                    JSONObject data = obj.getJSONObject("data");
                    Utility.showToastSuccess(mActivity, message);
                    Intent intent = getIntent();
                    intent.putExtra("KEY",data.getString("id"));
                    intent.putExtra("VALUE",data.getString("name"));
                    setResult(RESULT_OK, intent);
                    mActivity.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Utility.showToastError(mActivity, message);
            }
        });
    }

}
