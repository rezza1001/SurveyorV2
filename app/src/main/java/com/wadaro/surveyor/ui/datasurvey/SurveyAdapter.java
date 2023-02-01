package com.wadaro.surveyor.ui.datasurvey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;

import java.util.ArrayList;


public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.AdapterView> {
    private static final String TAG = "SurveyAdapter";

    private ArrayList<SurveyHolder> mList;
    private Context mContext;

    public SurveyAdapter(Context context, ArrayList<SurveyHolder> data){
        mList = data;
        mContext = context;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.datasurvey_adapter_survey, parent, false);
        return new AdapterView(itemView);
    }

    public SurveyHolder getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final SurveyHolder data = mList.get(position);
        holder.txvw_so_00.setText(data.so);
        holder.txvw_date_00.setText(data.demoDate);
        holder.txvw_date_11.setText(data.deliveryDate);
        holder.txvw_coordinator_00.setText(data.coordinator);
        holder.txvw_jp_00.setText(data.jp);
        holder.txvw_status_00.setText(data.status);
        holder.lnly_root_00.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(data);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class AdapterView extends RecyclerView.ViewHolder{
        private final TextView txvw_so_00, txvw_date_00,txvw_coordinator_00, txvw_jp_00,txvw_status_00,txvw_date_11;
        private final LinearLayout lnly_root_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_so_00       = itemView.findViewById(R.id.txvw_so_00);
            txvw_date_00  = itemView.findViewById(R.id.txvw_date_00);
            txvw_coordinator_00   = itemView.findViewById(R.id.txvw_coordinator_00);
            txvw_jp_00   = itemView.findViewById(R.id.txvw_jp_00);
            txvw_status_00   = itemView.findViewById(R.id.txvw_status_00);
            lnly_root_00   = itemView.findViewById(R.id.lnly_root_00);
            txvw_date_11   = itemView.findViewById(R.id.txvw_date_11);

        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(SurveyHolder data);
    }
}
