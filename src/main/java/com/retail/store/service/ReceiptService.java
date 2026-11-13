package com.retail.store.service;

import com.retail.store.model.Order;
import com.retail.store.model.OrderItem;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Service
public class ReceiptService {

    private static final String FOOTER = "Printed ${date:yyyy-MM-dd HH:mm}";

    @Value("${store.receipts.dir:./receipts}")
    private String receiptsDir;

    public String render(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("RETAIL STORE RECEIPT\n");
        sb.append("Order: ").append(order.getId()).append('\n');
        sb.append("Customer: ").append(order.getCustomerId()).append('\n');
        sb.append("Date: ").append(order.getCreatedAt()).append("\n\n");
        for (OrderItem item : order.getItems()) {
            sb.append(String.format("%-32s %3d x %8s%n", item.getName(), item.getQuantity(), item.getUnitPrice()));
        }
        sb.append('\n');
        sb.append("Subtotal: ").append(order.getSubtotal()).append('\n');
        sb.append("Discount: ").append(order.getDiscount()).append('\n');
        sb.append("Total:    ").append(order.getTotal()).append('\n');
        if (order.getNote() != null && !order.getNote().isEmpty()) {
            sb.append("\nNote: ").append(order.getNote()).append('\n');
        }
        // Interpolate only the constant footer; order data (e.g. the customer note) must stay literal.
        sb.append('\n').append(StringSubstitutor.createInterpolator().replace(FOOTER)).append('\n');
        String text = sb.toString();

        write(order.getId(), text);
        return text;
    }

    private void write(long orderId, String text) {
        File dir = new File(receiptsDir);
        dir.mkdirs();
        File target = new File(dir, "receipt-" + orderId + ".txt");
        try {
            Files.write(target.toPath(), text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public File locate(String file) {
        return new File(receiptsDir, file);
    }
}
