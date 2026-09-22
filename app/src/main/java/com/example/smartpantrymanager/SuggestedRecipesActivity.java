
package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRecipes;
    private TextView tvNoRecipes;
    private Button btnBackToPantry;

    private DatabaseHelper databaseHelper;
    private RecipeAdapter recipeAdapter;

    private List<Recipe> suggestedRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_suggested_recipes
        );

        recyclerViewRecipes = findViewById(
                R.id.recyclerViewRecipes
        );

        tvNoRecipes = findViewById(
                R.id.tvNoRecipes
        );

        btnBackToPantry = findViewById(
                R.id.btnBackToPantry
        );

        databaseHelper = new DatabaseHelper(this);

        suggestedRecipes = new ArrayList<>();

        recyclerViewRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recipeAdapter = new RecipeAdapter(
                suggestedRecipes
        );

        recyclerViewRecipes.setAdapter(
                recipeAdapter
        );

        // Back to Pantry button
        btnBackToPantry.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SuggestedRecipesActivity.this,
                    PantryListActivity.class
            );

            startActivity(intent);

            finish();
        });

        loadSuggestedRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadSuggestedRecipes();
        }
    }

    private void loadSuggestedRecipes() {

        suggestedRecipes.clear();

        Cursor recipeCursor =
                databaseHelper.getAllRecipes();

        if (recipeCursor != null) {

            while (recipeCursor.moveToNext()) {

                int recipeId =
                        recipeCursor.getInt(
                                recipeCursor.getColumnIndexOrThrow(
                                        "id"
                                )
                        );

                String recipeName =
                        recipeCursor.getString(
                                recipeCursor.getColumnIndexOrThrow(
                                        "name"
                                )
                        );

                String instructions =
                        recipeCursor.getString(
                                recipeCursor.getColumnIndexOrThrow(
                                        "instructions"
                                )
                        );

                if (canMakeRecipe(recipeId)) {

                    Recipe recipe = new Recipe(
                            recipeId,
                            recipeName,
                            instructions
                    );

                    suggestedRecipes.add(recipe);
                }
            }

            recipeCursor.close();
        }

        recipeAdapter.notifyDataSetChanged();

        if (suggestedRecipes.isEmpty()) {

            tvNoRecipes.setVisibility(
                    View.VISIBLE
            );

            recyclerViewRecipes.setVisibility(
                    View.GONE
            );

        } else {

            tvNoRecipes.setVisibility(
                    View.GONE
            );

            recyclerViewRecipes.setVisibility(
                    View.VISIBLE
            );
        }
    }

    private boolean canMakeRecipe(int recipeId) {

        Cursor ingredientCursor =
                databaseHelper.getRecipeIngredients(
                        recipeId
                );

        if (ingredientCursor == null) {
            return false;
        }

        boolean canMake = true;

        while (ingredientCursor.moveToNext()) {

            String requiredIngredient =
                    ingredientCursor.getString(
                            ingredientCursor.getColumnIndexOrThrow(
                                    "ingredient_name"
                            )
                    );

            double requiredQuantity =
                    ingredientCursor.getDouble(
                            ingredientCursor.getColumnIndexOrThrow(
                                    "required_quantity"
                            )
                    );

            String requiredUnit =
                    ingredientCursor.getString(
                            ingredientCursor.getColumnIndexOrThrow(
                                    "unit"
                            )
                    );

            Cursor pantryCursor =
                    databaseHelper.getAllPantryItems();

            boolean ingredientAvailable = false;

            if (pantryCursor != null) {

                while (pantryCursor.moveToNext()) {

                    String pantryName =
                            pantryCursor.getString(
                                    pantryCursor.getColumnIndexOrThrow(
                                            "name"
                                    )
                            );

                    double pantryQuantity =
                            pantryCursor.getDouble(
                                    pantryCursor.getColumnIndexOrThrow(
                                            "quantity"
                                    )
                            );

                    String pantryUnit =
                            pantryCursor.getString(
                                    pantryCursor.getColumnIndexOrThrow(
                                            "unit"
                                    )
                            );

                    if (ingredientsMatch(
                            pantryName,
                            requiredIngredient
                    )
                            && quantitiesMatch(
                            pantryQuantity,
                            pantryUnit,
                            requiredQuantity,
                            requiredUnit
                    )) {

                        ingredientAvailable = true;
                        break;
                    }
                }

                pantryCursor.close();
            }

            if (!ingredientAvailable) {

                canMake = false;
                break;
            }
        }

        ingredientCursor.close();

        return canMake;
    }

    private boolean ingredientsMatch(
            String pantryName,
            String requiredName
    ) {

        if (pantryName == null || requiredName == null) {
            return false;
        }

        String pantry =
                normalizeIngredientName(pantryName);

        String required =
                normalizeIngredientName(requiredName);

        return pantry.equals(required);
    }

    private String normalizeIngredientName(String name) {

        String normalized =
                name.trim()
                        .toLowerCase()
                        .replaceAll("\\s+", " ");

        // Common irregular plural forms
        if (normalized.endsWith("ies")
                && normalized.length() > 3) {

            normalized =
                    normalized.substring(
                            0,
                            normalized.length() - 3
                    ) + "y";

        } else if (normalized.endsWith("oes")
                && normalized.length() > 3) {

            normalized =
                    normalized.substring(
                            0,
                            normalized.length() - 2
                    );

        } else if (normalized.endsWith("s")
                && !normalized.endsWith("ss")
                && normalized.length() > 3) {

            normalized =
                    normalized.substring(
                            0,
                            normalized.length() - 1
                    );
        }

        return normalized;
    }

    private boolean quantitiesMatch(
            double pantryQuantity,
            String pantryUnit,
            double requiredQuantity,
            String requiredUnit
    ) {

        String pantryNormalized =
                normalizeUnit(pantryUnit);

        String requiredNormalized =
                normalizeUnit(requiredUnit);

        // Weight
        if (isWeightUnit(pantryNormalized)
                && isWeightUnit(requiredNormalized)) {

            double pantryGrams =
                    convertToGrams(
                            pantryQuantity,
                            pantryNormalized
                    );

            double requiredGrams =
                    convertToGrams(
                            requiredQuantity,
                            requiredNormalized
                    );

            return pantryGrams >= requiredGrams;
        }

        // Volume
        if (isVolumeUnit(pantryNormalized)
                && isVolumeUnit(requiredNormalized)) {

            double pantryMillilitres =
                    convertToMillilitres(
                            pantryQuantity,
                            pantryNormalized
                    );

            double requiredMillilitres =
                    convertToMillilitres(
                            requiredQuantity,
                            requiredNormalized
                    );

            return pantryMillilitres >= requiredMillilitres;
        }

        // Other units must match after normalisation.
        if (!pantryNormalized.equals(requiredNormalized)) {
            return false;
        }

        return pantryQuantity >= requiredQuantity;
    }

    private String normalizeUnit(String unit) {

        if (unit == null) {
            return "";
        }

        String normalized =
                unit.trim()
                        .toLowerCase();

        switch (normalized) {

            case "piece":
            case "pieces":
                return "piece";

            case "tablespoon":
            case "tablespoons":
            case "tbsp":
                return "tablespoon";

            case "cup":
            case "cups":
                return "cup";

            case "gram":
            case "grams":
            case "g":
                return "g";

            case "kilogram":
            case "kilograms":
            case "kg":
                return "kg";

            case "milliliter":
            case "milliliters":
            case "ml":
                return "ml";

            case "liter":
            case "liters":
            case "l":
                return "l";

            case "packet":
            case "packets":
                return "packet";

            case "can":
            case "cans":
                return "can";

            case "bottle":
            case "bottles":
                return "bottle";

            case "leaf":
            case "leaves":
                return "leaf";

            default:
                return normalized;
        }
    }

    private boolean isWeightUnit(String unit) {

        return unit.equals("g")
                || unit.equals("kg");
    }

    private boolean isVolumeUnit(String unit) {

        return unit.equals("ml")
                || unit.equals("l");
    }

    private double convertToGrams(
            double quantity,
            String unit
    ) {

        if (unit.equals("kg")) {
            return quantity * 1000;
        }

        return quantity;
    }

    private double convertToMillilitres(
            double quantity,
            String unit
    ) {

        if (unit.equals("l")) {
            return quantity * 1000;
        }

        return quantity;
    }
}

