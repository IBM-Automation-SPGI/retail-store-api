package com.retail.store.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PaymentClient {

    @Value("${payment.gateway.url}")
    private String gatewayUrl;

    @Value("${payment.gateway.api-key}")
    private String apiKey;

    public String authorise(long customerId, BigDecimal amount) {
        if (gatewayUrl == null) {
            throw new IllegalStateException("Payment gateway is not configured");
        }
        return "AUTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
