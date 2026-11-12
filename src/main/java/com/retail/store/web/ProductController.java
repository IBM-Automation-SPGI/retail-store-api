package com.retail.store.web;

import com.retail.store.model.Product;
import com.retail.store.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> list() {
        return productService.list();
    }

    @GetMapping("/search")
    public List<Product> search(@RequestParam("q") String q) {
        return productService.search(q);
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable long id) {
        return productService.get(id);
    }
}
