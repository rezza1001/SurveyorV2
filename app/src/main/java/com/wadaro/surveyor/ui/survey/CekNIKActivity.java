package com.wadaro.surveyor.ui.survey;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.base.MyActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class CekNIKActivity extends MyActivity {

    @Override
    protected int setLayout() {
        return R.layout.activity_cek_nik;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_titile_00 = findViewById(R.id.txvw_titile_00);
        txvw_titile_00.setText("Cek NIK");
        KeyValueView kvl_ktp_00 = findViewById(R.id.kvl_ktp_00);
        KeyValueView kvl_name_00 = findViewById(R.id.kvl_name_00);
        KeyValueView kvl_phone_00 = findViewById(R.id.kvl_phone_00);
        KeyValueView kvl_address_00 = findViewById(R.id.kvl_address_00);

        try {
            JSONObject data = new JSONObject(Objects.requireNonNull(getIntent().getStringExtra("DATA")));
            kvl_ktp_00.create("NO.KTP/NIK", data.getString("consumen_nik"));
            kvl_name_00.create("Nama (Sesuai KTP)", data.getString("consumen_name"));
            kvl_phone_00.create("Nomor Telepon", data.getString("consumen_phone"));
            kvl_address_00.create("Alamat", data.isNull("consumen_address")? "-" : data.getString("consumen_address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        findViewById(R.id.btnSimpan).setOnClickListener(v -> {
            setResult(RESULT_OK, getIntent());
            mActivity.finish();
        });
        findViewById(R.id.btnBatal).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            mActivity.finish();
        });
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            mActivity.finish();
        });
    }
}
