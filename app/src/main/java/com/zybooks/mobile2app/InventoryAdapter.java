package com.zybooks.mobile2app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/*
 * Adapters provide a binding from an app-specific data set to views that are displayed within a RecyclerView.
 */
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private ArrayList<SparePart> items;
    private final OnItemClickListener listener;

    // Click interface so MainActivity can react to taps on item
    public interface OnItemClickListener {
        void onItemClick(SparePart item);
        void onItemLongClick(SparePart item);
    }

    public InventoryAdapter(ArrayList<SparePart> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }


    // ViewHolder: holds row views
    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvOEMNumber, tvName, tvPrice, tvDescription, tvQuantity;

        public InventoryViewHolder(View itemView) {
            super(itemView);
            tvOEMNumber = itemView.findViewById(R.id.tv_oem_number);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvPrice = itemView.findViewById(R.id.tv_item_price);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
            tvQuantity = itemView.findViewById(R.id.tv_item_quantity);

        }
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InventoryViewHolder holder, int position) {
        SparePart item = items.get(position);

        holder.tvOEMNumber.setText(item.oemNumber);
        holder.tvName.setText(item.name);
        holder.tvPrice.setText(String.valueOf(item.price));
        holder.tvDescription.setText(item.description);
        holder.tvQuantity.setText(String.valueOf(item.quantity));


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onItemLongClick(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Update list after DB changes
    public void updateItems(ArrayList<SparePart> newItems) {
        this.items = newItems;
        notifyDataSetChanged(); //FIXME:: add more specific notifyDataSet for each CRUD
    }
}
