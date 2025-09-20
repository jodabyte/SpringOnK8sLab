package de.jodabyte.springonk8slab.productservice.controller;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.jodabyte.springonk8slab.productservice.domain.command.Allocate;
import de.jodabyte.springonk8slab.productservice.domain.command.CreateBatch;
import de.jodabyte.springonk8slab.productservice.domain.exception.InvalidSkuException;
import de.jodabyte.springonk8slab.productservice.domain.view.Allocation;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

        private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());;

        private LocalDate EARLY = LocalDate.of(2025, 01, 01);
        private LocalDate LATER = LocalDate.of(2025, 02, 01);

        @Autowired
        private MockMvcTester client;

        @Test
        void testHappyPathReturns202AndBatchIsAllocated() throws JsonProcessingException {
                String orderId = "oder1";
                String aSku = "sku1";
                String otherSku = "sku2";
                String earlyBatch = "batch1";
                String laterBatch = "batch2";
                String otherBatch = "batch3";

                this.client.post().uri("/add_batch")
                                .content(objectMapper.writeValueAsString(new CreateBatch(laterBatch, aSku, 100, LATER)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .assertThat()
                                .hasStatus(HttpStatus.OK);
                this.client.post().uri("/add_batch")
                                .content(objectMapper.writeValueAsString(new CreateBatch(earlyBatch, aSku, 100, EARLY)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .assertThat()
                                .hasStatus(HttpStatus.OK);
                this.client.post().uri("/add_batch")
                                .content(objectMapper
                                                .writeValueAsString(new CreateBatch(otherBatch, otherSku, 100, null)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .assertThat()
                                .hasStatus(HttpStatus.OK);
                this.client.post().uri("/allocate")
                                .content(objectMapper.writeValueAsString(new Allocate(orderId, aSku, 3)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .assertThat()
                                .hasStatus(HttpStatus.OK);
                this.client.get().uri("/allocations/{orderId}", orderId)
                                .assertThat()
                                .hasStatus(HttpStatus.OK)
                                .bodyJson()
                                .convertTo(InstanceOfAssertFactories.list(Allocation.class))
                                .hasSize(1)
                                .containsExactly(new Allocation(orderId, aSku, earlyBatch));
        }

        @Test
        void testUnhappyPathReturns400AndErrorMessage() throws JsonProcessingException {
                String orderId = "oder1";
                String sku = "sku1";

                this.client.post().uri("/allocate")
                                .content(objectMapper.writeValueAsString(new Allocate(orderId, sku, 20)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .assertThat()
                                .hasStatus(HttpStatus.BAD_REQUEST)
                                .failure()
                                .hasMessageContaining(InvalidSkuException.of(sku).getMessage());

                this.client.get().uri("/allocations/{orderId}", orderId)
                                .assertThat()
                                .hasStatus(HttpStatus.NOT_FOUND)
                                .failure()
                                .isInstanceOf(NoSuchElementException.class);
        }
}
