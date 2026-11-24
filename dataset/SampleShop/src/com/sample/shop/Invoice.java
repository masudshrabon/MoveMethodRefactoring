package com.sample.shop;

/** An invoice for one order. */
public class Invoice {

    private String invoiceNumber;
    private Order order;
    private double taxRate;

    public Invoice(String invoiceNumber, Order order, double taxRate) {
        this.invoiceNumber = invoiceNumber;
        this.order = order;
        this.taxRate = taxRate;
    }

    public String getInvoiceNumber() { return invoiceNumber; }

    /** Well placed: tax is the invoice's responsibility. */
    public double computeTax() {
        return order.calculateTotal() * taxRate;
    }

    /** Well placed: total amount due on this invoice. */
    public double computeAmountDue() {
        return order.calculateTotal() + computeTax();
    }

    /** FEATURE ENVY (planted): only uses Customer contact data -> should move to Customer. */
    public String printCustomerContact() {
        Customer buyer = order.getCustomer();
        return buyer.getName() + " <" + buyer.getEmail() + ">, phone: " + buyer.getPhone();
    }
}
