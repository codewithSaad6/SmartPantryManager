package com.example.smartpantrymanager.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.db.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SwitchMaterial switchAlerts;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences("smart_pantry_prefs", MODE_PRIVATE);

        switchAlerts = findViewById(R.id.switch_expiring_alerts);
        MaterialButton btnClearPantry = findViewById(R.id.btn_clear_pantry_items);
        MaterialButton btnResetRecipes = findViewById(R.id.btn_reset_recipe_db);

        boolean alertsEnabled = prefs.getBoolean("pref_alerts_enabled", true);
        switchAlerts.setChecked(alertsEnabled);

        switchAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("pref_alerts_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "Alerts enabled" : "Alerts disabled", Toast.LENGTH_SHORT).show();
        });

        btnClearPantry.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.confirm_clear_pantry_title)
                    .setMessage(R.string.confirm_clear_pantry_msg)
                    .setPositiveButton("Clear All", (dialog, which) -> {
                        dbHelper.clearAllPantryItems();
                        Toast.makeText(this, "Pantry cleared!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show();
        });

        btnResetRecipes.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.confirm_reset_recipes_title)
                    .setMessage(R.string.confirm_reset_recipes_msg)
                    .setPositiveButton("Reset", (dialog, which) -> {
                        dbHelper.resetAndSeedRecipes();
                        Toast.makeText(this, "Recipes database reset to default collection!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show();
        });

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBottomNavSelection();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_settings);

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
                startActivity(new Intent(this, AllRecipesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_settings) {
                return true;
            }
            return false;
        });
    }

    private void updateBottomNavSelection() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_settings);
    }
}