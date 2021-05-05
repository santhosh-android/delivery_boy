package com.app.dosmiosdelivery.Adapters;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.dosmiosdelivery.R;
import com.app.dosmiosdelivery.Model.NewOrdersRecyclerModel;


import java.util.List;

public class NewOrderRecyclerAdapter extends RecyclerView.Adapter<NewOrderRecyclerAdapter.ViewHolder> {
    private Context mContext;
    private List<NewOrdersRecyclerModel> newOrdersRecyclerModelList;

    public NewOrderRecyclerAdapter(Context mContext, List<NewOrdersRecyclerModel> newOrdersRecyclerModelList) {
        this.mContext = mContext;
        this.newOrdersRecyclerModelList = newOrdersRecyclerModelList;
    }

    public interface OrderDetailsInterface {
        void onOrderdetailsListener(int position);
    }

    private OrderDetailsInterface orderDetailsInterface;

    public void setOrderDetailsInterface(OrderDetailsInterface orderDetailsInterface) {
        this.orderDetailsInterface = orderDetailsInterface;
    }

    @NonNull
    @Override
    public NewOrderRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_new_order_recycler_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewOrderRecyclerAdapter.ViewHolder holder, int position) {
        NewOrdersRecyclerModel modelList = newOrdersRecyclerModelList.get(position);

        holder.order_id.setText(modelList.getOrderId());
        holder.tv_name.setText(modelList.getCustomerName());
        holder.tv_number.setText(modelList.getCustomerMobile());
        holder.tv_address.setText(modelList.getDeliveryAddress());
        holder.tv_date.setText(modelList.getDateTime());
        holder.tv_payMode.setText(modelList.getPayMode());
        holder.payment_status.setText(modelList.getPaymentstatus());
    }

    @Override
    public int getItemCount() {
        return newOrdersRecyclerModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView order_id, tv_name,
                tv_number, tv_address, tv_date,tv_payMode, payment_status;
        private CardView cv_newOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            order_id = itemView.findViewById(R.id.order_id);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_number = itemView.findViewById(R.id.tv_number);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_date = itemView.findViewById(R.id.tv_date);
            payment_status = itemView.findViewById(R.id.payment_status);
            tv_payMode = itemView.findViewById(R.id.tv_payMode);
            cv_newOrder = itemView.findViewById(R.id.cv_newOrder);

            cv_newOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (orderDetailsInterface != null) {
                        orderDetailsInterface.onOrderdetailsListener(getAdapterPosition());
                    }
                }
            });
        }
    }
}
