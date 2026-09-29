# Smart Pantry Manager 🍳

> A Native Java Android Application that Suggests Recipes Based Strictly on Leftover Ingredients to Cut Food Waste.

**Module:** Mobile App Development 700 - Practical Assignment  
**Language:** Pure Java (Android SDK)  
**Database:** SQLite (`SQLiteOpenHelper`)  
**UI Framework:** Material Design 3 Components (Activities, Layouts, RecyclerView, Cards, BottomNavigationView)

---

## 📌 Project Overview

The **Smart Pantry Manager** is designed to fight household food waste by tracking ingredients users already have in their home pantry and suggesting recipes that can be cooked **strictly** using those existing leftovers—without requiring any additional grocery shopping.

---

## 🎯 Key Features & Requirements Met

### 1. Pantry Management (Full CRUD)
* **Create:** Add new pantry items with Name, Quantity, Unit of Measurement (g, kg, ml, L, pcs, cups, tbsp, tsp, etc.), Category, and Expiry Date.
* **Read:** View dynamic list of ingredients via `RecyclerView` bound to SQLite database.
* **Update:** Edit existing ingredient details, quantities, or expiration dates.
* **Delete:** Remove pantry items with confirmation dialogs.
* **Search & Filter:** Instant real-time search across pantry items.
* **Expiry Highlights:** Visual badges highlighting fresh, expiring soon (within 3 days), and expired items.

### 2. Core Business Logic: Strict-Matching Rule
* **100% Strict Match:** A recipe appears under "Suggested Recipes" **only if every single required ingredient** is present in the user's pantry in at least the required quantity.
* **Robust Matching Algorithm (`IngredientMatcher`):**
  * Normalizes ingredient names (handles case insensitivity, whitespace, plurals like "tomatoes" -> "tomato", "eggs" -> "egg").
  * Smart unit conversions (e.g., 1.5 kg in pantry satisfies a 500 g recipe requirement; 1 L satisfies 200 ml).
* **Zero-Match Feedback:** Clear empty-state messaging when no recipes match current pantry ingredients.
* **Stretch Feature - "Almost There":** Dedicated tab showing recipes missing only 1 ingredient.

### 3. Recipe Collection & Cooking Action
* **Pre-Loaded Seed Collection:** 20 rich, realistic recipes seeded on first launch into SQLite (Omelette, Fried Rice, Garlic Butter Pasta, French Toast, Pancakes, Chicken Noodle Soup, Guacamole, Potato Soup, etc.).
* **Recipe Details View:** Ingredient checklist against current pantry availability + step-by-step preparation steps.
* **"I Cooked This Recipe!":** Deducts cooked ingredient quantities directly from the SQLite pantry database.

### 4. Settings & Customization
* Toggle switch for Expiring Soon Alert highlights.
* Data management options: Clear All Pantry Items, Reset & Seed Default Recipe Database.
* App Version & Architecture Information.

---

## 🗄️ Database Choice & Justification

**Choice:** Local On-Device **SQLite** via `SQLiteOpenHelper`.

**Justification:**
1. **Offline Availability & Fast Access:** Pantry management is a daily kitchen task; local SQLite ensures instant reads/writes without network latency or internet dependency.
2. **Data Integrity & Relational Model:** Recipes and ingredients have structured relational constraints (One-to-Many relationship between `recipes` and `recipe_ingredients`).
3. **Module Alignment:** Aligns with persistent data storage standards for Native Java Android development.

---

## 📂 Project Structure

```
com.example.smartpantrymanager/
├── activities/
│   ├── PantryListActivity.java        (Main Pantry View - CRUD & Search)
│   ├── AddEditIngredientActivity.java (Form for Adding/Editing Ingredients with Validation)
│   ├── SuggestedRecipesActivity.java  (Core Strict-Matching Logic & Suggestions)
│   ├── AllRecipesActivity.java        (Browse & Filter 20+ Seeded Recipes)
│   ├── RecipeDetailActivity.java      (Ingredient Checklist & Deduct Action)
│   └── SettingsActivity.java          (Alerts Toggle & Database Reset Controls)
├── adapters/
│   ├── PantryAdapter.java             (RecyclerView Adapter for Pantry Items)
│   ├── RecipeAdapter.java             (RecyclerView Adapter for Recipe Cards)
│   └── RecipeIngredientAdapter.java   (RecyclerView Adapter for Recipe Details Checklist)
├── db/
│   └── DatabaseHelper.java            (SQLite Database Helper, Tables, CRUD & Seeding)
├── models/
│   ├── PantryItem.java                (Pantry Ingredient Model)
│   ├── Recipe.java                    (Recipe Entity Model)
│   └── RecipeIngredient.java          (Recipe Requirement Model)
└── utils/
    └── IngredientMatcher.java         (Strict Matching & Unit Conversion Helper)
```

---

## 🚀 Setup & Execution Instructions

1. **Clone or Extract Project:**
   ```bash
   git clone <repository-url>
   ```
2. **Open in Android Studio:**
   * Launch Android Studio (2024.1+ recommended).
   * Choose `File -> Open` and select the `SMARTPANTRYMANAGER2` folder.
3. **Sync & Build:**
   * Allow Gradle sync to complete automatically (`JDK 11` or higher).
   * Run Gradle Task: `app:assembleDebug`.
4. **Run Application:**
   * Select an Android Emulator or physical device (Android 7.0 / API 24 or higher).
   * Click **Run (Shift + F10)**.

---

## 🧪 Unit Tests

Run unit tests verifying the strict-matching logic:
```bash
./gradlew testDebugUnitTest
```
Tests verify:
* 100% strict match when all ingredients are present.
* Rejection when 1 ingredient is missing or insufficient quantity.
* Name normalization (plural & case handling).
* Mass unit conversions (kg to g).

---

## 📄 License & Originality
This project was developed independently for **Mobile App Development 700 Practical Assignment**. Contains 0 location/GPS/Google Maps SDKs as required by assignment specifications.
