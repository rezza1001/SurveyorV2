package com.wadaro.surveyor.ui.assignment;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.base.MyLayout;

public class KeyValView extends MyLayout {

    private TextView txvw_key_00,txvw_val_00;
    public KeyValView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int setlayout() {
        return R.layout.ui_view_databooking_keyvalue;
    }

    @Override
    protected void initLayout() {
        txvw_val_00 = findViewById(R.id.txvw_val_00);
        txvw_key_00 = findViewById(R.id.txvw_key_00);
    }

    @Override
    protected void initListener() {

    }

    public void setKey(String key){
        txvw_key_00.setText(key);
    }

    public void setValue(String value){
        txvw_val_00.setText(value);
    }
}
