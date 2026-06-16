package com.grocerypos.model;

import java.math.BigDecimal;
import javafx.beans.property.*;

public class CartItem {
    private final StringProperty name;
    private final IntegerProperty quantity;
    private final ObjectProperty<BigDecimal> unitPrice;
    private final ObjectProperty<BigDecimal> lineTotal;

    public CartItem(Product product, int qty) {
        this.name = new SimpleStringProperty(product.getName());
        this.quantity = new SimpleIntegerProperty(qty);
        this.unitPrice = new SimpleObjectProperty<>(product.getUnitPrice());
        this.lineTotal = new SimpleObjectProperty<>(product.getUnitPrice().multiply(BigDecimal.valueOf(qty)));
    }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public int getQuantity() { return quantity.get(); }
    public IntegerProperty quantityProperty() { return quantity; }
    public void setQuantity(int qty) {
        this.quantity.set(qty);
        this.lineTotal.set(getUnitPrice().multiply(BigDecimal.valueOf(qty)));
    }

    public BigDecimal getUnitPrice() { return unitPrice.get(); }
    public ObjectProperty<BigDecimal> unitPriceProperty() { return unitPrice; }

    public BigDecimal getLineTotal() { return lineTotal.get(); }
    public ObjectProperty<BigDecimal> lineTotalProperty() { return lineTotal; }
}