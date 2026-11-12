package com.retail.store.service;

import com.retail.store.model.Coupon;
import com.retail.store.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CouponService {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public String generate() {
        java.util.Random random = new java.util.Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public Coupon create(int percent) {
        Coupon coupon = new Coupon(generate(), percent);
        couponRepository.save(coupon);
        return coupon;
    }

    public Optional<Coupon> find(String code) {
        return couponRepository.findByCode(code);
    }
}
