package com.wadaro.surveyor.ui.datasurvey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.util.MyCurrency;

import java.util.ArrayList;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.AdapterView> {
    private static final String TAG = "FIndAdapter";

    private ArrayList<OrderHolder> mList;
    private Context mContext;

    public OrderAdapter(Context context, ArrayList<OrderHolder> data){
        mList = data;
        mContext = context;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.datasurvey_adapter_order, parent, false);
        return new AdapterView(itemView);
    }

    public OrderHolder getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final OrderHolder data = mList.get(position);
        holder.txvw_name_00.setText(data.name);
        holder.txvw_goods_00.setText(data.goods);
        holder.txvw_qty_00.setText(data.qty);
        long price = Long.parseLong(data.price) * Integer.parseInt(data.qty);
        holder.txvw_price_00.setText(MyCurrency.toCurrnecy(price));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class AdapterView extends RecyclerView.ViewHolder{
        private TextView txvw_name_00, txvw_goods_00,txvw_qty_00, txvw_price_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_name_00       = itemView.findViewById(R.id.txvw_name_00);
            txvw_goods_00  = itemView.findViewById(R.id.txvw_goods_00);
            txvw_qty_00   = itemView.findViewById(R.id.txvw_qty_00);
            txvw_price_00   = itemView.findViewById(R.id.txvw_price_00);

        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(OrderHolder data);
    }
}
