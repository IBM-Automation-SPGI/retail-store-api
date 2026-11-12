package com.retail.store;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CheckoutFlowTest {

    private static Path receiptsDir;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) throws Exception {
        receiptsDir = Files.createTempDirectory("receipts");
        registry.add("store.receipts.dir", () -> receiptsDir.toString());
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private void addToCart(int customerId, int productId, int quantity) throws Exception {
        mvc.perform(post("/api/cart/" + customerId + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":" + productId + ",\"quantity\":" + quantity + "}"))
                .andExpect(status().isCreated());
    }

    @Test
    void addsItemsToCart() throws Exception {
        addToCart(2, 5, 2);
        mvc.perform(get("/api/cart/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].quantity").value(2));
        mvc.perform(post("/api/checkout/2")).andExpect(status().isCreated());
    }

    @Test
    void checkoutWithoutCoupon() throws Exception {
        addToCart(1, 1, 2);
        addToCart(1, 5, 1);

        mvc.perform(post("/api/checkout/1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subtotal").value(41.5))
                .andExpect(jsonPath("$.discount").value(0))
                .andExpect(jsonPath("$.total").value(41.5));

        mvc.perform(get("/api/cart/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void checkoutWithCoupon() throws Exception {
        addToCart(2, 1, 2);

        mvc.perform(post("/api/checkout/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"couponCode\":\"WELCOME10\",\"note\":\"Leave at the door\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subtotal").value(37.0))
                .andExpect(jsonPath("$.discount").value(3.7))
                .andExpect(jsonPath("$.total").value(33.3))
                .andExpect(jsonPath("$.couponCode").value("WELCOME10"));
    }

    @Test
    void createdCouponCanBeUsedAtCheckout() throws Exception {
        MvcResult created = mvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"percent\":20}"))
                .andExpect(status().isCreated())
                .andReturn();
        String code = mapper.readTree(created.getResponse().getContentAsString()).get("code").asText();

        addToCart(3, 2, 1);
        mvc.perform(post("/api/checkout/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"couponCode\":\"" + code + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.discount").value(1.98));
    }

    @Test
    void receiptIsWrittenAndDownloadable() throws Exception {
        addToCart(3, 3, 1);

        MvcResult result = mvc.perform(post("/api/checkout/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":\"Gift wrap please\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode order = mapper.readTree(result.getResponse().getContentAsString());
        long orderId = order.get("id").asLong();
        String fileName = "receipt-" + orderId + ".txt";

        Path written = receiptsDir.resolve(fileName);
        assertTrue(Files.exists(written));
        String text = Files.readString(written);
        assertTrue(text.contains("Green Tea 100 bags"));
        assertTrue(text.contains("Gift wrap please"));

        mvc.perform(get("/api/receipts/download").param("file", fileName))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Order: " + orderId)));

        mvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)));
        MvcResult list = mvc.perform(get("/api/customers/3/orders")).andExpect(status().isOk()).andReturn();
        assertTrue(mapper.readTree(list.getResponse().getContentAsString()).size() >= 1);
        assertEquals(order.get("customerId").asLong(), 3L);
    }

    @Test
    void missingReceiptIsNotFound() throws Exception {
        mvc.perform(get("/api/receipts/download").param("file", "receipt-0.txt"))
                .andExpect(status().isNotFound());
    }
}
