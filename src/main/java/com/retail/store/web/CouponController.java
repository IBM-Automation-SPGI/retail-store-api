package com.retail.store.web;

import com.retail.store.model.Coupon;
import com.retail.store.model.CouponRequest;
import com.retail.store.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Coupon create(@Valid @RequestBody(required = false) CouponRequest request) {
        CouponRequest body = request != null ? request : new CouponRequest();
        return couponService.create(body.getPercent());
    }
}
