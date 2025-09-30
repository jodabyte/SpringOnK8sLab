package de.jodabyte.springonk8slab.eventgenerator.event;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.jodabyte.springonk8slab.eventgenerator.event.data.RandomValueProvider;
import de.jodabyte.springonk8slab.eventgenerator.productservice.api.ProductControllerApi;
import de.jodabyte.springonk8slab.eventgenerator.productservice.model.CreateBatch;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AddBatch {

    private List<String> batchRefs;

    private ProductControllerApi client;
    private RandomValueProvider provider;

    public AddBatch(List<String> batchRefs, ProductControllerApi client, RandomValueProvider provider) {
        this.batchRefs = batchRefs;
        this.client = client;
        this.provider = provider;
    }

    @Scheduled(fixedRateString = "${app.add-batch.fixedrate}", timeUnit = TimeUnit.SECONDS)
    public void addBatch() {
        log.info("AddBatch executed in " + Thread.currentThread().getName());
        CreateBatch event = this.createBatch();
        client.addBatchWithHttpInfo(event);
        this.batchRefs.add(event.getReference());
        log.info("AddBatch BatchRef " + event.getReference());
    }

    private CreateBatch createBatch() {
        return new CreateBatch()
                .reference(this.provider.generateBatchReference())
                .sku(this.provider.generateSku())
                .qty(this.provider.generateQuantity())
                .eta(this.provider.generateEta());
    }

}
