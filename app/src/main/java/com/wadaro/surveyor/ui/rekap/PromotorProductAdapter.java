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
import com.wadaro.surveyor.util.MyCurrency;

import java.util.ArrayList;


public class PromotorProductAdapter extends RecyclerView.Adapter<PromotorProductAdapter.AdapterView> {
    private static final String TAG = "ProductAdapter";

    private final ArrayList<Bundle> mList;

    public PromotorProductAdapter(ArrayList<Bundle> data){
        mList = data;
    }

    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rekap_adapter_productpromotor, parent, false);
        return new AdapterView(itemView);
    }

    public Bundle getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final Bundle data = mList.get(position);
        holder.txvw_qty.setText(data.getString("qty"));
        holder.txvw_product.setText(data.getString("product"));
        holder.txvw_price.setText(MyCurrency.toCurrnecy(data.getString("price")));
        holder.txvw_total.setText(MyCurrency.toCurrnecy(data.getString("total")));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class AdapterView extends RecyclerView.ViewHolder{
        private final TextView txvw_qty, txvw_product,txvw_price,txvw_total;

        AdapterView(View itemView) {
            super(itemView);
            txvw_qty = itemView.findViewById(R.id.txvw_qty);
            txvw_product = itemView.findViewById(R.id.txvw_product);
            txvw_price = itemView.findViewById(R.id.txvw_price);
            txvw_total = itemView.findViewById(R.id.txvw_total);

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
