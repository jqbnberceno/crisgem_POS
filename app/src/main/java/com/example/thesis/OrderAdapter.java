package com.example.thesis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Order> list;
    private OnDeleteClickListener onDeleteClickListener;
    private OnConfirmClickListener onConfirmClickListener;

    public OrderAdapter(Context context, ArrayList<Order> list) {
        this.context = context;
        this.list = list;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(String key);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public interface OnConfirmClickListener {
        void onConfirmClick(String key, Order order);
    }

    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.onConfirmClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.order, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = list.get(position);
        holder.customerName.setText(order.getCustomerName());
        holder.subtotal.setText(String.valueOf(order.getOrderTotal()));
        holder.pickupTime.setText(order.getTime());

        // Set click listener for delete button
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(order.getKey());
                }
            }
        });

        // Set click listener for confirm button
        holder.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirmClickListener != null) {
                    onConfirmClickListener.onConfirmClick(order.getKey(), order);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView customerName, subtotal, pickupTime;
        Button deleteButton, confirmButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            customerName = itemView.findViewById(R.id.customerName);
            subtotal = itemView.findViewById(R.id.subtotal);
            pickupTime = itemView.findViewById(R.id.pickupTime);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            confirmButton = itemView.findViewById(R.id.confirmButton);
        }
    }
}
