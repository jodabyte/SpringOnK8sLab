package de.jodabyte.springonk8slab.productservice.domain.repository;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

import de.jodabyte.springonk8slab.productservice.domain.model.Allocation;

public interface AllocationRepository extends ListCrudRepository<Allocation, Long> {

    public List<Allocation> findByOrderId(String orderId);

    public int deleteByOrderIdAndSku(String orderId, String sku);

}
