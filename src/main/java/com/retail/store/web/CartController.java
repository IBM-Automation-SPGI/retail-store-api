package com.retail.store.web;

import com.retail.store.model.CartItem;
import com.retail.store.model.CartItemRequest;
import com.retail.store.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{customerId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public List<CartItem> addItem(@PathVariable long customerId, @Valid @RequestBody CartItemRequest request) {
        return cartService.addItem(customerId, request.getProductId(), request.getQuantity());
    }

    @GetMapping("/{customerId}")
    public List<CartItem> get(@PathVariable long customerId) {
        return cartService.getCart(customerId);
    }
}
