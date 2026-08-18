package com.example.smartpantrymanager.utils;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientMatcher {

    public static class MatchResult {
        public final boolean isStrictMatch;
        public final int missingCount;
        public final List<RecipeIngredient> missingIngredients;

        public MatchResult(boolean isStrictMatch, int missingCount, List<RecipeIngredient> missingIngredients) {
            this.isStrictMatch = isStrictMatch;
            this.missingCount = missingCount;
            this.missingIngredients = missingIngredients;
        }
    }

    /**
     * Normalizes an ingredient name for flexible matching (handles lowercasing, plurals, whitespace).
     */
    public static String normalizeName(String name) {
        if (name == null) return "";
        String clean = name.trim().toLowerCase();

        // Strip common prefix/suffix words
        clean = clean.replaceAll("\\b(fresh|raw|chopped|diced|sliced|minced|large|small|medium|whole)\\b", "").trim();

        // Handle plurals
        if (clean.endsWith("tomatoes")) return clean.replace("tomatoes", "tomato");
        if (clean.endsWith("potatoes")) return clean.replace("potatoes", "potato");
        if (clean.endsWith("cherries")) return clean.replace("cherries", "cherry");
        if (clean.endsWith("berries")) return clean.replace("berries", "berry");
        if (clean.endsWith("es") && clean.length() > 4) return clean.substring(0, clean.length() - 2);
        if (clean.endsWith("s") && !clean.endsWith("ss") && clean.length() > 3) return clean.substring(0, clean.length() - 1);

        return clean;
    }

    /**
     * Normalizes measurement units.
     */
    public static String normalizeUnit(String unit) {
        if (unit == null) return "pcs";
        String u = unit.trim().toLowerCase();
        if (u.equals("gram") || u.equals("grams") || u.equals("g")) return "g";
        if (u.equals("kilogram") || u.equals("kilograms") || u.equals("kg")) return "kg";
        if (u.equals("milliliter") || u.equals("milliliters") || u.equals("ml")) return "ml";
        if (u.equals("liter") || u.equals("liters") || u.equals("l")) return "l";
        if (u.equals("tablespoon") || u.equals("tablespoons") || u.equals("tbsp")) return "tbsp";
        if (u.equals("teaspoon") || u.equals("teaspoons") || u.equals("tsp")) return "tsp";
        if (u.equals("cup") || u.equals("cups")) return "cup";
        if (u.equals("piece") || u.equals("pieces") || u.equals("pcs") || u.equals("item") || u.equals("items")) return "pcs";
        if (u.equals("clove") || u.equals("cloves")) return "clove";
        if (u.equals("slice") || u.equals("slices")) return "slice";
        return u;
    }

    /**
     * Converts quantity between compatible units.
     */
    public static double convertQuantity(double quantity, String sourceUnit, String targetUnit) {
        String src = normalizeUnit(sourceUnit);
        String tgt = normalizeUnit(targetUnit);

        if (src.equals(tgt)) return quantity;

        // Mass conversions
        if (src.equals("kg") && tgt.equals("g")) return quantity * 1000.0;
        if (src.equals("g") && tgt.equals("kg")) return quantity / 1000.0;

        // Volume conversions
        if (src.equals("l") && tgt.equals("ml")) return quantity * 1000.0;
        if (src.equals("ml") && tgt.equals("l")) return quantity / 1000.0;

        // Tablespoon / Teaspoon
        if (src.equals("tbsp") && tgt.equals("tsp")) return quantity * 3.0;
        if (src.equals("tsp") && tgt.equals("tbsp")) return quantity / 3.0;

        return quantity;
    }

    /**
     * Checks if a given pantry item satisfies a required recipe ingredient (name and quantity).
     */
    public static boolean isIngredientSatisfied(RecipeIngredient required, PantryItem pantryItem) {
        if (pantryItem == null) return false;

        String reqNameNorm = normalizeName(required.getIngredientName());
        String pantryNameNorm = normalizeName(pantryItem.getName());

        boolean nameMatch = reqNameNorm.equalsIgnoreCase(pantryNameNorm) ||
                            reqNameNorm.contains(pantryNameNorm) ||
                            pantryNameNorm.contains(reqNameNorm);

        if (!nameMatch) return false;

        double pantryQtyConverted = convertQuantity(pantryItem.getQuantity(), pantryItem.getUnit(), required.getUnit());
        return pantryQtyConverted >= required.getQuantity();
    }

    /**
     * Evaluates a recipe against a user's pantry.
     * Returns MatchResult with strict match status and missing count.
     */
    public static MatchResult evaluateRecipe(Recipe recipe, List<PantryItem> pantry) {
        if (recipe == null || recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            return new MatchResult(false, 0, new ArrayList<>());
        }

        int missingCount = 0;
        List<RecipeIngredient> missingIngredients = new ArrayList<>();

        for (RecipeIngredient req : recipe.getIngredients()) {
            boolean satisfied = false;
            if (pantry != null) {
                for (PantryItem pItem : pantry) {
                    if (isIngredientSatisfied(req, pItem)) {
                        satisfied = true;
                        break;
                    }
                }
            }

            if (!satisfied) {
                missingCount++;
                missingIngredients.add(req);
            }
        }

        boolean isStrictMatch = (missingCount == 0);
        return new MatchResult(isStrictMatch, missingCount, missingIngredients);
    }
}