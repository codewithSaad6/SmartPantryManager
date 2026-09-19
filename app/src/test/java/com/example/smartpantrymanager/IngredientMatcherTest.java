package com.example.smartpantrymanager;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;
import com.example.smartpantrymanager.utils.IngredientMatcher;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IngredientMatcherTest {

    @Test
    public void testStrictMatching_AllIngredientsPresent_ReturnsTrue() {
        Recipe recipe = new Recipe("Omelette", "Breakfast", 5, 5, "Cook eggs");
        recipe.addIngredient(new RecipeIngredient("Eggs", 2, "pcs"));
        recipe.addIngredient(new RecipeIngredient("Milk", 50, "ml"));
        recipe.addIngredient(new RecipeIngredient("Salt", 1, "tsp"));

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem("Eggs", 6, "pcs", "2025-12-31", "Dairy"));
        pantry.add(new PantryItem("Milk", 500, "ml", "2025-12-31", "Dairy"));
        pantry.add(new PantryItem("Salt", 1, "tsp", "", "Spices"));

        IngredientMatcher.MatchResult result = IngredientMatcher.evaluateRecipe(recipe, pantry);

        assertTrue("Recipe should strictly match when all ingredients are available", result.isStrictMatch);
        assertEquals(0, result.missingCount);
    }

    @Test
    public void testStrictMatching_MissingOneIngredient_ReturnsFalse() {
        Recipe recipe = new Recipe("Omelette", "Breakfast", 5, 5, "Cook eggs");
        recipe.addIngredient(new RecipeIngredient("Eggs", 2, "pcs"));
        recipe.addIngredient(new RecipeIngredient("Milk", 50, "ml"));
        recipe.addIngredient(new RecipeIngredient("Cheese", 50, "g"));

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem("Eggs", 6, "pcs", "2025-12-31", "Dairy"));
        pantry.add(new PantryItem("Milk", 500, "ml", "2025-12-31", "Dairy"));
        // Missing Cheese!

        IngredientMatcher.MatchResult result = IngredientMatcher.evaluateRecipe(recipe, pantry);

        assertFalse("Recipe must NOT match if an ingredient is missing", result.isStrictMatch);
        assertEquals(1, result.missingCount);
        assertEquals("Cheese", result.missingIngredients.get(0).getIngredientName());
    }

    @Test
    public void testStrictMatching_InsufficientQuantity_ReturnsFalse() {
        Recipe recipe = new Recipe("Pancakes", "Breakfast", 10, 10, "Mix and cook");
        recipe.addIngredient(new RecipeIngredient("Flour", 200, "g"));

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem("Flour", 100, "g", "", "Grains")); // Needs 200g, has 100g

        IngredientMatcher.MatchResult result = IngredientMatcher.evaluateRecipe(recipe, pantry);

        assertFalse("Recipe should fail if quantity is insufficient", result.isStrictMatch);
        assertEquals(1, result.missingCount);
    }

    @Test
    public void testNormalizer_PluralsAndLowercasing() {
        assertEquals("tomato", IngredientMatcher.normalizeName("Tomatoes"));
        assertEquals("egg", IngredientMatcher.normalizeName("EGGS "));
        assertEquals("potato", IngredientMatcher.normalizeName("Fresh Potatoes"));
    }

    @Test
    public void testUnitConversion_GramsToKilograms() {
        Recipe recipe = new Recipe("Pancakes", "Breakfast", 10, 10, "Mix and cook");
        recipe.addIngredient(new RecipeIngredient("Flour", 500, "g"));

        List<PantryItem> pantry = new ArrayList<>();
        pantry.add(new PantryItem("Flour", 1.5, "kg", "", "Grains")); // 1.5 kg = 1500 g >= 500 g

        IngredientMatcher.MatchResult result = IngredientMatcher.evaluateRecipe(recipe, pantry);

        assertTrue("1.5 kg should satisfy 500 g requirement", result.isStrictMatch);
    }
}