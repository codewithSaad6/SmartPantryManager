package com.example.smartpantrymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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

import java.util.ArrayList;
import java.util.List;

public class AllRecipesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper dbHelper;
    private RecipeAdapter adapter;
    private RecyclerView rvRecipes;
    private EditText etSearch;
    private Spinner spinnerCategory;

    private List<Recipe> allRecipesList = new ArrayList<>();
    private final String[] categories = {"All Categories", "Breakfast", "Main Dish", "Soup", "Side Dish", "Salad", "Snack", "Dessert"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recipes);

        dbHelper = new DatabaseHelper(this);

        rvRecipes = findViewById(R.id.rv_all_recipes);
        etSearch = findViewById(R.id.et_search_recipes);
        spinnerCategory = findViewById(R.id.spinner_recipe_category);

        rvRecipes.setLayoutManager(new LinearLayoutManager(this));

        setupCategorySpinner();
        setupSearch();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllRecipes();
        updateBottomNavSelection();
    }

    private void loadAllRecipes() {
        allRecipesList = dbHelper.getAllRecipes();
        List<PantryItem> pantryList = dbHelper.getAllPantryItems();

        if (adapter == null) {
            adapter = new RecipeAdapter(this, allRecipesList, pantryList, this);
            rvRecipes.setAdapter(adapter);
        } else {
            filterRecipes();
        }
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);
        catAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterRecipes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRecipes();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterRecipes() {
        String query = etSearch.getText() != null ? etSearch.getText().toString().trim().toLowerCase() : "";
        String selectedCategory = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "All Categories";

        List<Recipe> filtered = new ArrayList<>();
        for (Recipe r : allRecipesList) {
            boolean matchesCategory = selectedCategory.equals("All Categories") || r.getCategory().equalsIgnoreCase(selectedCategory);
            boolean matchesQuery = query.isEmpty() || r.getTitle().toLowerCase().contains(query);

            if (matchesCategory && matchesQuery) {
                filtered.add(r);
            }
        }

        if (adapter != null) {
            adapter.updateList(filtered);
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
        bottomNav.setSelectedItemId(R.id.nav_recipes);

        bottomNav.setOnItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.nav_pantry) {
                startActivity(new Intent(this, PantryListActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_suggested) {
                startActivity(new Intent(this, SuggestedRecipesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_recipes) {
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
        bottomNav.setSelectedItemId(R.id.nav_recipes);
    }
}