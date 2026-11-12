package com.retail.store.repository;

import com.retail.store.model.CartItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CartRepository {

    private static final RowMapper<CartItem> MAPPER = (rs, rowNum) -> new CartItem(
            rs.getLong("id"),
            rs.getLong("customer_id"),
            rs.getLong("product_id"),
            rs.getString("name"),
            rs.getBigDecimal("price"),
            rs.getInt("quantity"));

    private final JdbcTemplate jdbcTemplate;

    public CartRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addItem(long customerId, long productId, int quantity) {
        int updated = jdbcTemplate.update(
                "UPDATE cart_items SET quantity = quantity + ? WHERE customer_id = ? AND product_id = ?",
                quantity, customerId, productId);
        if (updated == 0) {
            jdbcTemplate.update(
                    "INSERT INTO cart_items (customer_id, product_id, quantity) VALUES (?, ?, ?)",
                    customerId, productId, quantity);
        }
    }

    public List<CartItem> findByCustomer(long customerId) {
        return jdbcTemplate.query(
                "SELECT c.id, c.customer_id, c.product_id, p.name, p.price, c.quantity "
                        + "FROM cart_items c JOIN products p ON p.id = c.product_id "
                        + "WHERE c.customer_id = ? ORDER BY c.id",
                MAPPER, customerId);
    }

    public void clear(long customerId) {
        jdbcTemplate.update("DELETE FROM cart_items WHERE customer_id = ?", customerId);
    }
}
