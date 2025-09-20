package de.jodabyte.springonk8slab.productservice.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import de.jodabyte.springonk8slab.productservice.domain.command.Allocate;
import de.jodabyte.springonk8slab.productservice.domain.command.ChangeBatchQuantity;
import de.jodabyte.springonk8slab.productservice.domain.command.CreateBatch;
import de.jodabyte.springonk8slab.productservice.domain.event.Allocated;
import de.jodabyte.springonk8slab.productservice.domain.exception.InvalidSkuException;
import de.jodabyte.springonk8slab.productservice.domain.exception.OutOfStockException;
import de.jodabyte.springonk8slab.productservice.domain.mapper.AllocationMapperImpl;
import de.jodabyte.springonk8slab.productservice.domain.mapper.BatchMapperImpl;
import de.jodabyte.springonk8slab.productservice.domain.mapper.EventAndCommandMapperImpl;
import de.jodabyte.springonk8slab.productservice.domain.mapper.OrderLineMapperImpl;
import de.jodabyte.springonk8slab.productservice.domain.model.Allocation;
import de.jodabyte.springonk8slab.productservice.domain.model.Batch;
import de.jodabyte.springonk8slab.productservice.domain.model.Product;
import de.jodabyte.springonk8slab.productservice.domain.repository.AllocationRepository;
import de.jodabyte.springonk8slab.productservice.domain.repository.ProductRepository;
import de.jodabyte.springonk8slab.productservice.domain.test.TestData;

@SpringBootTest(classes = { AllocationMapperImpl.class, BatchMapperImpl.class, EventAndCommandMapperImpl.class,
        OrderLineMapperImpl.class })
@SpringBootApplication(scanBasePackages = { "de.jodabyte.springonk8slab.productservice.domain" })
@EnableJpaRepositories(basePackages = { "de.jodabyte.springonk8slab.productservice.domain.repository" })
@EntityScan(basePackages = { "de.jodabyte.springonk8slab.productservice.domain.model" })
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AllocationRepository allocationRepository;

    @Autowired
    private ProductService sut;

    @Test
    void outputsAllocatedEvent() {
        String sku = "RETRO-LAMPSHADE";
        Allocate allocate = new Allocate("order-123", sku, 10);
        Product product = TestData.makeProduct(sku);
        Batch batch = TestData.makeBatch("batch-001", product, 100, null);
        product.addBatch(batch);
        this.productRepository.save(product);

        Allocated allocated = sut.allocate(allocate);

        assertEquals(allocate.orderId(), allocated.orderId());
        assertEquals(allocate.sku(), allocated.sku());
        assertEquals(allocate.quantity(), allocated.qty());
        assertEquals(batch.getReference(), allocated.batchRef());
    }

    @Test
    void testForNewProduct() {
        CreateBatch batch = new CreateBatch("b1", "CRUNCHY-ARMCHAIR", 100, null);

        this.sut.addBatch(batch);

        Optional<Product> product = this.productRepository.findBySku(batch.sku());
        assertTrue(product.isPresent());
    }

    @Test
    void testForExistingProduct() {
        String sku = "GARISH-RUG";
        CreateBatch b1 = new CreateBatch("b1", sku, 100, null);
        CreateBatch b2 = new CreateBatch("b2", sku, 99, null);

        this.sut.addBatch(b1);
        this.sut.addBatch(b2);

        List<Batch> batches = this.productRepository.findBySku(sku).orElseThrow().getBatches();
        assertEquals(2, batches.size());
        assertThat(batches).flatExtracting(Batch::getReference).contains(b1.reference());
    }

    @Test
    void testAllocates() {
        String sku = "COMPLICATED-LAMP";

        this.sut.addBatch(new CreateBatch("batch1", sku, 100, null));
        this.sut.allocate(new Allocate("o1", sku, 10));

        List<Batch> batches = this.productRepository.findBySku(sku).orElseThrow().getBatches();
        assertEquals(1, batches.size());
        assertEquals(90, batches.getFirst().getAvailableQuantity());
    }

    @Test
    void testErrorsForInvalidSku() {
        this.sut.addBatch(new CreateBatch("b1", "AREALSKU", 100, null));

        assertThrows(InvalidSkuException.class,
                () -> this.sut.allocate(new Allocate("o1", "NONEXISTENTSKU", 10)), "Invalid sku NONEXISTENTSKU.");
    }

    @Test
    void testSendsEmailOnOutOfStockError() {
        String sku = "POPULAR-CURTAINS";

        assertThrows(OutOfStockException.class, () -> {
            this.sut.addBatch(new CreateBatch("b1", sku, 9, null));
            this.sut.allocate(new Allocate("o1", sku, 10));
        }, String.format("Out of stock for sku {0}.", sku));
    }

    @Test
    void testChangesAvailableQuantity() {
        String sku = "ADORABLE-SETTEE";

        this.sut.addBatch(new CreateBatch("batch1", sku, 100, null));
        Batch batchWith100Qty = this.productRepository.findBySku(sku).orElseThrow().getBatches().getFirst();
        assertEquals(100, batchWith100Qty.getAvailableQuantity());

        this.sut.changeBatchQuantity(new ChangeBatchQuantity(batchWith100Qty.getReference(), 50));
        Batch batchWith50Qty = this.productRepository.findBySku(sku).orElseThrow().getBatches().getFirst();
        assertEquals(50, batchWith50Qty.getAvailableQuantity());
    }

    @Test
    void testReallocatesIfNecessary() {
        String sku = "INDIFFERENT-TABLE";
        CreateBatch batch1 = new CreateBatch("batch1", sku, 50, null);
        CreateBatch batch2 = new CreateBatch("batch2", sku, 50, TestData.TODAY);

        this.sut.addBatch(batch1);
        this.sut.addBatch(batch2);
        this.sut.allocate(new Allocate("order1", sku, 20));
        this.sut.allocate(new Allocate("order2", sku, 20));

        List<Batch> batches = this.productRepository.findBySku(sku).orElseThrow().getBatches();
        assertThat(batches).extracting(Batch::getAvailableQuantity).containsExactly(10, 50);

        this.sut.changeBatchQuantity(new ChangeBatchQuantity("batch1", 25));

        List<Batch> batchesAfterReallocation = this.productRepository.findBySku(sku).orElseThrow().getBatches();
        assertThat(batchesAfterReallocation).extracting(Batch::getAvailableQuantity).containsExactly(5, 30);
    }

    @Test
    void testAllocationsView() {
        this.sut.addBatch(new CreateBatch("sku1batch", "sku1", 50, null));
        this.sut.addBatch(new CreateBatch("sku2batch", "sku2", 50, TestData.TODAY));
        this.sut.allocate(new Allocate("order1", "sku1", 20));
        this.sut.allocate(new Allocate("order1", "sku2", 20));
        // # add a spurious batch and order to make sure we're getting the right ones
        this.sut.addBatch(new CreateBatch("sku1batch-later", "sku1", 50, TestData.TODAY));
        this.sut.allocate(new Allocate("otherorder", "sku1", 30));
        this.sut.allocate(new Allocate("otherorder", "sku2", 10));

        List<Allocation> allocations = this.allocationRepository.findByOrderId("order1");
        assertThat(allocations).extracting(Allocation::getBatchRef).containsExactlyInAnyOrder("sku1batch", "sku2batch");
    }

    @Test
    void testDeallocation() {
        this.sut.addBatch(new CreateBatch("b1", "sku1", 50, null));
        this.sut.addBatch(new CreateBatch("b2", "sku1", 50, TestData.TODAY));
        this.sut.allocate(new Allocate("o1", "sku1", 40));
        this.sut.changeBatchQuantity(new ChangeBatchQuantity("b1", 10));

        List<Allocation> allocations = this.allocationRepository.findByOrderId("o1");
        assertThat(allocations.getFirst().getBatchRef()).isEqualTo("b2");
    }
}
