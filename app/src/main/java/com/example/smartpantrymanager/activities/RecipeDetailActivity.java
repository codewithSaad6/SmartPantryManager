package com.example.smartpantrymanager.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.adapters.RecipeIngredientAdapter;
import com.example.smartpantrymanager.db.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvTitle, tvCategory, tvPrepTime, tvCookTime, tvInstructions;
    private RecyclerView rvIngredients;
    private MaterialButton btnCook;

    private long recipeId = -1;
    private Recipe currentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        dbHelper = new DatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_recipe_detail);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.tv_detail_title);
        tvCategory = findViewById(R.id.tv_detail_category);
        tvPrepTime = findViewById(R.id.tv_detail_prep_time);
        tvCookTime = findViewById(R.id.tv_detail_cook_time);
        tvInstructions = findViewById(R.id.tv_detail_instructions);
        rvIngredients = findViewById(R.id.rv_recipe_ingredients);
        btnCook = findViewById(R.id.btn_cook_recipe);

        rvIngredients.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent().hasExtra("EXTRA_RECIPE_ID")) {
            recipeId = getIntent().getLongExtra("EXTRA_RECIPE_ID", -1);
        }

        if (recipeId != -1) {
            loadRecipeData();
        } else {
            finish();
        }

        btnCook.setOnClickListener(v -> cookRecipe());
    }

    private void loadRecipeData() {
        currentRecipe = dbHelper.getRecipeById(recipeId);
        if (currentRecipe == null) {
            finish();
            return;
        }

        tvTitle.setText(currentRecipe.getTitle());
        tvCategory.setText(currentRecipe.getCategory());
        tvPrepTime.setText(getString(R.string.label_prep_time, currentRecipe.getPrepTimeMinutes()));
        tvCookTime.setText(getString(R.string.label_cook_time, currentRecipe.getCookTimeMinutes()));
        tvInstructions.setText(currentRecipe.getInstructions());

        List<PantryItem> pantryList = dbHelper.getAllPantryItems();
        RecipeIngredientAdapter adapter = new RecipeIngredientAdapter(this, currentRecipe.getIngredients(), pantryList);
        rvIngredients.setAdapter(adapter);
    }

    private void cookRecipe() {
        if (currentRecipe != null) {
            dbHelper.deductIngredientsForRecipe(currentRecipe);
            Toast.makeText(this, R.string.msg_cooked_success, Toast.LENGTH_LONG).show();
            loadRecipeData(); // Refresh UI
        }
    }
}