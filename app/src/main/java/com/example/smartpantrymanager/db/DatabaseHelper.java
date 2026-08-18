package com.example.smartpantrymanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;
import com.example.smartpantrymanager.utils.IngredientMatcher;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    // Common Column
    public static final String COL_ID = "_id";

    // Pantry Table Columns
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QTY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date";
    public static final String COL_PANTRY_CATEGORY = "category";

    // Recipes Table Columns
    public static final String COL_RECIPE_TITLE = "title";
    public static final String COL_RECIPE_CATEGORY = "category";
    public static final String COL_RECIPE_PREP_TIME = "prep_time";
    public static final String COL_RECIPE_COOK_TIME = "cook_time";
    public static final String COL_RECIPE_INSTRUCTIONS = "instructions";

    // Recipe Ingredients Table Columns
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_ING_NAME = "ingredient_name";
    public static final String COL_RI_QTY = "quantity";
    public static final String COL_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Pantry Table
        String createPantryTable = "CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PANTRY_NAME + " TEXT NOT NULL, " +
                COL_PANTRY_QTY + " REAL NOT NULL, " +
                COL_PANTRY_UNIT + " TEXT NOT NULL, " +
                COL_PANTRY_EXPIRY + " TEXT, " +
                COL_PANTRY_CATEGORY + " TEXT);";

        // Create Recipes Table
        String createRecipesTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_TITLE + " TEXT NOT NULL, " +
                COL_RECIPE_CATEGORY + " TEXT, " +
                COL_RECIPE_PREP_TIME + " INTEGER, " +
                COL_RECIPE_COOK_TIME + " INTEGER, " +
                COL_RECIPE_INSTRUCTIONS + " TEXT);";

        // Create Recipe Ingredients Table
        String createRecipeIngredientsTable = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RI_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_RI_ING_NAME + " TEXT NOT NULL, " +
                COL_RI_QTY + " REAL NOT NULL, " +
                COL_RI_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_ID + ") ON DELETE CASCADE);";

        db.execSQL(createPantryTable);
        db.execSQL(createRecipesTable);
        db.execSQL(createRecipeIngredientsTable);

        // Seed Default Recipes
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ==================== PANTRY CRUD OPERATIONS ====================

    public long insertPantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PANTRY_NAME, item.getName().trim());
        cv.put(COL_PANTRY_QTY, item.getQuantity());
        cv.put(COL_PANTRY_UNIT, item.getUnit());
        cv.put(COL_PANTRY_EXPIRY, item.getExpiryDate());
        cv.put(COL_PANTRY_CATEGORY, item.getCategory());

        long id = db.insert(TABLE_PANTRY, null, cv);
        item.setId(id);
        return id;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, null, null, null, null, COL_PANTRY_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                PantryItem item = new PantryItem(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QTY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_CATEGORY))
                );
                list.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public PantryItem getPantryItemById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            PantryItem item = new PantryItem(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QTY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_CATEGORY))
            );
            cursor.close();
            return item;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PANTRY_NAME, item.getName().trim());
        cv.put(COL_PANTRY_QTY, item.getQuantity());
        cv.put(COL_PANTRY_UNIT, item.getUnit());
        cv.put(COL_PANTRY_EXPIRY, item.getExpiryDate());
        cv.put(COL_PANTRY_CATEGORY, item.getCategory());

        return db.update(TABLE_PANTRY, cv, COL_ID + "=?", new String[]{String.valueOf(item.getId())});
    }

    public int deletePantryItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void clearAllPantryItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, null, null);
    }

    public List<PantryItem> searchPantryItems(String query) {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COL_PANTRY_NAME + " LIKE ?",
                new String[]{"%" + query + "%"}, null, null, COL_PANTRY_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                PantryItem item = new PantryItem(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PANTRY_QTY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_UNIT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_EXPIRY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PANTRY_CATEGORY))
                );
                list.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    // ==================== RECIPES & MATCHING OPERATIONS ====================

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, null, null, null, null, COL_RECIPE_TITLE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long recipeId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                Recipe recipe = new Recipe(
                        recipeId,
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_CATEGORY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_PREP_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_COOK_TIME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_INSTRUCTIONS))
                );
                recipe.setIngredients(getIngredientsForRecipe(db, recipeId));
                recipes.add(recipe);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return recipes;
    }

    public Recipe getRecipeById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Recipe recipe = new Recipe(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_CATEGORY)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_PREP_TIME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_COOK_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_INSTRUCTIONS))
            );
            recipe.setIngredients(getIngredientsForRecipe(db, id));
            cursor.close();
            return recipe;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        boolean closeDb = false;
        if (db == null) {
            db = this.getReadableDatabase();
            closeDb = true;
        }

        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null, COL_RI_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, COL_ID + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                RecipeIngredient ri = new RecipeIngredient(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_RI_RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_ING_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_RI_QTY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_RI_UNIT))
                );
                list.add(ri);
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (closeDb && db != null && db.isOpen()) {
            db.close();
        }

        return list;
    }

    /**
     * Executes the strict-matching logic against the user's current pantry.
     * @param strictOnly true to return 100% strict matches (missing == 0), false to return "Almost There" (missing == 1)
     */
    public List<Recipe> getSuggestedRecipes(boolean strictOnly) {
        List<PantryItem> pantry = getAllPantryItems();
        List<Recipe> allRecipes = getAllRecipes();
        List<Recipe> suggested = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            IngredientMatcher.MatchResult result = IngredientMatcher.evaluateRecipe(recipe, pantry);
            if (strictOnly) {
                if (result.isStrictMatch) {
                    suggested.add(recipe);
                }
            } else {
                if (!result.isStrictMatch && result.missingCount == 1) {
                    suggested.add(recipe);
                }
            }
        }
        return suggested;
    }

    /**
     * Deducts recipe ingredient quantities from pantry when user cooks a recipe.
     */
    public void deductIngredientsForRecipe(Recipe recipe) {
        if (recipe == null || recipe.getIngredients() == null) return;
        List<PantryItem> pantryList = getAllPantryItems();

        for (RecipeIngredient req : recipe.getIngredients()) {
            for (PantryItem pItem : pantryList) {
                if (IngredientMatcher.isIngredientSatisfied(req, pItem)) {
                    double reqQtyInPantryUnit = IngredientMatcher.convertQuantity(req.getQuantity(), req.getUnit(), pItem.getUnit());
                    double newQty = pItem.getQuantity() - reqQtyInPantryUnit;

                    if (newQty <= 0) {
                        deletePantryItem(pItem.getId());
                    } else {
                        pItem.setQuantity(newQty);
                        updatePantryItem(pItem);
                    }
                    break;
                }
            }
        }
    }

    public void resetAndSeedRecipes() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DELETE FROM " + TABLE_RECIPES);
        seedRecipes(db);
    }

    // ==================== SEED DATA (20 REALISTIC RECIPES) ====================

    private void seedRecipes(SQLiteDatabase db) {
        // 1. Scrambled Eggs
        addRecipeWithIngredients(db, "Scrambled Eggs", "Breakfast", 5, 5,
                "1. Whisk eggs, milk, and salt in a bowl.\n2. Melt butter in a non-stick pan over medium heat.\n3. Pour in eggs and gently fold until softly set.\n4. Serve immediately.",
                new RecipeIngredient("Eggs", 2, "pcs"),
                new RecipeIngredient("Milk", 50, "ml"),
                new RecipeIngredient("Butter", 15, "g"),
                new RecipeIngredient("Salt", 1, "tsp"));

        // 2. Classic Omelette
        addRecipeWithIngredients(db, "Classic Omelette", "Breakfast", 5, 7,
                "1. Beat eggs with milk and salt.\n2. Pour into hot buttered pan.\n3. Sprinkle grated cheese when eggs begin to set.\n4. Fold in half and serve.",
                new RecipeIngredient("Eggs", 3, "pcs"),
                new RecipeIngredient("Cheese", 50, "g"),
                new RecipeIngredient("Milk", 30, "ml"),
                new RecipeIngredient("Butter", 15, "g"),
                new RecipeIngredient("Salt", 1, "tsp"));

        // 3. Fried Rice
        addRecipeWithIngredients(db, "Egg Fried Rice", "Main Dish", 10, 10,
                "1. Heat oil in skillet, saute chopped onion and minced garlic.\n2. Push to side, scramble eggs in pan.\n3. Add cooked rice and soy sauce, toss well on high heat.",
                new RecipeIngredient("Rice", 200, "g"),
                new RecipeIngredient("Eggs", 2, "pcs"),
                new RecipeIngredient("Soy Sauce", 20, "ml"),
                new RecipeIngredient("Garlic", 2, "clove"),
                new RecipeIngredient("Onion", 1, "pcs"),
                new RecipeIngredient("Vegetable Oil", 15, "ml"));

        // 4. Garlic Butter Pasta
        addRecipeWithIngredients(db, "Garlic Butter Pasta", "Main Dish", 10, 12,
                "1. Boil pasta in salted water until al dente.\n2. Melt butter in skillet, cook minced garlic until fragrant.\n3. Toss pasta in garlic butter, garnish with salt.",
                new RecipeIngredient("Pasta", 200, "g"),
                new RecipeIngredient("Butter", 30, "g"),
                new RecipeIngredient("Garlic", 3, "clove"),
                new RecipeIngredient("Salt", 1, "tsp"));

        // 5. Tomato Basil Pasta
        addRecipeWithIngredients(db, "Tomato Pasta", "Main Dish", 10, 15,
                "1. Boil pasta until tender.\n2. Heat olive oil, saute garlic and chopped tomatoes until sauce forms.\n3. Mix pasta into tomato sauce and season with salt.",
                new RecipeIngredient("Pasta", 200, "g"),
                new RecipeIngredient("Tomatoes", 3, "pcs"),
                new RecipeIngredient("Garlic", 2, "clove"),
                new RecipeIngredient("Olive Oil", 15, "ml"),
                new RecipeIngredient("Salt", 1, "tsp"));

        // 6. Grilled Cheese Sandwich
        addRecipeWithIngredients(db, "Grilled Cheese Sandwich", "Snack", 5, 5,
                "1. Butter one side of each slice of bread.\n2. Place cheese between unbuttered sides.\n3. Grill on pan over medium heat until golden brown and cheese melts.",
                new RecipeIngredient("Bread", 2, "slice"),
                new RecipeIngredient("Cheese", 40, "g"),
                new RecipeIngredient("Butter", 15, "g"));

        // 7. French Toast
        addRecipeWithIngredients(db, "French Toast", "Breakfast", 5, 8,
                "1. Whisk eggs, milk, and sugar in shallow dish.\n2. Dip bread slices to coat both sides.\n3. Fry in buttered pan until golden on both sides.",
                new RecipeIngredient("Bread", 3, "slice"),
                new RecipeIngredient("Eggs", 2, "pcs"),
                new RecipeIngredient("Milk", 60, "ml"),
                new RecipeIngredient("Sugar", 10, "g"),
                new RecipeIngredient("Butter", 15, "g"));

        // 8. Fluffy Pancakes
        addRecipeWithIngredients(db, "Pancakes", "Breakfast", 10, 10,
                "1. Mix flour, sugar, eggs, and milk into smooth batter.\n2. Pour batter portions onto melted butter pan.\n3. Flip when bubbles form on surface.",
                new RecipeIngredient("Flour", 150, "g"),
                new RecipeIngredient("Milk", 200, "ml"),
                new RecipeIngredient("Eggs", 1, "pcs"),
                new RecipeIngredient("Sugar", 20, "g"),
                new RecipeIngredient("Butter", 15, "g"));

        // 9. Chicken Noodle Soup
        addRecipeWithIngredients(db, "Chicken Noodle Soup", "Soup", 15, 25,
                "1. Saute diced onion, garlic, and sliced carrots in pot.\n2. Add shredded chicken and water/broth, simmer 15 mins.\n3. Add pasta and salt, cook until noodles are tender.",
                new RecipeIngredient("Chicken", 250, "g"),
                new RecipeIngredient("Carrots", 2, "pcs"),
                new RecipeIngredient("Onion", 1, "pcs"),
                new RecipeIngredient("Garlic", 2, "clove"),
                new RecipeIngredient("Pasta", 100, "g"),
                new RecipeIngredient("Salt", 1, "tsp"));

        // 10. Vegetable Stir Fry
        addRecipeWithIngredients(db, "Vegetable Stir Fry", "Side Dish", 10, 10,
                "1. Chop carrots and broccoli into bite-sized pieces.\n2. Heat vegetable oil in wok, fry garlic for 30s.\n3. Add vegetables and soy sauce, stir-fry on high heat for 6 mins.",
                new RecipeIngredient("Carrots", 2, "pcs"),
                new RecipeIngredient("Broccoli", 1, "pcs"),
                new RecipeIngredient("Soy Sauce", 20, "ml"),
                new RecipeIngredient("Garlic", 2, "clove"),
                new RecipeIngredient("Vegetable Oil", 15, "ml"));

        // 11. Mashed Potatoes
        addRecipeWithIngredients(db, "Creamy Mashed Potatoes", "Side Dish", 15, 20,
                "1. Peel and boil potatoes in water until fork tender.\n2. Drain water, mash potatoes thoroughly.\n3. Stir in butter, warm milk, and salt until smooth.",
                new RecipeIngredient("Potatoes", 4, "pcs"),
                new RecipeIngredient("Butter", 30, "g"),
                new RecipeIngredient("Milk", 60, "ml"),
                new RecipeIngredient("Salt", 1, "tsp"));

        // 12. Garlic Bread
        addRecipeWithIngredients(db, "Garlic Bread", "Appetizer", 5, 10,
                "1. Mix softened butter with minced garlic.\n2. Spread generously over bread slices.\n3. Bake or toast in oven until crispy and golden.",
                new RecipeIngredient("Bread", 4, "slice"),
                new RecipeIngredient("Butter", 30, "g"),
                new RecipeIngredient("Garlic", 3, "clove"));

        // 13. Fresh Guacamole
        addRecipeWithIngredients(db, "Fresh Guacamole", "Dip/Snack", 10, 0,
                "1. Mash ripe avocados in a bowl.\n2. Stir in finely diced onion, tomatoes, lemon juice, and salt.\n3. Serve immediately with chips or toast.",
                new RecipeIngredient("Avocado", 2, "pcs"),
                new RecipeIngredient("Onion", 0.5, "pcs"),
                new RecipeIngredient("Tomatoes", 1, "pcs"),
                new RecipeIngredient("Lemon", 1, "pcs"),
                new RecipeIngredient("Salt", 1, "tsp"));

        // 14. Banana Oatmeal
        addRecipeWithIngredients(db, "Banana Honey Oatmeal", "Breakfast", 5, 5,
                "1. Combine oats and milk in a pot, cook over medium heat for 5 mins.\n2. Slice banana on top and drizzle with honey before serving.",
                new RecipeIngredient("Oats", 80, "g"),
                new RecipeIngredient("Milk", 250, "ml"),
                new RecipeIngredient("Banana", 1, "pcs"),
                new RecipeIngredient("Honey", 15, "ml"));

        // 15. Creamy Potato Soup
        addRecipeWithIngredients(db, "Potato Soup", "Soup", 15, 20,
                "1. Cook diced potatoes and onions in water until soft.\n2. Add milk, butter, and salt.\n3. Mash partially and simmer until rich and creamy.",
                new RecipeIngredient("Potatoes", 3, "pcs"),
                new RecipeIngredient("Onion", 1, "pcs"),
                new RecipeIngredient("Milk", 200, "ml"),
                new RecipeIngredient("Butter", 20, "g"),
                new RecipeIngredient("Salt", 1, "tsp"));

        // 16. Simple Tuna Salad
        addRecipeWithIngredients(db, "Tuna Salad", "Salad", 10, 0,
                "1. Drain canned tuna.\n2. Mix tuna, mayonnaise, finely chopped onion, salt, and black pepper in a bowl.\n3. Serve on bread or lettuce.",
                new RecipeIngredient("Tuna", 150, "g"),
                new RecipeIngredient("Mayonnaise", 30, "g"),
                new RecipeIngredient("Onion", 0.5, "pcs"),
                new RecipeIngredient("Salt", 1, "tsp"));

        // 17. Caesar Salad
        addRecipeWithIngredients(db, "Caesar Salad", "Salad", 10, 5,
                "1. Wash and chop lettuce.\n2. Toast bread cubes in olive oil and garlic for croutons.\n3. Toss lettuce, croutons, olive oil, and parmesan cheese.",
                new RecipeIngredient("Lettuce", 1, "pcs"),
                new RecipeIngredient("Bread", 2, "slice"),
                new RecipeIngredient("Olive Oil", 20, "ml"),
                new RecipeIngredient("Garlic", 1, "clove"),
                new RecipeIngredient("Cheese", 30, "g"));

        // 18. Fresh Fruit Salad
        addRecipeWithIngredients(db, "Fresh Fruit Salad", "Dessert", 10, 0,
                "1. Chop apple, banana, and orange into bite-size pieces.\n2. Toss together in bowl and drizzle with honey.",
                new RecipeIngredient("Apple", 1, "pcs"),
                new RecipeIngredient("Banana", 1, "pcs"),
                new RecipeIngredient("Orange", 1, "pcs"),
                new RecipeIngredient("Honey", 15, "ml"));

        // 19. Rich Tomato Soup
        addRecipeWithIngredients(db, "Tomato Soup", "Soup", 10, 20,
                "1. Cook diced tomatoes, onion, and garlic in butter.\n2. Add milk and salt, simmer for 15 mins.\n3. Blend until smooth.",
                new RecipeIngredient("Tomatoes", 4, "pcs"),
                new RecipeIngredient("Onion", 1, "pcs"),
                new RecipeIngredient("Garlic", 2, "clove"),
                new RecipeIngredient("Butter", 20, "g"),
                new RecipeIngredient("Milk", 150, "ml"),
                new RecipeIngredient("Salt", 1, "tsp"));

        // 20. Peanut Butter Banana Toast
        addRecipeWithIngredients(db, "Peanut Butter Banana Toast", "Snack", 3, 2,
                "1. Toast bread slices until crisp.\n2. Spread peanut butter evenly.\n3. Top with sliced banana.",
                new RecipeIngredient("Bread", 2, "slice"),
                new RecipeIngredient("Peanut Butter", 30, "g"),
                new RecipeIngredient("Banana", 1, "pcs"));
    }

    private void addRecipeWithIngredients(SQLiteDatabase db, String title, String category, int prepTime, int cookTime, String instructions, RecipeIngredient... ingredients) {
        ContentValues cv = new ContentValues();
        cv.put(COL_RECIPE_TITLE, title);
        cv.put(COL_RECIPE_CATEGORY, category);
        cv.put(COL_RECIPE_PREP_TIME, prepTime);
        cv.put(COL_RECIPE_COOK_TIME, cookTime);
        cv.put(COL_RECIPE_INSTRUCTIONS, instructions);

        long recipeId = db.insert(TABLE_RECIPES, null, cv);

        for (RecipeIngredient ri : ingredients) {
            ContentValues riCv = new ContentValues();
            riCv.put(COL_RI_RECIPE_ID, recipeId);
            riCv.put(COL_RI_ING_NAME, ri.getIngredientName());
            riCv.put(COL_RI_QTY, ri.getQuantity());
            riCv.put(COL_RI_UNIT, ri.getUnit());

            db.insert(TABLE_RECIPE_INGREDIENTS, null, riCv);
        }
    }
}