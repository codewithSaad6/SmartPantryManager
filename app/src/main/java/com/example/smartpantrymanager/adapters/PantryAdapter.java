package com.example.smartpantrymanager.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.models.PantryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnPantryItemClickListener {
        void onEditClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    private final Context context;
    private List<PantryItem> items;
    private final OnPantryItemClickListener listener;

    public PantryAdapter(Context context, List<PantryItem> items, OnPantryItemClickListener listener) {
        this.context = context;
        this.items = (items != null) ? items : new ArrayList<>();
        this.listener = listener;
    }

    public void updateList(List<PantryItem> newItems) {
        this.items = (newItems != null) ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);

        holder.tvName.setText(item.getName());
        String qtyText = "Quantity: " + formatQuantity(item.getQuantity()) + " " + item.getUnit();
        holder.tvQuantityUnit.setText(qtyText);

        int status = item.getExpiryStatus();
        if (status == 2) {
            // Expired
            holder.tvExpiryStatus.setText("EXPIRED (" + item.getExpiryDate() + ")");
            holder.tvExpiryStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.badge_background_expired)));
            holder.tvExpiryStatus.setTextColor(ContextCompat.getColor(context, R.color.status_expired));
            holder.viewStatusIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.status_expired));
        } else if (status == 1) {
            // Expiring soon
            holder.tvExpiryStatus.setText("EXPIRING SOON (" + item.getExpiryDate() + ")");
            holder.tvExpiryStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.badge_background_expiring)));
            holder.tvExpiryStatus.setTextColor(ContextCompat.getColor(context, R.color.status_expiring));
            holder.viewStatusIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.status_expiring));
        } else {
            // Fresh / No expiry
            if (item.getExpiryDate() != null && !item.getExpiryDate().trim().isEmpty()) {
                holder.tvExpiryStatus.setText("Fresh (Expires " + item.getExpiryDate() + ")");
            } else {
                holder.tvExpiryStatus.setText("Fresh");
            }
            holder.tvExpiryStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.badge_background_fresh)));
            holder.tvExpiryStatus.setTextColor(ContextCompat.getColor(context, R.color.status_fresh));
            holder.viewStatusIndicator.setBackgroundColor(ContextCompat.getColor(context, R.color.status_fresh));
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatQuantity(double qty) {
        if (qty == (long) qty) {
            return String.format(Locale.getDefault(), "%d", (long) qty);
        } else {
            return String.format(Locale.getDefault(), "%.1f", qty);
        }
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        View viewStatusIndicator;
        TextView tvName, tvQuantityUnit, tvExpiryStatus;
        ImageButton btnEdit, btnDelete;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            viewStatusIndicator = itemView.findViewById(R.id.view_status_indicator);
            tvName = itemView.findViewById(R.id.tv_ingredient_name);
            tvQuantityUnit = itemView.findViewById(R.id.tv_quantity_unit);
            tvExpiryStatus = itemView.findViewById(R.id.tv_expiry_status);
            btnEdit = itemView.findViewById(R.id.btn_edit_item);
            btnDelete = itemView.findViewById(R.id.btn_delete_item);
        }
    }
}