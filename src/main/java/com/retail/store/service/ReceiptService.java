package com.retail.store.service;

import com.retail.store.model.Order;
import com.retail.store.model.OrderItem;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@Service
public class ReceiptService {

    private static final String FOOTER = "Printed ${date:yyyy-MM-dd HH:mm}";
    private static final Pattern RECEIPT_NAME = Pattern.compile("receipt-\\d+\\.txt");

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
        sb.append('\n').append(FOOTER).append('\n');

        StringSubstitutor substitutor = StringSubstitutor.createInterpolator();
        String text = substitutor.replace(sb.toString());

        write(order.getId(), text);
        return text;
    }

    private void write(long orderId, String text) {
        try {
            Path base = baseDir();
            Files.createDirectories(base);
            Path target = resolveInside(base, "receipt-" + orderId + ".txt");
            Files.write(target, text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public File locate(String file) {
        if (file == null || !RECEIPT_NAME.matcher(file).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid receipt name");
        }
        return resolveInside(baseDir(), file).toFile();
    }

    private Path baseDir() {
        return Paths.get(receiptsDir).toAbsolutePath().normalize();
    }

    private static Path resolveInside(Path base, String name) {
        Path resolved = base.resolve(name).normalize();
        if (!resolved.startsWith(base)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid receipt name");
        }
        return resolved;
    }
}
