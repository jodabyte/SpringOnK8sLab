package de.jodabyte.springonk8slab.productservice.domain.exception;

import java.text.MessageFormat;

public class InvalidSkuException extends RuntimeException {

    private static final String MESSAGE = "Invalid sku {0}.";

    private InvalidSkuException(String message) {
        super(message);
    }

    public static InvalidSkuException of(String sku) {
        return new InvalidSkuException(MessageFormat.format(MESSAGE, sku));
    }
}
