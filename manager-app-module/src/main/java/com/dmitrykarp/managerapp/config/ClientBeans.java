package com.dmitrykarp.managerapp.config;

import com.dmitrykarp.managerapp.client.RestClientProductsRestClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBeans {

    @Bean
    public RestClientProductsRestClientImpl productsRestClient(@Value("${managerapp.services.catalog.uri:http://localhost:8084}") String catalogBaseUri,
                                                               @Value("${managerapp.services.catalog.username:}") String catalogUsername,
                                                               @Value("${managerapp.services.catalog.password:}") String catalogPassword){
        return new RestClientProductsRestClientImpl(RestClient.builder()
                .baseUrl(catalogBaseUri)
                .requestInterceptor(new BasicAuthenticationInterceptor(catalogUsername, catalogPassword))
                .build());
    }
}
