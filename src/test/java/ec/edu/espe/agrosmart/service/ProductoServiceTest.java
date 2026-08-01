package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.entity.ProductoEntity;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

class ProductoServiceTest {

    @Test
    void obtenerProductosComercializables_conTresValidosYDosInvalidos_debeEmitirSoloLosValidos() {

        // Arrange
        ProductoRepository repository = Mockito.mock(ProductoRepository.class);

        Mockito.when(repository.findAll()).thenReturn(List.of(
                crearEntidad(
                        1L,
                        "Rosas premium ecuatorianas",
                        new BigDecimal("24.50"),
                        "Flores",
                        "ventas@agrosmart.ec"
                ),
                crearEntidad(
                        2L,
                        "Orquídeas blancas",
                        new BigDecimal("35.75"),
                        "Flores",
                        "pedidos@agrosmart.ec"
                ),
                crearEntidad(
                        3L,
                        "Girasoles de exportación",
                        new BigDecimal("18.90"),
                        "Flores",
                        "ventas@agrosmart.ec"
                ),
                crearEntidad(
                        4L,
                        "Tulipanes sin precio",
                        BigDecimal.ZERO,
                        "Flores",
                        "alertas@agrosmart.ec"
                ),
                crearEntidad(
                        5L,
                        "Claveles sin notificación",
                        new BigDecimal("12.25"),
                        "Flores",
                        ""
                )
        ));

        ProductoService service = new ProductoService(repository, null);

        // Act
        Flux<Producto> flujo = service.obtenerProductosComercializables();

        // Assert
        StepVerifier.create(flujo)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void obtenerProductosComercializables_cuandoTodosSonInvalidos_debeEmitirProductoGenerico() {

        // Arrange
        ProductoRepository repository = Mockito.mock(ProductoRepository.class);

        Mockito.when(repository.findAll()).thenReturn(List.of(
                crearEntidad(
                        1L,
                        "Producto sin precio",
                        BigDecimal.ZERO,
                        "Flores",
                        "ventas@agrosmart.ec"
                ),
                crearEntidad(
                        2L,
                        "Producto sin correos",
                        new BigDecimal("15.00"),
                        "Flores",
                        ""
                )
        ));

        ProductoService service = new ProductoService(repository, null);

        // Act
        Flux<Producto> flujo = service.obtenerProductosComercializables();

        // Assert
        StepVerifier.create(flujo)
                .expectNextMatches(producto ->
                        producto.getId().equals(0L)
                                && producto.getNombre().equals("PRODUCTO GENERICO")
                )
                .verifyComplete();
    }

    @Test
    void buscarPorId_cuandoNoExiste_debeTerminarConProductoNoEncontradoException() {

        // Arrange
        ProductoRepository repository = Mockito.mock(ProductoRepository.class);
        Mockito.when(repository.findById(9999L)).thenReturn(Optional.empty());

        ProductoService service = new ProductoService(repository, null);

        // Act & Assert
        StepVerifier.create(service.buscarPorId(9999L))
                .expectError(ProductoNoEncontradoException.class)
                .verify();
    }

    private ProductoEntity crearEntidad(
            Long id,
            String nombre,
            BigDecimal precio,
            String categoria,
            String correos
    ) {
        ProductoEntity entity = new ProductoEntity();
        entity.setIdProducto(id);
        entity.setNombreProducto(nombre);
        entity.setPrecioUsd(precio);
        entity.setStockKg(100);
        entity.setCategoria(categoria);
        entity.setCorreosNotificacion(correos);
        return entity;
    }
}