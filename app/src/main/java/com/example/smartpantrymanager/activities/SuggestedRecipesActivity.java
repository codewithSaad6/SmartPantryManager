package com.example.smartpantrymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.adapters.RecipeAdapter;
import com.example.smartpantrymanager.db.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper dbHelper;
    private RecipeAdapter adapter;
    private RecyclerView rvRecipes;
    private LinearLayout layoutEmpty;
    private TextView tvHeader;
    private TabLayout tabLayout;

    private boolean isStrictTab = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dbHelper = new DatabaseHelper(this);

        rvRecipes = findViewById(R.id.rv_suggested_recipes);
        layoutEmpty = findViewById(R.id.layout_empty_suggested);
        tvHeader = findViewById(R.id.tv_suggested_header);
        tabLayout = findViewById(R.id.tab_layout_matches);

        rvRecipes.setLayoutManager(new LinearLayoutManager(this));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isStrictTab = (tab.getPosition() == 0);
                loadSuggestedRecipes();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
        updateBottomNavSelection();
    }

    private void loadSuggestedRecipes() {
        List<Recipe> recipes = dbHelper.getSuggestedRecipes(isStrictTab);
        List<PantryItem> pantryList = dbHelper.getAllPantryItems();

        if (adapter == null) {
            adapter = new RecipeAdapter(this, recipes, pantryList, this);
            rvRecipes.setAdapter(adapter);
        } else {
            adapter.updateList(recipes);
        }

        if (isStrictTab) {
            tvHeader.setText(getString(R.string.strict_match_header, recipes.size()));
        } else {
            tvHeader.setText(getString(R.string.almost_there_header, recipes.size()));
        }

        if (recipes.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvRecipes.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvRecipes.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        if (recipe != null) {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("EXTRA_RECIPE_ID", recipe.getId());
            startActivity(intent);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_suggested);

        bottomNav.setOnItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.nav_pantry) {
                startActivity(new Intent(this, PantryListActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_suggested) {
                return true;
            } else if (itemId == R.id.nav_recipes) {
                startActivity(new Intent(this, AllRecipesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void updateBottomNavSelection() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_suggested);
    }
}