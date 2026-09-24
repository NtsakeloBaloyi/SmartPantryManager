# Smart Pantry Manager

## Description

Smart Pantry Manager is a Java-based Android application developed to help users manage pantry ingredients and reduce food waste. The application allows users to add, edit and delete pantry ingredients while recording quantities, measurement units and optional expiry dates.

The application also contains a collection of preloaded recipes. Recipes are suggested only when all of the required ingredients and sufficient quantities are available in the user's pantry. This strict matching approach prevents recipes from being suggested when ingredients are missing or insufficient.

## Main Features

* Add pantry ingredients
* Edit existing pantry ingredients
* Delete pantry ingredients
* Record ingredient quantities and measurement units
* Record optional expiry dates
* View all pantry ingredients using a RecyclerView
* Preloaded recipe collection containing 20 recipes
* Strict recipe matching based on available ingredients and quantities
* Ingredient name normalisation for simple singular/plural differences
* Unit normalisation and basic weight and volume conversions
* View complete recipe ingredients and preparation instructions
* Settings and Profile screen
* Input validation and user feedback
* Persistent SQLite database storage

## Technology Used

* Java
* Android Studio
* Android SDK
* SQLite
* RecyclerView
* Intents
* XML layouts
* Git and GitHub

## Database Choice

SQLite was selected because Smart Pantry Manager is a local Android application that does not require an online server or cloud database.

SQLite provides persistent local storage, allowing pantry ingredients and recipe information to remain available after the application is closed and reopened. It also supports the required Create, Read, Update and Delete (CRUD) operations.

The database contains tables for pantry ingredients, recipes and recipe ingredients.

## Database Structure

### Pantry Table

Stores the ingredients currently available in the user's pantry.

Main fields:

* `id`
* `name`
* `quantity`
* `unit`
* `expiry_date`

### Recipes Table

Stores the available recipes.

Main fields:

* `id`
* `name`
* `instructions`

### Recipe Ingredients Table

Stores the ingredients and quantities required for each recipe.

Main fields:

* `id`
* `recipe_id`
* `ingredient_name`
* `required_quantity`
* `unit`

## Recipe Matching

The application uses strict recipe matching.

A recipe is displayed as a suggestion only when every required ingredient is available in the pantry in a sufficient quantity.

For example, if a recipe requires:

* Eggs: 2 pieces
* Bread: 2 pieces
* Mayonnaise: 1 tablespoon

the recipe will only appear when the pantry contains all three ingredients with sufficient quantities.

The matching logic also normalises common singular and plural ingredient names and measurement units. Weight measurements can be compared between grams and kilograms, while volume measurements can be compared between millilitres and litres.

## Application Screens

The application contains the following main screens:

1. My Pantry
2. Add Ingredient
3. Edit Ingredient
4. Suggested Recipes
5. Recipe Details
6. Settings & Profile

## Setup and Run Instructions

1. Install Android Studio.
2. Clone or download the Smart Pantry Manager project from GitHub.
3. Open the project in Android Studio.
4. Allow Android Studio to complete Gradle synchronisation.
5. Make sure a compatible Android emulator is available.
6. Select the emulator as the target device.
7. Build the project.
8. Run the application.
9. The application opens on the My Pantry screen.

## GitHub

The project is maintained under Git version control and contains incremental commits documenting the development process.

Repository:

NtsakeloBaloyi/SmartPantryManager

## Development

The application was developed incrementally using Git version control. The development history includes commits for:

* Android project creation
* SQLite database setup
* Pantry CRUD functionality
* Recipe database and models
* Suggested recipe matching
* Recipe detail screen
* Settings and Profile screen
* Recipe RecyclerView adapter
* Ingredient validation
* Delete confirmation

## Project Purpose

The purpose of Smart Pantry Manager is to provide a simple mobile solution for managing available pantry ingredients and identifying recipes that can be prepared immediately from those ingredients. By avoiding partial recipe suggestions, the application focuses on practical meal planning using ingredients that are already available.
