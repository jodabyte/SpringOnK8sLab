package de.jodabyte.springonk8slab.eventgenerator.event;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.jodabyte.springonk8slab.eventgenerator.event.data.RandomValueProvider;
import de.jodabyte.springonk8slab.eventgenerator.productservice.api.ProductControllerApi;
import de.jodabyte.springonk8slab.eventgenerator.productservice.model.ChangeBatchQuantity;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UpdateBatchQuantity {

    private List<String> batchRefs;

    private ProductControllerApi client;
    private RandomValueProvider provider;

    public UpdateBatchQuantity(List<String> batchRefs, ProductControllerApi client, RandomValueProvider provider) {
        this.batchRefs = batchRefs;
        this.client = client;
        this.provider = provider;
    }

    @Scheduled(initialDelayString = "${app.updatebatchquantity.initialdelay}", fixedRateString = "${app.updatebatchquantity.fixedrate}", timeUnit = TimeUnit.SECONDS)
    public void changeBatchQuantity() {
        log.info("ChangeBatchQuantity executed in " + Thread.currentThread().getName());
        var event = this.createEventData();
        client.changeBatchQuantityWithHttpInfo(event);
        log.info("ChangeBatchQuantity Batch " + event.getBatchRef());
    }

    private ChangeBatchQuantity createEventData() {
        return new ChangeBatchQuantity()
                .batchRef(this.getBatchRef())
                .qty(this.provider.generateQuantity());
    }

    private String getBatchRef() {
        int index = this.provider.generateNumberBetween(this.batchRefs.size());
        return this.batchRefs.get(index);
    }
}
