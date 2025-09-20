package de.jodabyte.springonk8slab.productservice.domain.event;

public record Deallocated(String orderId, String sku, int quantity) {

}
