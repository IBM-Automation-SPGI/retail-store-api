package com.retail.store.web;

import com.retail.store.model.Order;
import com.retail.store.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/api/orders/{id}")
    public Order get(@PathVariable long id) {
        return orderService.get(id);
    }

    @GetMapping("/api/customers/{id}/orders")
    public List<Order> forCustomer(@PathVariable long id) {
        return orderService.forCustomer(id);
    }
}
