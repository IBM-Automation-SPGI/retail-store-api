package com.retail.store.service;

import com.retail.store.model.CartItem;
import com.retail.store.model.Coupon;
import com.retail.store.model.Order;
import com.retail.store.model.OrderItem;
import com.retail.store.payment.PaymentClient;
import com.retail.store.repository.CartRepository;
import com.retail.store.repository.CustomerRepository;
import com.retail.store.repository.OrderRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger AUDIT = LogManager.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final CouponService couponService;
    private final ReceiptService receiptService;
    private final PaymentClient paymentClient;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository,
                        CustomerRepository customerRepository, CouponService couponService,
                        ReceiptService receiptService, PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.couponService = couponService;
        this.receiptService = receiptService;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public Order checkout(long customerId, String couponCode, String note) {
        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        List<CartItem> cart = cartRepository.findByCustomer(customerId);
        if (cart.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        BigDecimal subtotal = cart.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = BigDecimal.ZERO;
        String appliedCoupon = null;
        if (couponCode != null && !couponCode.isEmpty()) {
            Coupon coupon = couponService.find(couponCode)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown coupon"));
            discount = subtotal.multiply(BigDecimal.valueOf(coupon.getPercent()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            appliedCoupon = coupon.getCode();
        }
        BigDecimal total = subtotal.subtract(discount);

        paymentClient.authorise(customerId, total);

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setTotal(total);
        order.setCouponCode(appliedCoupon);
        order.setNote(note);
        order.setCreatedAt(LocalDateTime.now());
        order.setItems(cart.stream()
                .map(c -> new OrderItem(c.getProductId(), c.getProductName(), c.getQuantity(), c.getUnitPrice()))
                .collect(Collectors.toList()));

        order.setId(orderRepository.save(order));
        cartRepository.clear(customerId);

        AUDIT.info("Order {} placed for customer {} total={} coupon={} note={}",
                order.getId(), customerId, total, couponCode, note);

        receiptService.render(order);
        return order;
    }

    public Order get(long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    public List<Order> forCustomer(long customerId) {
        return orderRepository.findByCustomer(customerId);
    }
}
