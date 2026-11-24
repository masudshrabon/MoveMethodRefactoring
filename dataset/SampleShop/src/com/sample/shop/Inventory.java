package com.sample.shop;

import java.util.HashMap;
import java.util.Map;

/** Stock levels of a warehouse. */
public class Inventory {

    private String warehouseName;
    private Map<String, Integer> stockLevels = new HashMap<>();
    private int reorderThreshold;

    public Inventory(String warehouseName, int reorderThreshold) {
        this.warehouseName = warehouseName;
        this.reorderThreshold = reorderThreshold;
    }

    public String getWarehouseName() { return warehouseName; }

    public int getStockLevel(String productName) {
        Integer level = stockLevels.get(productName);
        return level == null ? 0 : level;
    }

    public boolean isInStock(String productName) {
        return getStockLevel(productName) > 0;
    }

    public boolean needsReorder(String productName) {
        return getStockLevel(productName) < reorderThreshold;
    }

    public void restock(String productName, int amount) {
        stockLevels.put(productName, getStockLevel(productName) + amount);
    }
}
