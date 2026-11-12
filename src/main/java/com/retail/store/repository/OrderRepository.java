package com.retail.store.repository;

import com.retail.store.model.Order;
import com.retail.store.model.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    private static final RowMapper<Order> ORDER_MAPPER = (rs, rowNum) -> {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setCustomerId(rs.getLong("customer_id"));
        order.setSubtotal(rs.getBigDecimal("subtotal"));
        order.setDiscount(rs.getBigDecimal("discount"));
        order.setTotal(rs.getBigDecimal("total"));
        order.setCouponCode(rs.getString("coupon_code"));
        order.setNote(rs.getString("note"));
        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return order;
    };

    private static final RowMapper<OrderItem> ITEM_MAPPER = (rs, rowNum) -> new OrderItem(
            rs.getLong("product_id"),
            rs.getString("name"),
            rs.getInt("quantity"),
            rs.getBigDecimal("unit_price"));

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Order order) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO orders (customer_id, subtotal, discount, total, coupon_code, note, created_at) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, order.getCustomerId());
            ps.setBigDecimal(2, order.getSubtotal());
            ps.setBigDecimal(3, order.getDiscount());
            ps.setBigDecimal(4, order.getTotal());
            ps.setString(5, order.getCouponCode());
            ps.setString(6, order.getNote());
            ps.setTimestamp(7, Timestamp.valueOf(order.getCreatedAt()));
            return ps;
        }, keys);
        long orderId = ((Number) keys.getKeys().get("ID")).longValue();
        for (OrderItem item : order.getItems()) {
            jdbcTemplate.update(
                    "INSERT INTO order_items (order_id, product_id, name, quantity, unit_price) VALUES (?, ?, ?, ?, ?)",
                    orderId, item.getProductId(), item.getName(), item.getQuantity(), item.getUnitPrice());
        }
        return orderId;
    }

    public Optional<Order> findById(long id) {
        Optional<Order> found = jdbcTemplate.query(
                "SELECT id, customer_id, subtotal, discount, total, coupon_code, note, created_at "
                        + "FROM orders WHERE id = ?", ORDER_MAPPER, id).stream().findFirst();
        found.ifPresent(o -> o.setItems(loadItems(o.getId())));
        return found;
    }

    public List<Order> findByCustomer(long customerId) {
        List<Order> orders = jdbcTemplate.query(
                "SELECT id, customer_id, subtotal, discount, total, coupon_code, note, created_at "
                        + "FROM orders WHERE customer_id = ? ORDER BY id", ORDER_MAPPER, customerId);
        orders.forEach(o -> o.setItems(loadItems(o.getId())));
        return orders;
    }

    private List<OrderItem> loadItems(long orderId) {
        return jdbcTemplate.query(
                "SELECT product_id, name, quantity, unit_price FROM order_items WHERE order_id = ? ORDER BY id",
                ITEM_MAPPER, orderId);
    }
}
