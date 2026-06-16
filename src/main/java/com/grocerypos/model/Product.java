package com.grocerypos.model;

import java.math.BigDecimal;

public class Product {
    private final String barcode;
    private final String name;
    private final BigDecimal unitPrice;

    public Product(String barcode, String name, BigDecimal unitPrice) {
        this.barcode = barcode;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public String getBarcode() { return barcode; }
    public String getName() { return name; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}