package com.retail.store.model;

import java.math.BigDecimal;

public class CartItem {

    private Long id;
    private Long customerId;
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private int quantity;

    public CartItem() {
    }

    public CartItem(Long id, Long customerId, Long productId, String productName, BigDecimal unitPrice, int quantity) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
