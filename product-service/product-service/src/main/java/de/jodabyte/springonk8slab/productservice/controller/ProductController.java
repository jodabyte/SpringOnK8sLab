package de.jodabyte.springonk8slab.productservice.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.jodabyte.springonk8slab.productservice.domain.command.Allocate;
import de.jodabyte.springonk8slab.productservice.domain.command.ChangeBatchQuantity;
import de.jodabyte.springonk8slab.productservice.domain.command.CreateBatch;
import de.jodabyte.springonk8slab.productservice.domain.event.Allocated;
import de.jodabyte.springonk8slab.productservice.domain.exception.InvalidBatchReferenceException;
import de.jodabyte.springonk8slab.productservice.domain.exception.InvalidSkuException;
import de.jodabyte.springonk8slab.productservice.domain.exception.OutOfStockException;
import de.jodabyte.springonk8slab.productservice.domain.service.ProductService;
import de.jodabyte.springonk8slab.productservice.domain.view.Allocation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
public class ProductController {

    private ProductService allocationService;

    public ProductController(ProductService allocationService) {
        this.allocationService = allocationService;
    }

    @Operation(summary = "Allocate an order line to a batch", responses = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(oneOf = {
                    Allocated.class, OutOfStockException.class }))),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = InvalidSkuException.class)))
    })
    @PostMapping("/allocate")
    public Allocated allocate(@RequestBody Allocate allocate) {
        return this.allocationService.allocate(allocate);
    }

    @Operation(summary = "Get allocations for order id", responses = {
            @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Allocation.class)))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = NoSuchElementException.class)))
    })
    @GetMapping("/allocations/{orderId}")
    public List<Allocation> allocations(@PathVariable String orderId) {
        return this.allocationService.getAllocations(orderId);
    }

    @Operation(summary = "Add a batch to a sku", responses = {
            @ApiResponse(responseCode = "200")
    })
    @PostMapping("/add_batch")
    public void addBatch(@RequestBody CreateBatch createBatch) {
        this.allocationService.addBatch(createBatch);
    }

    @Operation(summary = "Get allocations for order id", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(oneOf = {
                    InvalidBatchReferenceException.class, InvalidSkuException.class })))
    })
    @PostMapping("/change_batch_quantity")
    public void ChangeBatchQuantity(@RequestBody ChangeBatchQuantity changeBatchQuantity) {
        this.allocationService.changeBatchQuantity(changeBatchQuantity);
    }
}
