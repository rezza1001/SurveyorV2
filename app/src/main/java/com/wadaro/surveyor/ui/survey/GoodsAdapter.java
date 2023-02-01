package com.wadaro.surveyor.ui.survey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;

import java.util.ArrayList;


public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.AdapterView> {
    private static final String TAG = "SurveyAdapter";

    private ArrayList<GoodsHolder> mList;
    private Context mContext;

    public GoodsAdapter(Context context, ArrayList<GoodsHolder> data){
        mList = data;
        mContext = context;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_adapter_goods, parent, false);
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

        holder.imvw_delete_00.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(data,"Delete");
            }
        });
        holder.txvw_goods_00.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(data,"Edit");
            }
        });
        holder.txvw_qty_00.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(data,"Edit");
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class AdapterView extends RecyclerView.ViewHolder{
        private TextView txvw_goods_00,txvw_qty_00;
        private ImageView imvw_delete_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_qty_00       = itemView.findViewById(R.id.txvw_qtygd_00);
            txvw_goods_00  = itemView.findViewById(R.id.txvw_goods_00);
            imvw_delete_00   = itemView.findViewById(R.id.imvw_delete_00);;
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
