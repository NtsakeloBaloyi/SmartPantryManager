package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantryListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPantry;

    private DatabaseHelper databaseHelper;

    private PantryAdapter pantryAdapter;

    private List<PantryItem> pantryItems;

    private Button btnAddIngredient;
    private Button btnSuggestedRecipes;
    private Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_pantry_list
        );

        recyclerViewPantry = findViewById(
                R.id.recyclerViewPantry
        );

        btnAddIngredient = findViewById(
                R.id.btnAddIngredient
        );

        btnSuggestedRecipes = findViewById(
                R.id.btnSuggestedRecipes
        );

        btnSettings = findViewById(
                R.id.btnSettings
        );

        databaseHelper = new DatabaseHelper(this);

        pantryItems = new ArrayList<>();

        recyclerViewPantry.setLayoutManager(
                new LinearLayoutManager(this)
        );

        pantryAdapter = new PantryAdapter(
                pantryItems
        );

        recyclerViewPantry.setAdapter(
                pantryAdapter
        );


        // Add Ingredient button
        btnAddIngredient.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PantryListActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });


        // Suggested Recipes button
        btnSuggestedRecipes.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PantryListActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });


        // Settings button
        btnSettings.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PantryListActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });


        loadPantryItems();
    }


    @Override
    protected void onResume() {

        super.onResume();

        loadPantryItems();
    }


    private void loadPantryItems() {

        pantryItems.clear();

        Cursor cursor =
                databaseHelper.getAllPantryItems();

        if (cursor != null) {

            while (cursor.moveToNext()) {

                int id =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "id"
                                )
                        );

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "name"
                                )
                        );

                double quantity =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "quantity"
                                )
                        );

                String unit =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "unit"
                                )
                        );

                String expiryDate =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "expiry_date"
                                )
                        );

                PantryItem item =
                        new PantryItem(
                                id,
                                name,
                                quantity,
                                unit,
                                expiryDate
                        );

                pantryItems.add(item);
            }

            cursor.close();
        }

        pantryAdapter.notifyDataSetChanged();
    }
}