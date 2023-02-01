package com.wadaro.surveyor.ui.profile;

import android.annotation.SuppressLint;
import android.widget.EditText;
import android.widget.TextView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.api.ErrorCode;
import com.wadaro.surveyor.api.ObjectApi;
import com.wadaro.surveyor.api.PostManager;
import com.wadaro.surveyor.base.MyActivity;
import com.wadaro.surveyor.module.FailedWindow;
import com.wadaro.surveyor.module.SuccessWindow;
import com.wadaro.surveyor.module.Utility;

public class ChangePasswodActivity extends MyActivity {

    private EditText edtx_old_00,edtx_new_00,edtx_confrm_00;
    @Override
    protected int setLayout() {
        return R.layout.profile_activity_changepwd;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initLayout() {
        TextView txvw_title_00 = findViewById(R.id.txvw_titile_00);
        txvw_title_00.setText("Ganti Password");

        edtx_old_00 = findViewById(R.id.edtx_old_00);
        edtx_new_00 = findViewById(R.id.edtx_new_00);
        edtx_confrm_00 = findViewById(R.id.edtx_confrm_00);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        findViewById(R.id.imvw_back_00).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.bbtn_change_00).setOnClickListener(v -> save());
    }

    private void save(){
        String old_pwd = edtx_old_00.getText().toString().trim();
        String new_pwd = edtx_new_00.getText().toString().trim();
        String conf_pwd = edtx_confrm_00.getText().toString().trim();

        if (old_pwd.isEmpty()){
            Utility.showToastError(mActivity,"Silahkan isi password lama!");
            return;
        }
        if (new_pwd.isEmpty()){
            Utility.showToastError(mActivity,"Silahkan isi password baru!");
            return;
        }
        if (new_pwd.length() < 8){
            Utility.showToastError(mActivity,"Password minimal 8 karakter!");
            return;
        }
        if (!conf_pwd.equals(new_pwd)){
            Utility.showToastError(mActivity,"Konfirmasi Password tidak benar!");
            return;
        }

        PostManager post = new PostManager(mActivity,"profile/90/change-password");
        post.addParam(new ObjectApi("curr_pass",old_pwd));
        post.addParam(new ObjectApi("new_pass",new_pwd));
        post.execute("PUT");
        post.setOnReceiveListener((obj, code, message) -> {
            if (code == ErrorCode.OK){
                SuccessWindow successWindow = new SuccessWindow(mActivity);
                successWindow.setDescription(message);
                successWindow.show();
                successWindow.setOnFinishListener(() -> {
                    mActivity.finish();
                });

            }else {
                FailedWindow failedWindow = new FailedWindow(mActivity);
                failedWindow.setDescription(message);
                failedWindow.show();
            }
        });
    }
}
