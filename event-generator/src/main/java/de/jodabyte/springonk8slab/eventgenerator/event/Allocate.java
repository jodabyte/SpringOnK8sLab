package de.jodabyte.springonk8slab.eventgenerator.event;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;

import de.jodabyte.springonk8slab.eventgenerator.event.data.RandomValueProvider;
import de.jodabyte.springonk8slab.eventgenerator.productservice.api.ProductControllerApi;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Allocate {

    private ProductControllerApi client;
    private RandomValueProvider provider;

    public Allocate(ProductControllerApi client, RandomValueProvider provider) {
        this.client = client;
        this.provider = provider;
    }

    @Scheduled(initialDelayString = "${app.allocate.initialdelay}", fixedRateString = "${app.allocate.fixedrate}", timeUnit = TimeUnit.SECONDS)
    public void allocate() {
        log.info("Allocate executed in " + Thread.currentThread().getName());
        var event = this.createEventData();

        try {
            client.allocateWithHttpInfo(event);
            log.info("Allocate ID " + event.getOrderId());
        } catch (RestClientResponseException e) {
            log.error("Allocate msg " + e.getLocalizedMessage());
        }
    }

    private de.jodabyte.springonk8slab.eventgenerator.productservice.model.Allocate createEventData() {
        return new de.jodabyte.springonk8slab.eventgenerator.productservice.model.Allocate()
                .orderId(this.provider.generateOrderId())
                .sku(this.provider.generateSku())
                .quantity(this.provider.generateQuantity());
    }
}
