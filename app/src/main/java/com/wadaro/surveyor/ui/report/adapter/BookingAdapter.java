package com.wadaro.surveyor.ui.report.adapter;

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


public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.AdapterView> {
    private static final String TAG = "FIndAdapter";

    private ArrayList<BookingHolder> mList;
    private Context mContext;

    public  BookingAdapter(Context context, ArrayList<BookingHolder> data){
        mList = data;
        mContext = context;
    }
    @NonNull
    @Override
    public AdapterView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_reort_databooking_adapter, parent, false);
        return new AdapterView(itemView);
    }

    public BookingHolder getData(int position){
        return mList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterView holder, int position) {
        final BookingHolder data = mList.get(position);
        holder.txvw_demo_00.setText("Demo "+data.booking_demo);
        DateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.txvw_time_00.setText(format.format(data.booking_date));
        holder.txvw_coordinator_00.setText(data.coordinator_name);
        holder.txvw_status_00.setText(data.status);
        holder.txvw_sales_00.setText(data.sales);
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
        private TextView txvw_demo_00, txvw_time_00,txvw_coordinator_00,txvw_sales_00, txvw_status_00;
        private LinearLayout lnly_root_00;

        AdapterView(View itemView) {
            super(itemView);
            txvw_demo_00       = itemView.findViewById(R.id.txvw_demo_00);
            txvw_time_00  = itemView.findViewById(R.id.txvw_time_00);
            txvw_coordinator_00   = itemView.findViewById(R.id.txvw_coordinator_00);
            txvw_sales_00   = itemView.findViewById(R.id.txvw_sales_00);
            txvw_status_00   = itemView.findViewById(R.id.txvw_status_00);
            lnly_root_00   = itemView.findViewById(R.id.lnly_root_00);

        }
    }

    private OnSelectedListener onSelectedListener;
    public void setOnSelectedListener(OnSelectedListener onSelectedListener){
        this.onSelectedListener = onSelectedListener;
    }
    public interface OnSelectedListener{
        void onSelected(BookingHolder data);
    }
}
