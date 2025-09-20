package de.jodabyte.springonk8slab.productservice.domain.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Batch implements Comparable<Batch> {

    @Id
    @GeneratedValue
    private long id;
    @Column(nullable = false)
    private String reference;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Product product;
    @Column(nullable = false)
    private int purchasedQuantity;
    private LocalDate eta;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "allocations", joinColumns = @JoinColumn(name = "batchId"), inverseJoinColumns = @JoinColumn(name = "orderLineId"))
    private Set<OrderLine> allocations = new HashSet<>();

    @Override
    public int compareTo(Batch o) {
        if (this.getEta() == null && o.getEta() == null) {
            return 0;
        } else if (this.getEta() == null) {
            return -1;
        } else if (o.getEta() == null) {
            return 1;
        } else {
            return this.getEta().compareTo(o.getEta());
        }
    }

    public int getAllocatedQuantity() {
        return allocations.stream().mapToInt(OrderLine::getQuantity).sum();
    }

    public int getAvailableQuantity() {
        return this.purchasedQuantity - getAllocatedQuantity();
    }

    public void allocate(OrderLine orderLine) {
        if (this.canAllocate(orderLine)) {
            this.allocations.add(orderLine);
        }
    }

    public boolean canAllocate(OrderLine orderLine) {
        return this.product.getSku().equals(orderLine.getSku()) && getAvailableQuantity() >= orderLine.getQuantity();
    }

}
