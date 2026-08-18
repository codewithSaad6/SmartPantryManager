package com.example.smartpantrymanager.models;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private long id;
    private String title;
    private String category;
    private int prepTimeMinutes;
    private int cookTimeMinutes;
    private String instructions;
    private List<RecipeIngredient> ingredients;

    public Recipe() {
        this.ingredients = new ArrayList<>();
    }

    public Recipe(long id, String title, String category, int prepTimeMinutes, int cookTimeMinutes, String instructions) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.prepTimeMinutes = prepTimeMinutes;
        this.cookTimeMinutes = cookTimeMinutes;
        this.instructions = instructions;
        this.ingredients = new ArrayList<>();
    }

    public Recipe(String title, String category, int prepTimeMinutes, int cookTimeMinutes, String instructions) {
        this.title = title;
        this.category = category;
        this.prepTimeMinutes = prepTimeMinutes;
        this.cookTimeMinutes = cookTimeMinutes;
        this.instructions = instructions;
        this.ingredients = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(int prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public int getCookTimeMinutes() {
        return cookTimeMinutes;
    }

    public void setCookTimeMinutes(int cookTimeMinutes) {
        this.cookTimeMinutes = cookTimeMinutes;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(RecipeIngredient ingredient) {
        this.ingredients.add(ingredient);
    }
}