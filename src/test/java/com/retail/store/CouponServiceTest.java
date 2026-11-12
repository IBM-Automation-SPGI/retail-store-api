package com.retail.store;

import com.retail.store.service.CouponService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CouponServiceTest {

    @Autowired
    private CouponService couponService;

    @Test
    void generatesEightCharacterAlphanumericCode() {
        for (int i = 0; i < 50; i++) {
            String code = couponService.generate();
            assertEquals(8, code.length());
            assertTrue(code.matches("[A-Z0-9]{8}"), code);
        }
    }
}
