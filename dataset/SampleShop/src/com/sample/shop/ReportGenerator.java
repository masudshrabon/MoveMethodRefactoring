package com.sample.shop;

/** Builds text reports. */
public class ReportGenerator {

    private String reportTitle;
    private String author;

    public ReportGenerator(String reportTitle, String author) {
        this.reportTitle = reportTitle;
        this.author = author;
    }

    /** Well placed: uses only the report's own fields. */
    public String buildReportHeader() {
        return reportTitle + " by " + author;
    }

    /** FEATURE ENVY (planted): works almost entirely on Inventory -> should move to Inventory. */
    public String summarizeStock(Inventory inventory, String[] products) {
        StringBuilder text = new StringBuilder(inventory.getWarehouseName());
        for (String product : products) {
            text.append("\n").append(product).append(": ").append(inventory.getStockLevel(product));
            if (inventory.needsReorder(product)) {
                text.append(" (reorder)");
            }
        }
        return text.toString();
    }
}
