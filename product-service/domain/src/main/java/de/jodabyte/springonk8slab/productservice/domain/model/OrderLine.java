package de.jodabyte.springonk8slab.productservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderLine {

    @Id
    @GeneratedValue
    private long id;
    @Column(nullable = false)
    private String sku;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private String orderId;
}
