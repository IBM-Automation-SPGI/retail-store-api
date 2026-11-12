package com.retail.store.repository;

import com.retail.store.model.Coupon;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CouponRepository {

    private final JdbcTemplate jdbcTemplate;

    public CouponRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Coupon coupon) {
        jdbcTemplate.update("INSERT INTO coupons (code, percent) VALUES (?, ?)", coupon.getCode(), coupon.getPercent());
    }

    public Optional<Coupon> findByCode(String code) {
        return jdbcTemplate.query("SELECT code, percent FROM coupons WHERE code = ?",
                (rs, rowNum) -> new Coupon(rs.getString("code"), rs.getInt("percent")),
                code).stream().findFirst();
    }
}
