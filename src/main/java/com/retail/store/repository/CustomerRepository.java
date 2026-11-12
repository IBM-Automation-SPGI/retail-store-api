package com.retail.store.repository;

import com.retail.store.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Customer> findById(long id) {
        return jdbcTemplate.query("SELECT id, name, email FROM customers WHERE id = ?",
                (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("name"), rs.getString("email")),
                id).stream().findFirst();
    }
}
