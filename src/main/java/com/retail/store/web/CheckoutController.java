package com.retail.store.web;

import com.retail.store.model.CheckoutRequest;
import com.retail.store.model.Order;
import com.retail.store.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final OrderService orderService;

    public CheckoutController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{customerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Order checkout(@PathVariable long customerId,
                          @RequestBody(required = false) CheckoutRequest request) {
        CheckoutRequest body = request != null ? request : new CheckoutRequest();
        return orderService.checkout(customerId, body.getCouponCode(), body.getNote());
    }
}
