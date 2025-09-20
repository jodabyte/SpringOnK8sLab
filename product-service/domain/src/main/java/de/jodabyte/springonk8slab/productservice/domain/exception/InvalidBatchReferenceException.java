package de.jodabyte.springonk8slab.productservice.domain.exception;

import java.text.MessageFormat;

public class InvalidBatchReferenceException extends RuntimeException {

    private static final String MESSAGE = "Invalid batchRef {0}.";

    private InvalidBatchReferenceException(String message) {
        super(message);
    }

    public static InvalidBatchReferenceException of(String batchRef) {
        return new InvalidBatchReferenceException(MessageFormat.format(MESSAGE, batchRef));
    }
}
