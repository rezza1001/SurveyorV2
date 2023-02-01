package com.wadaro.surveyor.ui.assignment.adapter;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AdapterView> {
    private static final String TAG = "SurveyAdapter";

    private ArrayList<AssignmentHolder> mList;
    private Context mContext;

    public AssignmentAdapter(Context context, ArrayList<AssignmentHolder> data){
        mList = data;
        mContext = context;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_assignment_adapter, parent, false);
        return new AdapterView(itemView);
    }

    public AssignmentHolder getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final AssignmentHolder data = mList.get(position);
        holder.txvw_sonumber_00.setText(data.no_so);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        holder.txvw_sodate_00.setText(format.format(data.so_date));
        holder.txvw_coordinator_00.setText(data.coordinator_name);
        if (data.send_date != null){
            holder.txvw_send_00.setText(format.format(data.send_date));
        }
        else {
            holder.txvw_send_00.setText("-");
        }
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

    class AdapterView extends RecyclerView.ViewHolder{
        private TextView txvw_sonumber_00, txvw_sodate_00,txvw_coordinator_00,txvw_send_00;
        private LinearLayout lnly_root_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_sonumber_00       = itemView.findViewById(R.id.txvw_sonumber_00);
            txvw_sodate_00  = itemView.findViewById(R.id.txvw_sodate_00);
            txvw_coordinator_00   = itemView.findViewById(R.id.txvw_coordinator_00);
            txvw_send_00   = itemView.findViewById(R.id.txvw_send_00);
            lnly_root_00   = itemView.findViewById(R.id.lnly_root_00);

        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(AssignmentHolder data);
    }
}
