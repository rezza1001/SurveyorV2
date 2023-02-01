package com.wadaro.surveyor.ui.survey;

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


public class GoodsDtlAdapter extends RecyclerView.Adapter<GoodsDtlAdapter.AdapterView> {
    private static final String TAG = "SurveyAdapter";

    private ArrayList<GoodsHolder> mList;
    private Context mContext;

    public GoodsDtlAdapter(Context context, ArrayList<GoodsHolder> data){
        mList = data;
        mContext = context;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_adapter_goodsdtl, parent, false);
        return new AdapterView(itemView);
    }

    public GoodsHolder getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final GoodsHolder data = mList.get(position);
        holder.txvw_goods_00.setText(data.name);
        holder.txvw_qty_00.setText(data.qty+"");
        long price = data.price * data.qty;
        holder.txvw_rice_00.setText(MyCurrency.toCurrnecy(price));


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class AdapterView extends RecyclerView.ViewHolder{
        private TextView txvw_goods_00,txvw_qty_00,txvw_rice_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_qty_00       = itemView.findViewById(R.id.txvw_qtygd_00);
            txvw_goods_00  = itemView.findViewById(R.id.txvw_goods_00);
            txvw_rice_00   = itemView.findViewById(R.id.txvw_rice_00);;
        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(GoodsHolder data, String action);
    }
}
