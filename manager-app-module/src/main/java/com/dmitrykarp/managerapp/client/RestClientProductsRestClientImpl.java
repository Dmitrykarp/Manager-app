package com.dmitrykarp.managerapp.client;

import com.dmitrykarp.managerapp.controller.payload.NewProductPayload;
import com.dmitrykarp.managerapp.controller.payload.UpdateProductPayload;
import com.dmitrykarp.managerapp.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
@RequiredArgsConstructor
public class RestClientProductsRestClientImpl implements ProductsRestClient {
    private final RestClient restClient;

    private static final ParameterizedTypeReference<List<Product>> PRODUCTS_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    @Override
    public List<Product> findAllProducts(String filter) {
        return this.restClient
                .get()
                .uri("/catalog-api/products?filter={filter}", filter)
                .retrieve()
                .body(PRODUCTS_TYPE_REFERENCE);
    }

    @Override
    public Product createProduct(String title, String details) {
        try {
            return this.restClient
                    .post()
                    .uri("/catalog-api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new NewProductPayload(title, details))
                    .retrieve()
                    .body(Product.class);
        }catch (HttpClientErrorException.BadRequest exception){
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public Optional<Product> findProduct(int productId) {
        try {
            return Optional.ofNullable(this.restClient
                    .get()
                    .uri("/catalog-api/products/{productId}", productId)
                    .retrieve()
                    .body(Product.class));
        }catch (HttpClientErrorException.NotFound e){
            return Optional.empty();
        }
    }

    @Override
    public void updateProduct(int productId, String title, String details) {
        try {
            this.restClient
                    .patch()
                    .uri("/catalog-api/products/{productId}", productId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateProductPayload(title, details))
                    .retrieve()
                    .toBodilessEntity();
        }catch (HttpClientErrorException.BadRequest exception){
            ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
            throw new BadRequestException((List<String>) problemDetail.getProperties().get("errors"));
        }
    }

    @Override
    public void deleteProduct(int productId) {
        try {
            this.restClient
                    .delete()
                    .uri("/catalog-api/products/{productId}", productId)
                    .retrieve()
                    .toBodilessEntity();
        }catch (HttpClientErrorException.NotFound e){
            throw new NoSuchElementException();
        }
    }
}
