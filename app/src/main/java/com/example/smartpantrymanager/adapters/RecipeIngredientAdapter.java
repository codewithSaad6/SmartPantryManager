package com.example.smartpantrymanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.RecipeIngredient;
import com.example.smartpantrymanager.utils.IngredientMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.IngredientViewHolder> {

    private final Context context;
    private final List<RecipeIngredient> ingredients;
    private final List<PantryItem> pantryList;

    public RecipeIngredientAdapter(Context context, List<RecipeIngredient> ingredients, List<PantryItem> pantryList) {
        this.context = context;
        this.ingredients = (ingredients != null) ? ingredients : new ArrayList<>();
        this.pantryList = (pantryList != null) ? pantryList : new ArrayList<>();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_recipe_ingredient, parent, false);
        return new IngredientViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        RecipeIngredient req = ingredients.get(position);

        String nameQtyText = formatQuantity(req.getQuantity()) + " " + req.getUnit() + " " + req.getIngredientName();
        holder.tvNameQty.setText(nameQtyText);

        PantryItem matchingItem = null;
        if (pantryList != null) {
            for (PantryItem pItem : pantryList) {
                if (IngredientMatcher.isIngredientSatisfied(req, pItem)) {
                    matchingItem = pItem;
                    break;
                }
            }
        }

        if (matchingItem != null) {
            holder.ivMatchIcon.setImageResource(R.drawable.ic_check);
            holder.ivMatchIcon.setColorFilter(ContextCompat.getColor(context, R.color.status_fresh));
            holder.tvPantryStatus.setText("In Pantry (" + formatQuantity(matchingItem.getQuantity()) + " " + matchingItem.getUnit() + ")");
            holder.tvPantryStatus.setTextColor(ContextCompat.getColor(context, R.color.status_fresh));
        } else {
            holder.ivMatchIcon.setImageResource(R.drawable.ic_warning);
            holder.ivMatchIcon.setColorFilter(ContextCompat.getColor(context, R.color.status_expired));
            holder.tvPantryStatus.setText("Missing");
            holder.tvPantryStatus.setTextColor(ContextCompat.getColor(context, R.color.status_expired));
        }
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    private String formatQuantity(double qty) {
        if (qty == (long) qty) {
            return String.format(Locale.getDefault(), "%d", (long) qty);
        } else {
            return String.format(Locale.getDefault(), "%.1f", qty);
        }
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMatchIcon;
        TextView tvNameQty, tvPantryStatus;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMatchIcon = itemView.findViewById(R.id.iv_match_icon);
            tvNameQty = itemView.findViewById(R.id.tv_ingredient_name_qty);
            tvPantryStatus = itemView.findViewById(R.id.tv_pantry_status);
        }
    }
}