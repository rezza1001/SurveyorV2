package com.wadaro.surveyor.ui.credit;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.util.MyCurrency;

import java.util.ArrayList;


public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.AdapterView> {
    private static final String TAG = "FIndAdapter";

    private ArrayList<Bundle> mList;

    public CreditAdapter(ArrayList<Bundle> data){
        mList = data;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_adapter_main, parent, false);
        return new AdapterView(itemView);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final Bundle data = mList.get(position);
        holder.txvw_nottb_00.setText(data.getString("nottb"));
        holder.txvw_coordinator_00.setText(data.getString("coordinator"));
        holder.txvw_isntall_00.setText(data.getInt("installment")+"");
        holder.txvw_paydate_00.setText(data.getString("payment_date"));
        holder.txvw_total_00.setText(MyCurrency.toCurrnecy("Rp.",data.getLong("total")));
        if (position % 2 == 0){
            holder.lnly_root_00.setBackgroundColor(Color.parseColor("#cfd8dc"));
        }
        else {
            holder.lnly_root_00.setBackgroundColor(Color.parseColor("#f5f5f5"));
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

   static class AdapterView extends RecyclerView.ViewHolder{
        private final TextView txvw_nottb_00, txvw_isntall_00,txvw_coordinator_00, txvw_total_00,txvw_paydate_00;
        private final LinearLayout lnly_root_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_nottb_00 = itemView.findViewById(R.id.txvw_nottb_00);
            txvw_isntall_00 = itemView.findViewById(R.id.txvw_isntall_00);
            txvw_coordinator_00   = itemView.findViewById(R.id.txvw_coordinator_00);
            txvw_total_00 = itemView.findViewById(R.id.txvw_total_00);
            lnly_root_00   = itemView.findViewById(R.id.lnly_root_00);
            txvw_paydate_00   = itemView.findViewById(R.id.txvw_paydate_00);

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
