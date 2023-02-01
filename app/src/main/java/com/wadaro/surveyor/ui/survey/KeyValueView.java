package com.wadaro.surveyor.ui.survey;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.base.MyLayout;

public class KeyValueView extends MyLayout {

    private TextView txvw_key_00, txvw_value_00;
    public KeyValueView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.ui_view_databooking_keyvalue;
    }

    @Override
    protected void initLayout() {
        txvw_key_00 = findViewById(R.id.txvw_key_00);
        txvw_value_00 = findViewById(R.id.txvw_val_00);
    }

    @Override
    protected void initListener() {

    }

    public void setKey(String key){
        txvw_key_00.setText(key);
    }

    public void setValue(String value){
        txvw_value_00.setText(value);
    }

    public void create(String key, String value){
        txvw_key_00.setText(key);
        txvw_value_00.setText(value);
    }
}
