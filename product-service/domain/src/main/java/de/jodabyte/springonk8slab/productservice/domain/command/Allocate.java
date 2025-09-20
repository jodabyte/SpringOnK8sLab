package de.jodabyte.springonk8slab.productservice.domain.command;

public record Allocate(String orderId, String sku, int quantity) {

}
