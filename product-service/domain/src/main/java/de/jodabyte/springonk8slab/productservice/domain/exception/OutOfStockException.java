package de.jodabyte.springonk8slab.productservice.domain.exception;

import java.text.MessageFormat;

import lombok.Getter;

public class OutOfStockException extends RuntimeException {

    private static final String MESSAGE = "Out of stock for sku {0}.";

    @Getter
    private String sku;

    private OutOfStockException(String message, String sku) {
        super(message);
        this.sku = sku;
    }

    public static OutOfStockException of(String sku) {
        return new OutOfStockException(MessageFormat.format(MESSAGE, sku), sku);
    }
}
