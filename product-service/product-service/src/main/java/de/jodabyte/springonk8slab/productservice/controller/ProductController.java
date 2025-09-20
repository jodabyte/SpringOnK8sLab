package de.jodabyte.springonk8slab.productservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.jodabyte.springonk8slab.productservice.domain.command.Allocate;
import de.jodabyte.springonk8slab.productservice.domain.command.ChangeBatchQuantity;
import de.jodabyte.springonk8slab.productservice.domain.command.CreateBatch;
import de.jodabyte.springonk8slab.productservice.domain.event.Allocated;
import de.jodabyte.springonk8slab.productservice.domain.service.ProductService;
import de.jodabyte.springonk8slab.productservice.domain.view.Allocation;

@RestController
public class ProductController {

    private ProductService allocationService;

    public ProductController(ProductService allocationService) {
        this.allocationService = allocationService;
    }

    @PostMapping("/allocate")
    public Allocated allocate(@RequestBody Allocate allocate) {
        return this.allocationService.allocate(allocate);
    }

    @GetMapping("/allocations/{orderId}")
    public List<Allocation> allocations(@PathVariable String orderId) {
        return this.allocationService.getAllocations(orderId);
    }

    @PostMapping("/add_batch")
    public void addBatch(@RequestBody CreateBatch createBatch) {
        this.allocationService.addBatch(createBatch);
    }

    @PostMapping("/change_batch_quantity")
    public void ChangeBatchQuantity(@RequestBody ChangeBatchQuantity changeBatchQuantity) {
        this.allocationService.changeBatchQuantity(changeBatchQuantity);
    }
}
