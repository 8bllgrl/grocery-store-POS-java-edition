package com.grocerypos.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class Product {
    private final String barcode;
    private final String name;
    private final BigDecimal unitPrice;

    // Direct binding annotations keep your class safe and immutable
    @JsonCreator
    public Product(
            @JsonProperty("barcode") String barcode,
            @JsonProperty("name") String name,
            @JsonProperty("unitPrice") BigDecimal unitPrice) {
        this.barcode = barcode;
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public String getBarcode() { return barcode; }
    public String getName() { return name; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}
