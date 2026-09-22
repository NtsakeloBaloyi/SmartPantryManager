package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etIngredientName;
    private EditText etQuantity;
    private EditText etExpiryDate;
    private Spinner spinnerUnit;

    private TextView tvFormTitle;

    private Button btnSaveIngredient;
    private Button btnDeleteIngredient;
    private Button btnBackToPantry;

    private DatabaseHelper databaseHelper;

    private int ingredientId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_edit_ingredient);

        tvFormTitle = findViewById(R.id.tvFormTitle);

        etIngredientName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiryDate = findViewById(R.id.etExpiryDate);

        spinnerUnit = findViewById(R.id.spinnerUnit);

        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);
        btnDeleteIngredient = findViewById(R.id.btnDeleteIngredient);
        btnBackToPantry = findViewById(R.id.btnBackToPantry);

        databaseHelper = new DatabaseHelper(this);

        String[] units = {
                "pieces",
                "tablespoons",
                "cups",
                "g",
                "kg",
                "ml",
                "L",
                "packets",
                "cans",
                "bottles",
                "leaves"
        };

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUnit.setAdapter(unitAdapter);

        if (getIntent().hasExtra("ingredient_id")) {

            isEditMode = true;

            tvFormTitle.setText("Edit Ingredient");

            ingredientId = getIntent().getIntExtra(
                    "ingredient_id",
                    -1
            );

            String name = getIntent().getStringExtra(
                    "ingredient_name"
            );

            double quantity = getIntent().getDoubleExtra(
                    "ingredient_quantity",
                    0
            );

            String unit = getIntent().getStringExtra(
                    "ingredient_unit"
            );

            String expiryDate = getIntent().getStringExtra(
                    "ingredient_expiry"
            );

            etIngredientName.setText(name);

            etQuantity.setText(
                    String.valueOf(quantity)
            );

            if (expiryDate != null) {
                etExpiryDate.setText(expiryDate);
            }

            if (unit != null) {

                for (int i = 0; i < units.length; i++) {

                    if (units[i].equals(unit)) {

                        spinnerUnit.setSelection(i);

                        break;
                    }
                }
            }

            btnDeleteIngredient.setVisibility(
                    View.VISIBLE
            );

        } else {

            isEditMode = false;

            tvFormTitle.setText("Add Ingredient");

            btnDeleteIngredient.setVisibility(
                    View.GONE
            );
        }

        btnSaveIngredient.setOnClickListener(
                v -> saveIngredient()
        );

        btnDeleteIngredient.setOnClickListener(
                v -> deleteIngredient()
        );

        btnBackToPantry.setOnClickListener(v -> {

            Intent intent = new Intent(
                    AddEditIngredientActivity.this,
                    PantryListActivity.class
            );

            startActivity(intent);

            finish();
        });
    }

    private void saveIngredient() {

        String name = etIngredientName
                .getText()
                .toString()
                .trim();

        String quantityText = etQuantity
                .getText()
                .toString()
                .trim();

        String unit = spinnerUnit
                .getSelectedItem()
                .toString();

        String expiryDate = etExpiryDate
                .getText()
                .toString()
                .trim();

        if (name.isEmpty()) {

            etIngredientName.setError(
                    "Please enter an ingredient name"
            );

            etIngredientName.requestFocus();

            return;
        }

        if (quantityText.isEmpty()) {

            etQuantity.setError(
                    "Please enter a quantity"
            );

            etQuantity.requestFocus();

            return;
        }

        double quantity;

        try {

            quantity = Double.parseDouble(
                    quantityText
            );

        } catch (NumberFormatException e) {

            etQuantity.setError(
                    "Please enter a valid quantity"
            );

            etQuantity.requestFocus();

            return;
        }

        if (quantity <= 0) {

            etQuantity.setError(
                    "Quantity must be greater than zero"
            );

            etQuantity.requestFocus();

            return;
        }

        // Validate optional expiry date format
        if (!expiryDate.isEmpty()
                && !expiryDate.matches(
                "\\d{4}-\\d{2}-\\d{2}"
        )) {

            etExpiryDate.setError(
                    "Please use the format YYYY-MM-DD"
            );

            etExpiryDate.requestFocus();

            return;
        }

        if (isEditMode) {

            int result = databaseHelper.updatePantryItem(
                    ingredientId,
                    name,
                    quantity,
                    unit,
                    expiryDate
            );

            if (result > 0) {

                Toast.makeText(
                        this,
                        "Ingredient updated successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Failed to update ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else {

            long result = databaseHelper.addPantryItem(
                    name,
                    quantity,
                    unit,
                    expiryDate
            );

            if (result != -1) {

                Toast.makeText(
                        this,
                        "Ingredient saved successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Failed to save ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private void deleteIngredient() {

        if (ingredientId == -1) {

            Toast.makeText(
                    this,
                    "Unable to delete ingredient",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int result = databaseHelper.deletePantryItem(
                ingredientId
        );

        if (result > 0) {

            Toast.makeText(
                    this,
                    "Ingredient deleted successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to delete ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
