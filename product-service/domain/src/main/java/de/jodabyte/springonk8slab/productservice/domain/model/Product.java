package de.jodabyte.springonk8slab.productservice.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import de.jodabyte.springonk8slab.productservice.domain.exception.OutOfStockException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Product {

    @Id
    private String sku;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Batch> batches = new ArrayList<>();
    @Column(nullable = false)
    @ColumnDefault("1")
    private int version;

    public void addBatch(Batch batch) {
        batch.setProduct(this);
        this.getBatches().add(batch);
        Collections.sort(this.getBatches());
    }

    public void increaseVersion() {
        this.version++;
    }

    public String allocate(OrderLine orderLine) {
        Batch batch = this.getBatches().stream()
                .filter(b -> b.canAllocate(orderLine))
                .findFirst()
                .orElseThrow(() -> OutOfStockException.of(orderLine.getSku()));
        batch.allocate(orderLine);
        this.increaseVersion();
        return batch.getReference();
    }
}