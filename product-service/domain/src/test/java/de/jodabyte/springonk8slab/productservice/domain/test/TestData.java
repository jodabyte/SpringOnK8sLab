package de.jodabyte.springonk8slab.productservice.domain.test;

import java.time.LocalDate;

import de.jodabyte.springonk8slab.productservice.domain.model.Batch;
import de.jodabyte.springonk8slab.productservice.domain.model.OrderLine;
import de.jodabyte.springonk8slab.productservice.domain.model.Product;

public class TestData {

    public static final LocalDate TODAY = LocalDate.of(2025, 1, 1);
    public static final LocalDate TOMORROW = TODAY.plusDays(1);
    public static final LocalDate LATER = TOMORROW.plusDays(10);

    public static OrderLine makeOrderLine(String orderId, String sku, int quantity) {
        return TestData.createOrderLine(orderId, sku, quantity);
    }

    private static OrderLine createOrderLine(String orderId, String sku, int quantity) {
        OrderLine orderLine = new OrderLine();
        orderLine.setOrderId(orderId);
        orderLine.setSku(sku);
        orderLine.setQuantity(quantity);
        return orderLine;
    }

    public static Product makeProduct(String sku) {
        return TestData.createProduct(sku);
    }

    private static Product createProduct(String sku) {
        Product product = new Product();
        product.setSku(sku);
        return product;
    }

    public static Batch makeBatch(String reference, String sku, int batchQty, LocalDate eta) {
        return TestData.createBatch(reference,
                TestData.createProduct(sku),
                batchQty,
                eta);
    }

    public static Batch makeBatch(String reference, Product product, int batchQty, LocalDate eta) {
        return TestData.createBatch(reference,
                product,
                batchQty,
                eta);
    }

    private static Batch createBatch(String reference, Product product, int quantity, LocalDate eta) {
        Batch batch = new Batch();
        batch.setReference(reference);
        batch.setPurchasedQuantity(quantity);
        batch.setEta(eta);
        product.addBatch(batch);
        return batch;
    }

}
