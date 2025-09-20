package de.jodabyte.springonk8slab.productservice.controller;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import de.jodabyte.springonk8slab.productservice.domain.event.OutOfStock;
import de.jodabyte.springonk8slab.productservice.domain.exception.InvalidBatchReferenceException;
import de.jodabyte.springonk8slab.productservice.domain.exception.InvalidSkuException;
import de.jodabyte.springonk8slab.productservice.domain.exception.OutOfStockException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidSkuException.class)
    public ResponseEntity<String> handleInvalidSku(InvalidSkuException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidBatchReferenceException.class)
    public ResponseEntity<String> handleInvalidBatchReference(InvalidBatchReferenceException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<OutOfStock> handleOutOfStock(OutOfStockException ex) {
        return new ResponseEntity<>(new OutOfStock(ex.getSku()), HttpStatus.OK);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<NoSuchElementException> handleNoSuchElementException(NoSuchElementException ex) {
        return new ResponseEntity<>(ex, HttpStatus.NOT_FOUND);
    }
}
