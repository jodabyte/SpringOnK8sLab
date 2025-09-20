package de.jodabyte.springonk8slab.productservice.domain.command;

import java.time.LocalDate;

public record CreateBatch(String reference, String sku, int qty, LocalDate eta) {

}
