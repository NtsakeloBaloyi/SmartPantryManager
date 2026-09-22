package com.example.smartpantrymanager;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // =========================
        // PANTRY TABLE
        // =========================

        String createPantryTable = "CREATE TABLE pantry (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "expiry_date TEXT)";

        db.execSQL(createPantryTable);


        // =========================
        // RECIPES TABLE
        // =========================

        String createRecipesTable = "CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "instructions TEXT NOT NULL)";

        db.execSQL(createRecipesTable);


        // =========================
        // RECIPE INGREDIENTS TABLE
        // =========================

        String createRecipeIngredientsTable =
                "CREATE TABLE recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "required_quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)";

        db.execSQL(createRecipeIngredientsTable);


        // Add the preloaded recipes
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        /*
         * Version 1 -> Version 2
         *
         * The pantry table already exists.
         * We only need to add the recipe tables.
         */

        if (oldVersion < 2) {

            String createRecipesTable = "CREATE TABLE IF NOT EXISTS recipes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "instructions TEXT NOT NULL)";

            db.execSQL(createRecipesTable);


            String createRecipeIngredientsTable =
                    "CREATE TABLE IF NOT EXISTS recipe_ingredients (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "recipe_id INTEGER NOT NULL, " +
                            "ingredient_name TEXT NOT NULL, " +
                            "required_quantity REAL NOT NULL, " +
                            "unit TEXT NOT NULL, " +
                            "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)";

            db.execSQL(createRecipeIngredientsTable);


            // Add the preloaded recipes
            seedRecipes(db);
        }
    }


    // =========================================================
    // PANTRY METHODS
    // =========================================================

    public long addPantryItem(
            String name,
            double quantity,
            String unit,
            String expiryDate
    ) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);

        return db.insert(
                "pantry",
                null,
                values
        );
    }


    public Cursor getAllPantryItems() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                "pantry",
                null,
                null,
                null,
                null,
                null,
                "name ASC"
        );
    }


    public int updatePantryItem(
            int id,
            String name,
            double quantity,
            String unit,
            String expiryDate
    ) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);

        return db.update(
                "pantry",
                values,
                "id = ?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }


    public int deletePantryItem(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(
                "pantry",
                "id = ?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }


    // =========================================================
    // RECIPE METHODS
    // =========================================================

    public Cursor getAllRecipes() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                "recipes",
                null,
                null,
                null,
                null,
                null,
                "name ASC"
        );
    }


    public Cursor getRecipeById(int recipeId) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                "recipes",
                null,
                "id = ?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                null
        );
    }


    public Cursor getRecipeIngredients(int recipeId) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                "recipe_ingredients",
                null,
                "recipe_id = ?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                "id ASC"
        );
    }


    // =========================================================
    // ADD RECIPE
    // =========================================================

    private long addRecipe(
            SQLiteDatabase db,
            String name,
            String instructions
    ) {

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("instructions", instructions);

        return db.insert(
                "recipes",
                null,
                values
        );
    }


    // =========================================================
    // ADD RECIPE INGREDIENT
    // =========================================================

    private void addRecipeIngredient(
            SQLiteDatabase db,
            long recipeId,
            String ingredientName,
            double quantity,
            String unit
    ) {

        ContentValues values = new ContentValues();

        values.put(
                "recipe_id",
                recipeId
        );

        values.put(
                "ingredient_name",
                ingredientName
        );

        values.put(
                "required_quantity",
                quantity
        );

        values.put(
                "unit",
                unit
        );

        db.insert(
                "recipe_ingredients",
                null,
                values
        );
    }


    // =========================================================
    // SEED 20 RECIPES
    // =========================================================

    private void seedRecipes(SQLiteDatabase db) {

        // 1. Fried Rice
        long recipeId = addRecipe(
                db,
                "Fried Rice",
                "Cook the rice. Fry the onion and vegetables, add the rice and soy sauce, then stir-fry until heated through."
        );

        addRecipeIngredient(db, recipeId, "Rice", 2, "cups");
        addRecipeIngredient(db, recipeId, "Eggs", 2, "pieces");
        addRecipeIngredient(db, recipeId, "Onion", 1, "pieces");
        addRecipeIngredient(db, recipeId, "Mixed Vegetables", 1, "cups");


        // 2. Chicken and Rice
        recipeId = addRecipe(
                db,
                "Chicken and Rice",
                "Season and fry the chicken. Cook the rice separately, then serve the chicken with the rice."
        );

        addRecipeIngredient(db, recipeId, "Chicken", 500, "g");
        addRecipeIngredient(db, recipeId, "Rice", 2, "cups");
        addRecipeIngredient(db, recipeId, "Onion", 1, "pieces");


        // 3. Spaghetti Bolognese
        recipeId = addRecipe(
                db,
                "Spaghetti Bolognese",
                "Cook the spaghetti. Fry the onion and mince, add tomato sauce and simmer. Serve with the spaghetti."
        );

        addRecipeIngredient(db, recipeId, "Spaghetti", 250, "g");
        addRecipeIngredient(db, recipeId, "Mince", 500, "g");
        addRecipeIngredient(db, recipeId, "Tomato Sauce", 1, "cans");
        addRecipeIngredient(db, recipeId, "Onion", 1, "pieces");


        // 4. Chicken Pasta
        recipeId = addRecipe(
                db,
                "Chicken Pasta",
                "Cook the pasta. Fry the chicken and onion, add the sauce and combine with the cooked pasta."
        );

        addRecipeIngredient(db, recipeId, "Chicken", 300, "g");
        addRecipeIngredient(db, recipeId, "Pasta", 250, "g");
        addRecipeIngredient(db, recipeId, "Tomato Sauce", 1, "cans");
        addRecipeIngredient(db, recipeId, "Onion", 1, "pieces");


        // 5. Omelette
        recipeId = addRecipe(
                db,
                "Omelette",
                "Beat the eggs. Fry the onion and peppers, pour in the eggs and cook until set."
        );

        addRecipeIngredient(db, recipeId, "Eggs", 3, "pieces");
        addRecipeIngredient(db, recipeId, "Onion", 1, "pieces");
        addRecipeIngredient(db, recipeId, "Peppers", 1, "pieces");


        // 6. Tuna Sandwich
        recipeId = addRecipe(
                db,
                "Tuna Sandwich",
                "Mix tuna with mayonnaise. Place the mixture between slices of bread and serve."
        );

        addRecipeIngredient(db, recipeId, "Bread", 2, "pieces");
        addRecipeIngredient(db, recipeId, "Tuna", 1, "cans");
        addRecipeIngredient(db, recipeId, "Mayonnaise", 2, "tablespoons");


        // 7. Chicken Sandwich
        recipeId = addRecipe(
                db,
                "Chicken Sandwich",
                "Cook the chicken and place it between slices of bread with lettuce and mayonnaise."
        );

        addRecipeIngredient(db, recipeId, "Chicken", 200, "g");
        addRecipeIngredient(db, recipeId, "Bread", 2, "pieces");
        addRecipeIngredient(db, recipeId, "Lettuce", 2, "leaves");
        addRecipeIngredient(db, recipeId, "Mayonnaise", 1, "tablespoons");


        // 8. Tomato Pasta
        recipeId = addRecipe(
                db,
                "Tomato Pasta",
                "Cook the pasta. Heat the tomato sauce with onion and mix with the cooked pasta."
        );

        addRecipeIngredient(db, recipeId, "Pasta", 250, "g");
        addRecipeIngredient(db, recipeId, "Tomato Sauce", 1, "cans");
        addRecipeIngredient(db, recipeId, "Onion", 1, "pieces");


        // 9. Rice and Beans
        recipeId = addRecipe(
                db,
                "Rice and Beans",
                "Cook the rice. Heat the beans with onion and seasoning, then serve together."
        );

        addRecipeIngredient(db, recipeId, "Rice", 2, "cups");
        addRecipeIngredient(db, recipeId, "Beans", 1, "cans");
        addRecipeIngredient(db, recipeId, "Onion", 1, "pieces");


        // 10. Chicken Curry
        recipeId = addRecipe(
                db,
                "Chicken Curry",
                "Fry the onion and chicken with curry seasoning. Add the sauce and simmer until the chicken is cooked."
        );

        addRecipeIngredient(db, recipeId, "Chicken", 500, "g");
        addRecipeIngredient(db, recipeId, "Onion", 1, "pieces");
        addRecipeIngredient(db, recipeId, "Curry Powder", 2, "tablespoons");
        addRecipeIngredient(db, recipeId, "Tomato Sauce", 1, "cans");


        // 11. Beef Stew
        recipeId = addRecipe(
                db,
                "Beef Stew",
                "Brown the beef and onion. Add vegetables and water, then simmer until the beef is tender."
        );

        addRecipeIngredient(db, recipeId, "Beef", 500, "g");
        addRecipeIngredient(db, recipeId, "Potatoes", 3, "pieces");
        addRecipeIngredient(db, recipeId, "Carrots", 2, "pieces");
        addRecipeIngredient(db, recipeId, "Onion", 1, "pieces");


        // 12. Vegetable Rice
        recipeId = addRecipe(
                db,
                "Vegetable Rice",
                "Cook the rice. Fry the vegetables and onion, then mix them with the cooked rice."
        );

        addRecipeIngredient(db, recipeId, "Rice", 2, "cups");
        addRecipeIngredient(db, recipeId, "Mixed Vegetables", 2, "cups");
        addRecipeIngredient(db, recipeId, "Onion", 1, "pieces");


        // 13. Potato Omelette
        recipeId = addRecipe(
                db,
                "Potato Omelette",
                "Cook the potatoes until tender. Beat the eggs and combine with the potatoes and onion, then fry."
        );

        addRecipeIngredient(db, recipeId, "Potatoes", 3, "pieces");
        addRecipeIngredient(db, recipeId, "Eggs", 3, "pieces");
        addRecipeIngredient(db, recipeId, "Onion", 1, "pieces");


        // 14. Bean Sandwich
        recipeId = addRecipe(
                db,
                "Bean Sandwich",
                "Mash the beans and season them. Spread the mixture onto bread and serve."
        );

        addRecipeIngredient(db, recipeId, "Beans", 1, "cans");
        addRecipeIngredient(db, recipeId, "Bread", 2, "pieces");


        // 15. Egg Sandwich
        recipeId = addRecipe(
                db,
                "Egg Sandwich",
                "Boil the eggs and mash them with mayonnaise. Spread onto bread and serve."
        );

        addRecipeIngredient(db, recipeId, "Eggs", 2, "pieces");
        addRecipeIngredient(db, recipeId, "Bread", 2, "pieces");
        addRecipeIngredient(db, recipeId, "Mayonnaise", 1, "tablespoons");


        // 16. Beef Pasta
        recipeId = addRecipe(
                db,
                "Beef Pasta",
                "Cook the pasta. Fry the beef and onion, add tomato sauce and combine with the pasta."
        );

        addRecipeIngredient(db, recipeId, "Beef", 300, "g");
        addRecipeIngredient(db, recipeId, "Pasta", 250, "g");
        addRecipeIngredient(db, recipeId, "Tomato Sauce", 1, "cans");
        addRecipeIngredient(db, recipeId, "Onion", 1, "pieces");


        // 17. Tuna Pasta
        recipeId = addRecipe(
                db,
                "Tuna Pasta",
                "Cook the pasta. Mix tuna and mayonnaise, then combine with the cooked pasta."
        );

        addRecipeIngredient(db, recipeId, "Pasta", 250, "g");
        addRecipeIngredient(db, recipeId, "Tuna", 1, "cans");
        addRecipeIngredient(db, recipeId, "Mayonnaise", 2, "tablespoons");


        // 18. Chicken and Potatoes
        recipeId = addRecipe(
                db,
                "Chicken and Potatoes",
                "Season the chicken and potatoes. Bake or fry until the chicken is cooked and the potatoes are tender."
        );

        addRecipeIngredient(db, recipeId, "Chicken", 500, "g");
        addRecipeIngredient(db, recipeId, "Potatoes", 4, "pieces");


        // 19. Vegetable Omelette
        recipeId = addRecipe(
                db,
                "Vegetable Omelette",
                "Beat the eggs. Fry the vegetables and onion, add the eggs and cook until set."
        );

        addRecipeIngredient(db, recipeId, "Eggs", 3, "pieces");
        addRecipeIngredient(db, recipeId, "Mixed Vegetables", 1, "cups");
        addRecipeIngredient(db, recipeId, "Onion", 1, "pieces");


        // 20. Beef and Rice
        recipeId = addRecipe(
                db,
                "Beef and Rice",
                "Cook the rice. Fry the beef and onion until cooked, then serve with the rice."
        );

        addRecipeIngredient(db, recipeId, "Beef", 500, "g");
        addRecipeIngredient(db, recipeId, "Rice", 2, "cups");
        addRecipeIngredient(db, recipeId, "Onion", 1, "pieces");
    }
}