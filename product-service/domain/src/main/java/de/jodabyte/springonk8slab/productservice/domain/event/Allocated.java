package de.jodabyte.springonk8slab.productservice.domain.event;

public record Allocated(String orderId, String sku, int qty, String batchRef) {

}