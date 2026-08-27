package com.example.smartpantrymanager.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.utils.IngredientMatcher;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final Context context;
    private List<Recipe> recipes;
    private final List<PantryItem> pantryList;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(Context context, List<Recipe> recipes, List<PantryItem> pantryList, OnRecipeClickListener listener) {
        this.context = context;
        this.recipes = (recipes != null) ? recipes : new ArrayList<>();
        this.pantryList = (pantryList != null) ? pantryList : new ArrayList<>();
        this.listener = listener;
    }

    public void updateList(List<Recipe> newRecipes) {
        this.recipes = (newRecipes != null) ? newRecipes : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.tvTitle.setText(recipe.getTitle());
        holder.tvCategory.setText(recipe.getCategory());
        int totalTime = recipe.getPrepTimeMinutes() + recipe.getCookTimeMinutes();
        holder.tvTime.setText(totalTime + " mins");
        holder.tvIngCount.setText(recipe.getIngredients().size() + " ingredients");

        IngredientMatcher.MatchResult match = IngredientMatcher.evaluateRecipe(recipe, pantryList);
        if (match.isStrictMatch) {
            holder.tvMatchBadge.setText("100% Strict Match");
            holder.tvMatchBadge.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.badge_background_fresh)));
            holder.tvMatchBadge.setTextColor(ContextCompat.getColor(context, R.color.status_fresh));
        } else if (match.missingCount == 1) {
            holder.tvMatchBadge.setText("Missing 1 Item");
            holder.tvMatchBadge.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.badge_background_expiring)));
            holder.tvMatchBadge.setTextColor(ContextCompat.getColor(context, R.color.status_expiring));
        } else {
            holder.tvMatchBadge.setText("Missing " + match.missingCount + " Items");
            holder.tvMatchBadge.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.badge_background_expired)));
            holder.tvMatchBadge.setTextColor(ContextCompat.getColor(context, R.color.status_expired));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRecipeClick(recipe);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvTime, tvIngCount, tvMatchBadge;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_recipe_title);
            tvCategory = itemView.findViewById(R.id.tv_recipe_category);
            tvTime = itemView.findViewById(R.id.tv_recipe_time);
            tvIngCount = itemView.findViewById(R.id.tv_recipe_ing_count);
            tvMatchBadge = itemView.findViewById(R.id.tv_match_badge);
        }
    }
}