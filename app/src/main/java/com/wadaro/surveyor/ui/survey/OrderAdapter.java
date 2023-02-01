package com.wadaro.surveyor.ui.survey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wadaro.surveyor.R;
import com.wadaro.surveyor.module.Utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.AdapterView> {
    private static final String TAG = "SurveyAdapter";

    private ArrayList<OrderHolder> mList;
    private Context mContext;
    private boolean editable = true;

    public OrderAdapter(Context context, ArrayList<OrderHolder> data){
        mList = data;
        mContext = context;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_adapter_order, parent, false);
        return new AdapterView(itemView);
    }

    public void disableEdit(){
        editable = false;
    }
    public OrderHolder getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final OrderHolder data = mList.get(position);
        holder.txvw_customer_00.setText(data.customerName);
        holder.txvw_goods_00.setText(data.goodsName);
        holder.txvw_qty_00.setText(data.qty);
        if (data.survey.equals("1")){
            holder.txvw_survey_00.setBackground(Utility.getRectBackground("1bdb8e", Utility.dpToPx(mContext, 5)));
            holder.imvw_delete_00.setVisibility(View.INVISIBLE);
            holder.txvw_survey_00.setText("Survey");
            if (!editable){
                holder.txvw_survey_00.setVisibility(View.INVISIBLE);
            }
        }
        else {
            holder.txvw_survey_00.setText("Survey");
            holder.txvw_survey_00.setBackground(Utility.getRectBackground("0183d9", Utility.dpToPx(mContext, 5)));
        }
        holder.txvw_survey_00.setOnClickListener(v -> {
            if (onSelectedListener != null && !data.survey.equals("1")){
                onSelectedListener.onSelected(data,"Survey");
            }
        });
        holder.imvw_delete_00.setOnClickListener(v -> {
            if (onSelectedListener != null){
                onSelectedListener.onSelected(data,"Delete");
            }
        });
        holder.txvw_customer_00.setOnClickListener(v -> {
            if (onSelectedListener != null && !data.survey.equals("1")){
                onSelectedListener.onSelected(data,"Edit");
            }
        });
        holder.txvw_goods_00.setOnClickListener(v -> {
            if (onSelectedListener != null && !data.survey.equals("1")){
                onSelectedListener.onSelected(data,"Edit");
            }
        });
        holder.txvw_qty_00.setOnClickListener(v -> {
            if (onSelectedListener != null && !data.survey.equals("1")){
                onSelectedListener.onSelected(data,"Edit");
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class AdapterView extends RecyclerView.ViewHolder{
        private TextView txvw_survey_00, txvw_customer_00,txvw_goods_00,txvw_qty_00;
        private ImageView imvw_delete_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_qty_00       = itemView.findViewById(R.id.txvw_qty_00);
            txvw_goods_00  = itemView.findViewById(R.id.txvw_goods_00);
            txvw_customer_00   = itemView.findViewById(R.id.txvw_customer_00);
            txvw_survey_00   = itemView.findViewById(R.id.txvw_survey_00);
            imvw_delete_00   = itemView.findViewById(R.id.imvw_delete_00);

            txvw_survey_00.setBackground(Utility.getRectBackground("0183d9", Utility.dpToPx(mContext, 5)));
            txvw_survey_00.setText("Survey");

        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(OrderHolder data, String action);
    }
}
