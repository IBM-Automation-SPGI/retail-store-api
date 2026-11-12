package com.retail.store.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CartItemRequest {

    @NotNull
    private Long productId;

    @Min(1)
    private int quantity = 1;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
