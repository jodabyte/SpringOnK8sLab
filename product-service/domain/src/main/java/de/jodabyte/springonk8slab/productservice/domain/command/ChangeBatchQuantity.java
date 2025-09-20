package de.jodabyte.springonk8slab.productservice.domain.command;

public record ChangeBatchQuantity(String batchRef, int qty) {

}
