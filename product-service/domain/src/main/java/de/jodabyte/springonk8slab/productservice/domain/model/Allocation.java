package de.jodabyte.springonk8slab.productservice.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Allocation {

    @Id
    @GeneratedValue
    private Long id;
    private String orderId;
    private String sku;
    private String batchRef;

}
