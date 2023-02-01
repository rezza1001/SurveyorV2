package com.wadaro.surveyor.ui.rekap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;

import java.util.ArrayList;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.AdapterView> {
    private static final String TAG = "ProductAdapter";

    private final ArrayList<Bundle> mList;

    public ProductAdapter(ArrayList<Bundle> data){
        mList = data;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recap_adapter_product, parent, false);
        return new AdapterView(itemView);
    }

    public Bundle getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final Bundle data = mList.get(position);
        holder.txvw_no_00.setText(data.getInt("no")+"");
        holder.txvw_product_00.setText(data.getString("product"));
        holder.txvw_unit_00.setText(data.getString("unit"));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class AdapterView extends RecyclerView.ViewHolder{
        private final TextView txvw_no_00, txvw_product_00, txvw_unit_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_no_00 = itemView.findViewById(R.id.txvw_no_00);
            txvw_product_00 = itemView.findViewById(R.id.txvw_product_00);
            txvw_unit_00 = itemView.findViewById(R.id.txvw_unit_00);

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
