package de.jodabyte.springonk8slab.productservice.domain.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.jodabyte.springonk8slab.productservice.domain.command.Allocate;
import de.jodabyte.springonk8slab.productservice.domain.command.ChangeBatchQuantity;
import de.jodabyte.springonk8slab.productservice.domain.command.CreateBatch;
import de.jodabyte.springonk8slab.productservice.domain.event.Allocated;
import de.jodabyte.springonk8slab.productservice.domain.event.Deallocated;
import de.jodabyte.springonk8slab.productservice.domain.exception.InvalidBatchReferenceException;
import de.jodabyte.springonk8slab.productservice.domain.exception.InvalidSkuException;
import de.jodabyte.springonk8slab.productservice.domain.mapper.AllocationMapper;
import de.jodabyte.springonk8slab.productservice.domain.mapper.BatchMapper;
import de.jodabyte.springonk8slab.productservice.domain.mapper.EventAndCommandMapper;
import de.jodabyte.springonk8slab.productservice.domain.mapper.OrderLineMapper;
import de.jodabyte.springonk8slab.productservice.domain.model.Allocation;
import de.jodabyte.springonk8slab.productservice.domain.model.Batch;
import de.jodabyte.springonk8slab.productservice.domain.model.OrderLine;
import de.jodabyte.springonk8slab.productservice.domain.model.Product;
import de.jodabyte.springonk8slab.productservice.domain.repository.AllocationRepository;
import de.jodabyte.springonk8slab.productservice.domain.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductService {

    private OrderLineMapper orderLineMapper;
    private AllocationMapper allocationMapper;
    private BatchMapper batchMapper;
    private EventAndCommandMapper eventAndCommandMapper;

    private ProductRepository productRepository;
    private AllocationRepository allocationRepository;

    public ProductService(OrderLineMapper orderLineMapper,
            AllocationMapper allocationMapper,
            BatchMapper batchMapper,
            EventAndCommandMapper eventAndCommandMapper,
            ProductRepository productRepository,
            AllocationRepository allocationRepository) {
        this.orderLineMapper = orderLineMapper;
        this.allocationMapper = allocationMapper;
        this.batchMapper = batchMapper;
        this.eventAndCommandMapper = eventAndCommandMapper;
        this.productRepository = productRepository;
        this.allocationRepository = allocationRepository;
    }

    @Transactional
    public Allocated allocate(Allocate allocate) {
        OrderLine orderLine = this.orderLineMapper.from(allocate);
        Product product = this.productRepository.findBySku(orderLine.getSku())
                .orElseThrow(() -> InvalidSkuException.of(orderLine.getSku()));

        String batchReference = product.allocate(orderLine);
        Allocated allocated = new Allocated(orderLine.getOrderId(),
                orderLine.getSku(),
                orderLine.getQuantity(),
                batchReference);

        this.productRepository.save(product);
        this.allocationRepository.save(this.allocationMapper.from(allocated));

        log.info("Allocated order-id {} to batch {}", orderLine.getOrderId(), batchReference);
        return allocated;
    }

    public List<de.jodabyte.springonk8slab.productservice.domain.view.Allocation> getAllocations(String orderId) {
        List<Allocation> allocations = this.allocationRepository.findByOrderId(orderId);
        if (allocations.isEmpty()) {
            throw new NoSuchElementException("No allocations found for orderId: " + orderId);
        }
        return this.allocationMapper.to(allocations);
    }

    @Transactional
    public void addBatch(CreateBatch createBatch) {
        Product product = this.productRepository.findBySku(createBatch.sku())
                .orElseGet(() -> {
                    Product p = new Product();
                    p.setSku(createBatch.sku());
                    p = this.productRepository.save(p);
                    log.info("Created new product with sku {}", p.getSku());
                    return p;
                });

        product.addBatch(this.batchMapper.from(createBatch));
        this.productRepository.save(product);
        log.info("Added batch {} to product {}", createBatch.reference(), product.getSku());
    }

    @Transactional
    public void changeBatchQuantity(ChangeBatchQuantity changeBatchQuantity) {
        Product product = this.productRepository.findByBatches_Reference(changeBatchQuantity.batchRef())
                .orElseThrow(() -> InvalidBatchReferenceException.of(changeBatchQuantity.batchRef()));

        Batch batch = product.getBatches()
                .stream()
                .filter(b -> b.getReference().equals(changeBatchQuantity.batchRef()))
                .findFirst()
                .orElseThrow(() -> InvalidBatchReferenceException.of(changeBatchQuantity.batchRef()));

        batch.setPurchasedQuantity(changeBatchQuantity.qty());
        this.productRepository.save(product);
        log.info("Changed batch {} quantity to {}", batch.getReference(), batch.getPurchasedQuantity());

        while (batch.getAvailableQuantity() < 0) {
            OrderLine OrderLineToReAllocate = batch.getAllocations().iterator().next();
            batch.getAllocations().remove(OrderLineToReAllocate);
            log.info("Deallocating order-id {} from batch {}", OrderLineToReAllocate.getOrderId(),
                    batch.getReference());
            this.reAllocate(this.orderLineMapper.to(OrderLineToReAllocate));
        }
    }

    public void reAllocate(Deallocated deallocated) {
        int count = this.allocationRepository.deleteByOrderIdAndSku(deallocated.orderId(), deallocated.sku());
        if (count == 0) {
            log.info("No allocation found for order-id {} and sku {}", deallocated.orderId(), deallocated.sku());
        } else {
            log.info("Deleted {} allocations for order-id {} and sku {}", count, deallocated.orderId(),
                    deallocated.sku());
        }
        log.info("Reallocating order-id {} for sku {}", deallocated.orderId(), deallocated.sku());
        this.allocate(this.eventAndCommandMapper.toCommand(deallocated));
    }
}
