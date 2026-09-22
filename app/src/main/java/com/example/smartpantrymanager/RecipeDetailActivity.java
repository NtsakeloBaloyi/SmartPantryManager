package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvRecipeDetailTitle;
    private TextView tvRecipeIngredients;
    private TextView tvRecipeInstructions;

    private Button btnBackToSuggestedRecipes;

    private DatabaseHelper databaseHelper;

    private int recipeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_detail);

        tvRecipeDetailTitle = findViewById(
                R.id.tvRecipeDetailTitle
        );

        tvRecipeIngredients = findViewById(
                R.id.tvRecipeIngredients
        );

        tvRecipeInstructions = findViewById(
                R.id.tvRecipeInstructions
        );

        btnBackToSuggestedRecipes = findViewById(
                R.id.btnBackToSuggestedRecipes
        );

        databaseHelper = new DatabaseHelper(this);

        recipeId = getIntent().getIntExtra(
                "recipe_id",
                -1
        );

        if (recipeId == -1) {

            Toast.makeText(
                    this,
                    "Unable to load recipe",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        // Back to Suggested Recipes
        btnBackToSuggestedRecipes.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RecipeDetailActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);

            finish();
        });

        loadRecipeDetails();
    }

    private void loadRecipeDetails() {

        Cursor recipeCursor =
                databaseHelper.getRecipeById(recipeId);

        if (recipeCursor != null
                && recipeCursor.moveToFirst()) {

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

            tvRecipeDetailTitle.setText(
                    recipeName
            );

            tvRecipeInstructions.setText(
                    instructions
            );
        }

        if (recipeCursor != null) {
            recipeCursor.close();
        }

        Cursor ingredientCursor =
                databaseHelper.getRecipeIngredients(recipeId);

        StringBuilder ingredientsText =
                new StringBuilder();

        if (ingredientCursor != null) {

            while (ingredientCursor.moveToNext()) {

                String ingredientName =
                        ingredientCursor.getString(
                                ingredientCursor.getColumnIndexOrThrow(
                                        "ingredient_name"
                                )
                        );

                double quantity =
                        ingredientCursor.getDouble(
                                ingredientCursor.getColumnIndexOrThrow(
                                        "required_quantity"
                                )
                        );

                String unit =
                        ingredientCursor.getString(
                                ingredientCursor.getColumnIndexOrThrow(
                                        "unit"
                                )
                        );

                ingredientsText
                        .append("• ")
                        .append(ingredientName)
                        .append(" - ")
                        .append(quantity)
                        .append(" ")
                        .append(unit)
                        .append("\n");
            }

            ingredientCursor.close();
        }

        tvRecipeIngredients.setText(
                ingredientsText.toString()
        );
    }
}