package com.example.smartpantrymanager.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.db.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextInputLayout tilName, tilQuantity;
    private TextInputEditText etName, etQuantity, etExpiryDate;
    private Spinner spinnerUnit, spinnerCategory;
    private MaterialButton btnSave, btnDelete, btnCancel;
    private MaterialToolbar toolbar;

    private long editItemId = -1;
    private final String[] units = {"pcs", "g", "kg", "ml", "l", "cups", "tbsp", "tsp", "cloves", "slices"};
    private final String[] categories = {"Produce", "Dairy", "Protein", "Grains", "Pantry", "Spices", "Bakery", "Beverages", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        dbHelper = new DatabaseHelper(this);

        toolbar = findViewById(R.id.toolbar_add_edit);
        tilName = findViewById(R.id.til_name);
        tilQuantity = findViewById(R.id.til_quantity);
        etName = findViewById(R.id.et_ingredient_name);
        etQuantity = findViewById(R.id.et_quantity);
        etExpiryDate = findViewById(R.id.et_expiry_date);
        spinnerUnit = findViewById(R.id.spinner_unit);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnSave = findViewById(R.id.btn_save_ingredient);
        btnDelete = findViewById(R.id.btn_delete_ingredient);
        btnCancel = findViewById(R.id.btn_cancel);

        setupSpinners();
        setupDatePicker();

        toolbar.setNavigationOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());

        if (getIntent().hasExtra("EXTRA_ITEM_ID")) {
            editItemId = getIntent().getLongExtra("EXTRA_ITEM_ID", -1);
        }

        if (editItemId != -1) {
            toolbar.setTitle(R.string.title_edit_ingredient);
            btnSave.setText(R.string.btn_update);
            btnDelete.setVisibility(View.VISIBLE);
            loadItemDetails(editItemId);
        } else {
            toolbar.setTitle(R.string.title_add_ingredient);
            btnSave.setText(R.string.btn_save);
            btnDelete.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> saveIngredient());
        btnDelete.setOnClickListener(v -> {
            if (editItemId != -1) {
                dbHelper.deletePantryItem(editItemId);
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupSpinners() {
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, units);
        unitAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);
        catAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);
    }

    private void setupDatePicker() {
        etExpiryDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
                String formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                etExpiryDate.setText(formatted);
            }, year, month, day);
            dialog.show();
        });
    }

    private void loadItemDetails(long id) {
        PantryItem item = dbHelper.getPantryItemById(id);
        if (item != null) {
            etName.setText(item.getName());
            etQuantity.setText(String.valueOf(item.getQuantity()));
            etExpiryDate.setText(item.getExpiryDate());

            for (int i = 0; i < units.length; i++) {
                if (units[i].equalsIgnoreCase(item.getUnit())) {
                    spinnerUnit.setSelection(i);
                    break;
                }
            }

            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equalsIgnoreCase(item.getCategory())) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        }
    }

    private void saveIngredient() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String qtyStr = etQuantity.getText() != null ? etQuantity.getText().toString().trim() : "";
        String unit = spinnerUnit.getSelectedItem() != null ? spinnerUnit.getSelectedItem().toString() : "pcs";
        String category = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "Other";
        String expiry = etExpiryDate.getText() != null ? etExpiryDate.getText().toString().trim() : "";

        boolean isValid = true;

        if (name.isEmpty()) {
            tilName.setError(getString(R.string.error_empty_name));
            isValid = false;
        } else {
            tilName.setError(null);
        }

        double qty = 0;
        try {
            qty = Double.parseDouble(qtyStr);
            if (qty <= 0) {
                tilQuantity.setError(getString(R.string.error_invalid_quantity));
                isValid = false;
            } else {
                tilQuantity.setError(null);
            }
        } catch (NumberFormatException e) {
            tilQuantity.setError(getString(R.string.error_invalid_quantity));
            isValid = false;
        }

        if (!isValid) return;

        if (editItemId != -1) {
            PantryItem item = new PantryItem(editItemId, name, qty, unit, expiry, category);
            dbHelper.updatePantryItem(item);
            Toast.makeText(this, "Ingredient updated!", Toast.LENGTH_SHORT).show();
        } else {
            PantryItem item = new PantryItem(name, qty, unit, expiry, category);
            dbHelper.insertPantryItem(item);
            Toast.makeText(this, "Ingredient added!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}