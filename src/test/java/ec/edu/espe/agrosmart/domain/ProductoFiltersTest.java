package ec.edu.espe.agrosmart.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoFiltersTest {

    @Test
    void isValid_cuandoElProductoEsValido_debeRetornarTrue() {

        // Arrange
        Producto producto = new Producto(
                1L,
                "Fresas frescas",
                "Frutas",
                new BigDecimal("120.50"),
                List.of("ventas@agrosmart.ec")
        );

        // Act & Assert
        assertTrue(ProductoFilters.IS_VALID.test(producto));
    }

    @Test
    void isValid_cuandoElPrecioEsCero_debeRetornarFalse() {

        // Arrange
        Producto producto = new Producto(
                2L,
                "Fresas frescas",
                "Frutas",
                BigDecimal.ZERO,
                List.of("ventas@agrosmart.ec")
        );

        // Act & Assert
        assertFalse(ProductoFilters.IS_VALID.test(producto));
    }

    @Test
    void isValid_cuandoNoHayCorreos_debeRetornarFalse() {

        // Arrange
        Producto producto = new Producto(
                3L,
                "Fresas frescas",
                "Frutas",
                new BigDecimal("120.50"),
                List.of()
        );

        // Act & Assert
        assertFalse(ProductoFilters.IS_VALID.test(producto));
    }
}