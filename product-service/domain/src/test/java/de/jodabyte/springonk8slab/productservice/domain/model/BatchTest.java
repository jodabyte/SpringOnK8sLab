package de.jodabyte.springonk8slab.productservice.domain.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import de.jodabyte.springonk8slab.productservice.domain.test.TestData;

class BatchTest {

    @Test
    void allocatingToABatchReducesTheAvailableQuantity() {
        String sku = "ANGULAR-DESK";
        Batch batch = TestData.makeBatch("batch-001", sku, 20, null);
        OrderLine line = TestData.makeOrderLine("order-123", sku, 2);

        batch.allocate(line);

        assertEquals(18, batch.getAvailableQuantity());
    }

    @Test
    void canAllocateIfAvailableGreaterThanRequired() {
        String sku = "ELEGANT-LAMP";
        Batch largeBatch = TestData.makeBatch("batch-001", sku, 20, null);
        OrderLine smallLine = TestData.makeOrderLine("order-123", sku, 2);

        assertTrue(largeBatch.canAllocate(smallLine));
    }

    @Test
    void cannotAllocateIfAvailableSmallerThanRequired() {
        String sku = "ELEGANT-LAMP";
        Batch smallBatch = TestData.makeBatch("batch-001", sku, 2, null);
        OrderLine largeLine = TestData.makeOrderLine("order-123", sku, 20);

        assertFalse(smallBatch.canAllocate(largeLine));
    }

    @Test
    void canAllocateIfAvailableEqualToRequired() {
        String sku = "ELEGANT-LAMP";
        Batch batch = TestData.makeBatch("batch-001", sku, 2, null);
        OrderLine line = TestData.makeOrderLine("order-123", sku, 2);

        assertTrue(batch.canAllocate(line));
    }

    @Test
    void cannotAllocateIfSkusDoNotMatch() {
        Batch batch = TestData.makeBatch("batch-001", "UNCOMFORTABLE-CHAIR", 100, null);
        OrderLine differentSkuLine = TestData.makeOrderLine("order-123", "EXPENSIVE-TOASTER", 10);

        assertFalse(batch.canAllocate(differentSkuLine));
    }

    @Test
    void allocationIsIdempotent() {
        String sku = "ANGULAR-DESK";
        Batch batch = TestData.makeBatch("batch-001", sku, 20, null);
        OrderLine line = TestData.makeOrderLine("order-123", sku, 2);

        batch.allocate(line);
        batch.allocate(line);

        assertEquals(18, batch.getAvailableQuantity());
    }
}
