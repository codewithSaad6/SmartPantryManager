package com.example.smartpantrymanager.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PantryItem {
    private long id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate; // YYYY-MM-DD
    private String category;

    public PantryItem() {
    }

    public PantryItem(long id, String name, double quantity, String unit, String expiryDate, String category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
        this.category = category;
    }

    public PantryItem(String name, double quantity, String unit, String expiryDate, String category) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Determines expiry status:
     * 0 = Fresh
     * 1 = Expiring Soon (within 3 days)
     * 2 = Expired
     */
    public int getExpiryStatus() {
        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            return 0; // Fresh / No expiry specified
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date exp = sdf.parse(expiryDate);
            Date today = new Date();
            // Reset time part for accurate date diff
            String todayStr = sdf.format(today);
            today = sdf.parse(todayStr);

            if (exp == null || today == null) return 0;

            long diffInMillis = exp.getTime() - today.getTime();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

            if (diffInDays < 0) {
                return 2; // Expired
            } else if (diffInDays <= 3) {
                return 1; // Expiring soon
            } else {
                return 0; // Fresh
            }
        } catch (ParseException e) {
            return 0;
        }
    }
}