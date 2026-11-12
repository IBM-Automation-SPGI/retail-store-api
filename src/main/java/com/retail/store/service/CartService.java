package com.retail.store.service;

import com.retail.store.model.CartItem;
import com.retail.store.repository.CartRepository;
import com.retail.store.repository.CustomerRepository;
import com.retail.store.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CustomerRepository customerRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> addItem(long customerId, long productId, int quantity) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        cartRepository.addItem(customerId, productId, quantity);
        return cartRepository.findByCustomer(customerId);
    }

    public List<CartItem> getCart(long customerId) {
        return cartRepository.findByCustomer(customerId);
    }
}
