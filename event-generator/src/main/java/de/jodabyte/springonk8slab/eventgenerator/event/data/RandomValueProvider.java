package de.jodabyte.springonk8slab.eventgenerator.event.data;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.stereotype.Component;

import net.datafaker.Faker;

@Component
public class RandomValueProvider {

    private final Faker faker = new Faker();

    public String generateBatchReference() {
        return "batch-" + UUID.randomUUID().toString();
    }

    public String generateSku() {
        return this.faker.device().modelName();
    }

    public int generateQuantity() {
        return this.faker.number().randomDigitNotZero();
    }

    public LocalDate generateEta() {
        return LocalDate.ofInstant(this.faker.timeAndDate()
                .between(LocalDate.of(2025, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                        LocalDate.of(2025, 1, 31).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                ZoneId.systemDefault());
    }

    public int generateNumberBetween(int upperBound) {
        return this.faker.number().numberBetween(0, upperBound);
    }

    public String generateOrderId() {
        return "order-" + UUID.randomUUID().toString();
    }
}
