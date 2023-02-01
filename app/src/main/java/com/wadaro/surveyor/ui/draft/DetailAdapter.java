package com.wadaro.surveyor.ui.draft;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;

import java.util.ArrayList;


public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.AdapterView> {
    private static final String TAG = "FIndAdapter";

    private ArrayList<Bundle> mList;
    private Context mContext;

    public DetailAdapter(Context context, ArrayList<Bundle> data){
        mList = data;
        mContext = context;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.draft_adapter_detail, parent, false);
        return new AdapterView(itemView);
    }

    public Bundle getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final Bundle data = mList.get(position);
        holder.txvw_name.setText(data.getString("consumen_name"));
        holder.txvw_phone_00.setText(data.getString("consumen_phone"));
        holder.txvw_status.setText(data.getString("selling_confirmation"));
        holder.txvw_note.setText(data.getString("survey_note"));
        holder.txvw_value.setText(data.getString("survey_result"));
        holder.txvw_score.setText(data.getString("survey_scoring"));

        if (data.getString("selling_confirmation").equals("YA")){
            holder.lnly_value.setVisibility(View.VISIBLE);
            holder.txvw_note.setVisibility(View.GONE);
        }
        else {
            holder.lnly_value.setVisibility(View.GONE);
            holder.txvw_note.setVisibility(View.VISIBLE);
        }

        holder.card_main.setOnClickListener(v -> {
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
        TextView txvw_name, txvw_phone_00, txvw_status,txvw_note,txvw_value,txvw_score;
        LinearLayout lnly_value;
        CardView card_main;

        AdapterView(View itemView) {
            super(itemView);
            txvw_name       = itemView.findViewById(R.id.txvw_name);
            txvw_phone_00   = itemView.findViewById(R.id.txvw_phone_00);
            txvw_status   = itemView.findViewById(R.id.txvw_status);
            txvw_note   = itemView.findViewById(R.id.txvw_note);
            lnly_value   = itemView.findViewById(R.id.lnly_value);
            txvw_value   = itemView.findViewById(R.id.txvw_value);
            txvw_score   = itemView.findViewById(R.id.txvw_score);
            card_main   = itemView.findViewById(R.id.card_main);

        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(Bundle data);
    }
}
