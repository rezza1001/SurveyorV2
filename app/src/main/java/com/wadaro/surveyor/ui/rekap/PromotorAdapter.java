package com.wadaro.surveyor.ui.rekap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;

import java.util.ArrayList;


public class PromotorAdapter extends RecyclerView.Adapter<PromotorAdapter.AdapterView> {
    private static final String TAG = "ProductAdapter";

    private final ArrayList<Bundle> mList;

    public PromotorAdapter(ArrayList<Bundle> data){
        mList = data;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rekap_adapter_promotor, parent, false);
        return new AdapterView(itemView);
    }

    public Bundle getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final Bundle data = mList.get(position);
        holder.txvw_qty.setText(data.getInt("qty")+"");
        holder.txvw_promotor.setText(data.getString("promotor"));
        holder.rvly_header.setOnClickListener(v -> {
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
        private final TextView txvw_qty, txvw_promotor;
        RelativeLayout rvly_header;

        AdapterView(View itemView) {
            super(itemView);
            txvw_qty = itemView.findViewById(R.id.txvw_qty);
            txvw_promotor = itemView.findViewById(R.id.txvw_promotor);
            rvly_header = itemView.findViewById(R.id.rvly_header);

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
