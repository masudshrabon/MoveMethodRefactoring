package com.sample.shop;

import java.util.ArrayList;
import java.util.List;

/** An order placed by a customer. */
public class Order implements Shippable {

    private String orderId;
    private Customer customer;
    private List<OrderItem> items = new ArrayList<>();
    private double discountRate;

    public Order(String orderId, Customer customer, double discountRate) {
        this.orderId = orderId;
        this.customer = customer;
        this.discountRate = discountRate;
    }

    public String getOrderId()       { return orderId; }
    public Customer getCustomer()    { return customer; }
    public List<OrderItem> getItems() { return items; }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    /** Well placed: works on the order's items and discount. */
    public double calculateTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total - total * discountRate;
    }

    /** FEATURE ENVY (planted): only uses Customer data -> should move to Customer. */
    @Override
    public String formatShippingLabel() {
        return customer.getName() + "\n"
                + customer.getStreet() + "\n"
                + customer.getCity() + " " + customer.getZipCode();
    }
}
