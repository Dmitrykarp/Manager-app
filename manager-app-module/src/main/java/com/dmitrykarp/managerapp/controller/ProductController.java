package com.dmitrykarp.managerapp.controller;

import com.dmitrykarp.managerapp.client.BadRequestException;
import com.dmitrykarp.managerapp.client.ProductsRestClient;
import com.dmitrykarp.managerapp.controller.payload.UpdateProductPayload;
import com.dmitrykarp.managerapp.entity.Product;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalog/products/{productId:\\d+}")
public class ProductController {

    private final ProductsRestClient productsRestClient;
    private final MessageSource messageSource;


    @ModelAttribute("product")
    public Product product(@PathVariable("productId") int productId) {
        return this.productsRestClient.findProduct(productId).orElseThrow(() -> new NoSuchElementException("catalog.errors.product.not_found"));
    }

    @GetMapping()
    public String getProduct() {
        return "catalog/products/product";
    }

    @GetMapping("edit")
    public String getProductEditPage() {
        return "catalog/products/edit";
    }

    @PostMapping("edit")
    public String updateProduct(@ModelAttribute(value = "product", binding = false) Product product,
                                UpdateProductPayload payload,
                                Model model) {

        try {
            this.productsRestClient.updateProduct(product.id(), payload.title(), payload.details());
            return "redirect:/catalog/products/%d".formatted(product.id());
        } catch (BadRequestException e) {
            model.addAttribute("payload", payload);
            model.addAttribute("errors", e.getErrors());
            return "catalog/products/edit";
        }

    }

    @PostMapping("delete")
    public String deleteProduct(@ModelAttribute("product") Product product) {
        this.productsRestClient.deleteProduct(product.id());
        return "redirect:/catalog/products/list";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception,
                                               Model model,
                                               HttpServletResponse response,
                                               Locale locale) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", this.messageSource.getMessage(exception.getMessage(), null, locale));
        return "errors/404";
    }
}
