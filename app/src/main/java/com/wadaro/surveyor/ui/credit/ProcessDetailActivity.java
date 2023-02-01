package com.wadaro.surveyor.ui.credit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.ObjectApi;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.component.InputBasicView;
import com.wadaro.surveyor.module.FailedWindow;
import com.wadaro.surveyor.module.Utility;
import com.wadaro.surveyor.ui.assignment.KeyValView;
import com.wadaro.surveyor.util.MyCurrency;
import com.wadaro.surveyor.util.OperatorPrifix;
import com.wadaro.surveyor.util.KtpValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ProcessDetailActivity extends MyActivity {

    private LinearLayout lnly_body_00;
    private InputBasicView input_name_00,input_phone_00,input_identity_00,input_address_00,input_note_00;
    private DateFormat format1 = new SimpleDateFormat("dd MMMM yyyy", new Locale("id"));
    private DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", new Locale("id"));
    String billID, consumentID, productID;

    @Override
    protected int setLayout() {
        return R.layout.credit_activity_process;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Over Kredit");
        LinearLayout lnly_header_00 = findViewById(R.id.lnly_header_00);
        lnly_header_00.setBackgroundColor(Color.WHITE);
        lnly_body_00 = findViewById(R.id.lnly_body_00);

        input_name_00 = findViewById(R.id.input_name_00);
        input_name_00.setLabel("Nama");
        input_name_00.setMandatory();

        input_phone_00 = findViewById(R.id.input_phone_00);
        input_phone_00.setLabel("No. Telepon");
        input_phone_00.setInputType(InputType.TYPE_CLASS_NUMBER, InputType.TYPE_CLASS_PHONE);
        input_phone_00.setMandatory();

        input_identity_00 = findViewById(R.id.input_identity_00);
        input_identity_00.setLabel("No. Identitas");
        input_identity_00.setInputType(InputType.TYPE_CLASS_NUMBER, InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input_identity_00.setMandatory();

        input_address_00 = findViewById(R.id.input_address_00);
        input_address_00.setLabel("Alamat");
        input_address_00.setInputType(InputType.TYPE_CLASS_TEXT, InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input_address_00.setMandatory();

        input_note_00 = findViewById(R.id.input_note_00);
        input_note_00.setLabel("Catatan Tanggal Tagih");
    }

    @Override
    protected void initData() {
        lnly_body_00.removeAllViews();
        try {

            JSONObject obj = new JSONObject(Objects.requireNonNull(getIntent().getExtras().getString("data")));
            JSONObject data = obj.getJSONObject("data");
            Log.d(TAG,"data "+data);
            JSONObject details = data.getJSONObject("details");
            create("No. TTB",data.getString("sales_receive_id"));
            create("No. Kwitansi",data.getString("billing_id"));
            create("Koordinator",data.getString("coordinator_name"));
            create("Alamat",data.getString("coordinator_address"));
            create("No. KTP",data.getString("coordinator_ktp"));
            create("No. HP",data.getString("coordinator_phone"));
            Date deliveryDate = format2.parse(details.getString("delivery_date"));
            Date dueDate = format2.parse(data.getString("due_date"));
            create("Tanggal Kirim",format1.format(deliveryDate));
            create("Tanggal Jatuh Tempo",format1.format(dueDate));
            create("Nama Konsumen",details.getString("consumen_name"));
            create("Nama Barang",details.getString("product_name"));
            create("Angsuran", MyCurrency.toCurrnecy("Rp",details.getLong("installment")));
            create("Angsuran Ke",details.getString("installment_period"));

            billID      = data.getString("billing_id");
            consumentID = details.getString("consumen_id");
            productID   = details.getString("product_id");
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.bbtn_cancel_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.bbtn_process_00).setOnClickListener(v -> save());
    }

    private void create(String key, String value){
        KeyValView kv_namedb = new KeyValView(mActivity,null);
        kv_namedb.setKey(key);
        kv_namedb.setValue(value);
        lnly_body_00.addView(kv_namedb);
    }

    private void save(){
        if (!input_name_00.isValid()){
            Utility.showToastError(mActivity,"Nama harus di isi");
            return;
        }
        if (!input_phone_00.isValid()){
            Utility.showToastError(mActivity,"Nomor telepon harus di isi");
            return;
        }
        OperatorPrifix prifix = new OperatorPrifix();
        prifix.getInfo(input_phone_00.getValue());
        if (!prifix.isValidated()){
            Utility.showToastError(mActivity,"Nomor telepon tidak valid");
            return;
        }
        KtpValidator ktpValidator = new KtpValidator();
        if (!ktpValidator.valid(input_identity_00.getValue())){
            Utility.showToastError(mActivity,"Nomor KTP tidak valid");
            return;
        }
        if (!input_address_00.isValid()){
            Utility.showToastError(mActivity,"Alamat harus di isi");
            return;
        }

        PostManager post = new PostManager(mActivity,"over-credit/detail/save");
        post.addParam(new ObjectApi("billing_id",billID));
        post.addParam(new ObjectApi("product_id",productID));
        post.addParam(new ObjectApi("consumen_id",consumentID));
        post.addParam(new ObjectApi("consumen_name",input_name_00.getValue()));
        post.addParam(new ObjectApi("consumen_phone",prifix.getPhoneNumber()));
        post.addParam(new ObjectApi("consumen_nik",input_identity_00.getValue()));
        post.addParam(new ObjectApi("consumen_address",input_address_00.getValue()));
        post.addParam(new ObjectApi("note",input_note_00.getValue()));
        post.execute("POST");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                sendBroadcast(new Intent("FINISH"));
                new Handler().postDelayed(() -> mActivity.finish(),300);
            }
            else {
                FailedWindow failedWindow = new FailedWindow(mActivity);
                failedWindow.setDescription(message);
                failedWindow.show();
            }
        });
    }
}
