package com.sample.shop;

/** A customer with contact and address data. */
public class Customer {

    private String name;
    private String email;
    private String phone;
    private String street;
    private String city;
    private String zipCode;

    public Customer(String name, String email, String phone,
                    String street, String city, String zipCode) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
    }

    public String getName()    { return name; }
    public String getEmail()   { return email; }
    public String getPhone()   { return phone; }
    public String getStreet()  { return street; }
    public String getCity()    { return city; }
    public String getZipCode() { return zipCode; }

    /** Well placed: uses only Customer's own fields. */
    public String getFullAddress() {
        return street + ", " + city + " " + zipCode;
    }
}
