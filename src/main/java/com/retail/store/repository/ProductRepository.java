package com.retail.store.repository;

import com.retail.store.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    private static final RowMapper<Product> MAPPER = (rs, rowNum) -> new Product(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getBigDecimal("price"),
            rs.getInt("stock"));

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Product> findAll() {
        return jdbcTemplate.query("SELECT id, name, description, price, stock FROM products ORDER BY id", MAPPER);
    }

    public Optional<Product> findById(long id) {
        List<Product> result = jdbcTemplate.query(
                "SELECT id, name, description, price, stock FROM products WHERE id = ?", MAPPER, id);
        return result.stream().findFirst();
    }

    public List<Product> search(String q) {
        String sql = "SELECT id, name, description, price, stock FROM products WHERE LOWER(name) LIKE '%"
                + q.toLowerCase() + "%'";
        return jdbcTemplate.query(sql, MAPPER);
    }
}
