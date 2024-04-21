package com.dmitrykarp.managerapp.controller;

import com.dmitrykarp.managerapp.client.BadRequestException;
import com.dmitrykarp.managerapp.client.ProductsRestClient;
import com.dmitrykarp.managerapp.controller.payload.NewProductPayload;
import com.dmitrykarp.managerapp.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalog/products")
public class ProductsController {

    private final ProductsRestClient productsRestClient;

    @GetMapping("list")
    public String getProductsList(Model model, @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("products", this.productsRestClient.findAllProducts(filter));
        model.addAttribute("filter", filter);
        return "catalog/products/list";
    }

    @GetMapping("create")
    public String getNewProductPage() {
        return "catalog/products/new_product";
    }

    @PostMapping("create")
    public String createProduct(NewProductPayload payload, Model model) {
        try {
            Product product = this.productsRestClient.createProduct(payload.title(), payload.details());
            return "redirect:/catalog/products/%d".formatted(product.id());
        } catch (BadRequestException e) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", e.getErrors());
            return "catalog/products/new_product";
        }

    }

    @PostMapping("create_list")
    public String createProducts() {
        IntStream.range(1, 10)
                .forEach(i -> this.productsRestClient.createProduct("Товар #%d".formatted(i), "Описание товара #%d".formatted(i)));
        return "redirect:/catalog/products/list";
    }

    @PostMapping("delete_list")
    public String deleteProducts() {
        List<Product> tempList = this.productsRestClient.findAllProducts("");
        for (Product pr: tempList){
            this.productsRestClient.deleteProduct(pr.id());
        }
        return "redirect:/catalog/products/list";
    }

}
