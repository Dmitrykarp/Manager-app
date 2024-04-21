package com.dmitrykarp.catalog.controller.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewProductPayload(@NotNull
                                @Size(min=3,max=50, message = "{catalog.errors.product.create.size_title_invlaid}")
                                String title,
                                @Size(max=1000)
                                String details) {

}
