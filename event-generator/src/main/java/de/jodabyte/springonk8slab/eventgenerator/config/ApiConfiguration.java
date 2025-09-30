package de.jodabyte.springonk8slab.eventgenerator.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import de.jodabyte.springonk8slab.eventgenerator.productservice.client.ApiClient;

@Configuration
public class ApiConfiguration {

    @Bean
    public ApiClient apiClient(@Value("${app.productservice.url}") String baseURI) {
        return new ApiClient(restClient(baseURI)).setBasePath(baseURI);
    }

    @Bean
    public RestClient restClient(@Value("${app.productservice.url}") String baseURI) {
        return RestClient.create(baseURI);
    }

    @Bean
    public List<String> batchRefs() {
        return Collections.synchronizedList(new ArrayList<>());
    }

}
