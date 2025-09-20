package de.jodabyte.springonk8slab.productservice.domain.repository;

import java.util.Optional;

import org.springframework.data.repository.ListCrudRepository;

import de.jodabyte.springonk8slab.productservice.domain.model.Product;

public interface ProductRepository extends ListCrudRepository<Product, String> {

    Optional<Product> findBySku(String sku);

    Optional<Product> findByBatches_Reference(String batchRef);

}
