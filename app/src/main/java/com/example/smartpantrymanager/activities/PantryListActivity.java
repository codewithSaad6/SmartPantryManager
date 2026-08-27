package com.example.smartpantrymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.adapters.PantryAdapter;
import com.example.smartpantrymanager.db.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

public class PantryListActivity extends AppCompatActivity implements PantryAdapter.OnPantryItemClickListener {

    private DatabaseHelper dbHelper;
    private PantryAdapter adapter;
    private RecyclerView rvPantry;
    private LinearLayout layoutEmpty;
    private TextView tvCount;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        dbHelper = new DatabaseHelper(this);

        rvPantry = findViewById(R.id.rv_pantry_items);
        layoutEmpty = findViewById(R.id.layout_empty_pantry);
        tvCount = findViewById(R.id.tv_pantry_count);
        etSearch = findViewById(R.id.et_search_pantry);

        rvPantry.setLayoutManager(new LinearLayoutManager(this));

        ExtendedFloatingActionButton fabAdd = findViewById(R.id.fab_add_ingredient);
        fabAdd.setOnClickListener(v -> openAddIngredientScreen(-1));

        setupSearch();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantryItems();
        updateBottomNavSelection();
    }

    private void loadPantryItems() {
        List<PantryItem> items = dbHelper.getAllPantryItems();
        if (adapter == null) {
            adapter = new PantryAdapter(this, items, this);
            rvPantry.setAdapter(adapter);
        } else {
            adapter.updateList(items);
        }

        updateUI(items.size());
    }

    private void updateUI(int itemCount) {
        tvCount.setText(getString(R.string.pantry_total_count, itemCount));
        if (itemCount == 0) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvPantry.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvPantry.setVisibility(View.VISIBLE);
        }
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    List<PantryItem> filtered = dbHelper.searchPantryItems(s.toString().trim());
                    if (adapter != null) {
                        adapter.updateList(filtered);
                    }
                    updateUI(filtered.size());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void openAddIngredientScreen(long itemId) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        if (itemId != -1) {
            intent.putExtra("EXTRA_ITEM_ID", itemId);
        }
        startActivity(intent);
    }

    @Override
    public void onEditClick(PantryItem item) {
        if (item != null) {
            openAddIngredientScreen(item.getId());
        }
    }

    @Override
    public void onDeleteClick(PantryItem item) {
        if (item == null) return;
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient?")
                .setMessage("Are you sure you want to delete \"" + item.getName() + "\" from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deletePantryItem(item.getId());
                    Toast.makeText(this, "Deleted " + item.getName(), Toast.LENGTH_SHORT).show();
                    loadPantryItems();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_pantry);

        bottomNav.setOnItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.nav_pantry) {
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
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void updateBottomNavSelection() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_pantry);
    }
}