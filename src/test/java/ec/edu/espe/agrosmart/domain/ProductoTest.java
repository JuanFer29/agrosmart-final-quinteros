package ec.edu.espe.agrosmart.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    @Test
    void constructor_yGetters_debenRetornarLosValoresCorrectos() {

        // Arrange
        List<String> correos = List.of(
                "ventas@agrosmart.ec",
                "compras@agrosmart.ec"
        );

        Producto producto = new Producto(
                1L,
                "Fresas frescas",
                "Frutas",
                new BigDecimal("120.50"),
                correos
        );

        // Act & Assert
        assertEquals(1L, producto.getId());
        assertEquals("Fresas frescas", producto.getNombre());
        assertEquals("Frutas", producto.getCategoria());
        assertEquals(new BigDecimal("120.50"), producto.getPrecioUsd());
        assertEquals(correos, producto.getCorreosNotificacion());
    }

    @Test
    void getCorreosNotificacion_alMutarLaListaOriginal_noDebeAfectarAlProducto() {

        // Arrange
        List<String> correos = new ArrayList<>();
        correos.add("ventas@agrosmart.ec");

        Producto producto = new Producto(
                1L,
                "Fresas frescas",
                "Frutas",
                new BigDecimal("120.50"),
                correos
        );

        // Act
        correos.add("intruso@mail.com");

        // Assert
        assertEquals(1, producto.getCorreosNotificacion().size());
        assertNotSame(correos, producto.getCorreosNotificacion());
    }

    @Test
    void getCorreosNotificacion_debeRetornarListaInmodificable() {

        // Arrange
        List<String> correos = new ArrayList<>();
        correos.add("ventas@agrosmart.ec");

        Producto producto = new Producto(
                1L,
                "Fresas frescas",
                "Frutas",
                new BigDecimal("120.50"),
                correos
        );

        // Act & Assert
        assertThrows(
                UnsupportedOperationException.class,
                () -> producto.getCorreosNotificacion().add("nuevo@mail.com")
        );
    }
}