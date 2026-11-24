package com.sample.shop;

/** One line of an order. */
public class OrderItem {

    private String productName;
    private double unitPrice;
    private int quantity;

    public OrderItem(String productName, double unitPrice, int quantity) {
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getProductName() { return productName; }
    public double getUnitPrice()   { return unitPrice; }
    public int getQuantity()       { return quantity; }

    /** Well placed: uses only OrderItem's own fields. */
    public double getSubtotal() {
        return unitPrice * quantity;
    }
}
