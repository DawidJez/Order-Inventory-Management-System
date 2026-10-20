package pl.dawjez.orderinventory.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;


 // Request body for creating a product.
public record CreateProductRequest(

        @NotBlank @Size(max = 255)
        String sku,

        @NotBlank @Size(max = 255)
        String name,

        @Size(max = 1000)
        String description,

        // @Digits mirrors NUMERIC(10, 2) in the database.
        @NotNull @DecimalMin("0.00") @Digits(integer = 8, fraction = 2)
        BigDecimal price,

        // Primitive int: a missing field in the JSON becomes 0, which is a sensible default
        @PositiveOrZero
        int initialQuantity
) {
}
