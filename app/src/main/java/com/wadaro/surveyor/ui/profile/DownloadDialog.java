package com.wadaro.surveyor.ui.profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.module.Utility;

import java.util.Objects;

public class DownloadDialog extends Dialog {

    private final TaskView task_assignment_00,task_assgmntdtl_00,task_survey_00,task_dtlsurvey_00;
    private final TextView txvw_download_00,txvw_status_00,txvw_title_00;
    private final ProgressBar prgr_status_00;
    private Button bbtn_close_00;

    private final String[] task = {"Data Assignment","Detail Assignment","Proses Survey","Detail Survey"};
    public DownloadDialog(@NonNull Context context) {
        super(context, R.style.AppTheme_transparent);
        WindowManager.LayoutParams wlmp = Objects.requireNonNull(getWindow()).getAttributes();

        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.profile_dialog_downloadinfo, null);
        setContentView(view);

        RelativeLayout rvly_body_00 = view.findViewById(R.id.rvly_body_00);
        rvly_body_00.setBackground(Utility.getRectBackground("ffffff", Utility.dpToPx(context,6)));

        task_assignment_00 = view.findViewById(R.id.task_assignment_00);
        task_assignment_00.create("1. "+ task[0]);
        task_assgmntdtl_00 = view.findViewById(R.id.task_assgmntdtl_00);
        task_assgmntdtl_00.create("2. "+ task[1]);
        task_survey_00 = view.findViewById(R.id.task_survey_00);
        task_survey_00.create("3. "+ task[2]);
        task_dtlsurvey_00 = view.findViewById(R.id.task_dtlsurvey_00);
        task_dtlsurvey_00.create("4. "+ task[3]);

        txvw_download_00 = view.findViewById(R.id.txvw_download_00);
        prgr_status_00 = view.findViewById(R.id.prgr_status_00);
        txvw_status_00 = view.findViewById(R.id.txvw_status_00);
        bbtn_close_00 = view.findViewById(R.id.bbtn_close_00);
        txvw_title_00 = view.findViewById(R.id.txvw_title_00);
        prgr_status_00.setProgress(0);

        bbtn_close_00.setOnClickListener(v -> super.dismiss());
    }

    public void setTitleCustom(String title){
        txvw_title_00.setText(title);
    }
    public void setCompleteStep(int step){
        if (step < 3){
            txvw_download_00.setText(task[step+1]);
        }
        switch (step){
            case 0: task_assignment_00.setSuccess();break;
            case 1:task_assgmntdtl_00.setSuccess(); break;
            case 2:task_survey_00.setSuccess(); break;
            case 3:
                task_dtlsurvey_00.setSuccess();
                bbtn_close_00.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setErrorStep(int step){
        switch (step){
            case 0: task_assignment_00.setFailed();break;
            case 1:task_assgmntdtl_00.setFailed(); break;
            case 2:task_survey_00.setFailed(); break;
            case 3: task_dtlsurvey_00.setFailed();break;
        }
        bbtn_close_00.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    public void updateProgress(int current, int max){
        txvw_status_00.setText("Download... "+current+"/"+max);
        double progress = ((double) current / (double) max) * 100;
        int value = (int) Math.round(progress);
        prgr_status_00.setProgress(value);

    }

    @Override
    public void dismiss() {
        bbtn_close_00.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
    }
}
