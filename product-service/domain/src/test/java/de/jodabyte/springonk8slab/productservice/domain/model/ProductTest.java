package de.jodabyte.springonk8slab.productservice.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.jodabyte.springonk8slab.productservice.domain.exception.OutOfStockException;
import de.jodabyte.springonk8slab.productservice.domain.test.TestData;

class ProductTest {

    @Test
    void prefersWarehouseBatchesToShipments() {
        String sku = "RETRO-CLOCK";
        Product product = TestData.makeProduct(sku);
        Batch inStockBatch = TestData.makeBatch("in-stock-batch", product, 100, null);
        Batch shipmentBatch = TestData.makeBatch("shipment-batch", product, 100, TestData.TOMORROW);
        product.addBatch(inStockBatch);
        product.addBatch(shipmentBatch);
        OrderLine line = TestData.makeOrderLine("order-123", sku, 10);

        product.allocate(line);

        assertEquals(90, inStockBatch.getAvailableQuantity());
        assertEquals(100, shipmentBatch.getAvailableQuantity());
    }

    @Test
    void prefersEarlierBatches() {
        String sku = "MINIMALIST-SPOON";
        Product product = TestData.makeProduct(sku);
        Batch earliest = TestData.makeBatch("speedy-batch", product, 100, TestData.TODAY);
        Batch medium = TestData.makeBatch("normal-batch", product, 100, TestData.TOMORROW);
        Batch latest = TestData.makeBatch("slow-batch", product, 100, TestData.LATER);
        product.addBatch(latest);
        product.addBatch(medium);
        product.addBatch(earliest);
        OrderLine line = TestData.makeOrderLine("order-123", sku, 10);

        product.allocate(line);

        assertEquals(90, earliest.getAvailableQuantity());
        assertEquals(100, medium.getAvailableQuantity());
        assertEquals(100, latest.getAvailableQuantity());
    }

    @Test
    void returnsAllocatedBatchRef() {
        String sku = "HIGHBROW-POSTER";
        Product product = TestData.makeProduct(sku);
        Batch inStockBatch = TestData.makeBatch("in-stock-batch-ref", product, 100, null);
        Batch shipmentBatch = TestData.makeBatch("shipment-batch-ref", product, 100, TestData.TOMORROW);
        product.addBatch(inStockBatch);
        product.addBatch(shipmentBatch);
        OrderLine line = TestData.makeOrderLine("order-123", sku, 10);

        String batchReference = product.allocate(line);

        assertEquals(inStockBatch.getReference(), batchReference);
    }

    @Test
    void recordsOutOfStockEventIfCannotAllocate() {
        String sku = "SMALL-FORK";
        Product product = TestData.makeProduct(sku);
        Batch batch = TestData.makeBatch("batch-001", product, 10, null);
        product.addBatch(batch);

        OutOfStockException exception = assertThrows(OutOfStockException.class, () -> {
            product.allocate(TestData.makeOrderLine("order1", sku, 10));
            product.allocate(TestData.makeOrderLine("order2", sku, 1));
        });

        assertEquals("Out of stock for sku SMALL-FORK.", exception.getMessage());
    }

    @Test
    void incrementsVersionNumber() {
        String sku = "SCANDI-PEN";
        Product product = TestData.makeProduct(sku);
        Batch batch = TestData.makeBatch("batch-001", product, 100, null);
        product.addBatch(batch);
        product.setVersion(7);
        OrderLine line = TestData.makeOrderLine("order-123", sku, 10);

        product.allocate(line);

        assertEquals(8, product.getVersion());
    }
}
