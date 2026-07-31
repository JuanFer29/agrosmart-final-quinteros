package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.ai.AgroSmartAIService;
import ec.edu.espe.agrosmart.domain.Producto;
import ec.edu.espe.agrosmart.domain.ProductoFilters;
import ec.edu.espe.agrosmart.exception.ProductoNoEncontradoException;
import ec.edu.espe.agrosmart.mapper.ProductoMapper;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;

@Service
public class ProductoService {

    private static final Producto PRODUCTO_GENERICO = new Producto(
            0L,
            "PRODUCTO GENERICO",
            "SIN CATEGORIA",
            BigDecimal.ZERO,
            Collections.emptyList()
    );

    private final ProductoRepository repository;
    private final AgroSmartAIService aiService;

    public ProductoService(
            ProductoRepository repository,
            AgroSmartAIService aiService
    ) {
        this.repository = repository;
        this.aiService = aiService;
    }

    public Flux<Producto> obtenerProductosComercializables() {

        // fromCallable difiere la consulta al repositorio:
        // la operación no se ejecuta hasta que alguien se suscribe al flujo.
        return Mono.fromCallable(repository::findAll)

                // JPA e Hibernate realizan operaciones bloqueantes.
                // boundedElastic evita bloquear el event loop de Netty.
                .subscribeOn(Schedulers.boundedElastic())

                // Convierte la lista obtenida desde JPA en un Flux de entidades.
                .flatMapMany(Flux::fromIterable)

                // Convierte cada ProductoEntity al modelo de dominio inmutable Producto.
                .map(ProductoMapper::toDominio)

                // Crea una nueva instancia con el nombre en mayúsculas.
                .map(ProductoFilters.A_MAYUSCULAS)

                // Descarta productos con precio menor o igual a cero
                // o sin correos de notificación.
                .filter(ProductoFilters.IS_VALID)

                // Registra por consola el id y el nombre sin modificar el producto.
                .doOnNext(ProductoFilters.LOG_PRODUCTO)

                // Si ningún producto supera el filtro, emite un producto genérico.
                .defaultIfEmpty(PRODUCTO_GENERICO);
    }

    public Mono<Producto> buscarPorId(Long id) {

        // Envuelve la consulta bloqueante y retrasa su ejecución
        // hasta que exista una suscripción.
        return Mono.fromCallable(() -> repository.findById(id))

                // Ejecuta la consulta JPA fuera del event loop.
                .subscribeOn(Schedulers.boundedElastic())

                // Convierte Optional.empty() en Mono.empty()
                // y Optional con valor en Mono<ProductoEntity>.
                .flatMap(Mono::justOrEmpty)

                // Convierte la entidad encontrada al modelo de dominio.
                .map(ProductoMapper::toDominio)

                // Si el Mono está vacío, genera el error dentro del flujo reactivo.
                .switchIfEmpty(
                        Mono.error(new ProductoNoEncontradoException(id))
                );
    }

    public Mono<String> generarPublicidad(
            String producto,
            String audiencia
    ) {
        // La llamada al proveedor de IA es bloqueante, por eso se difiere
        // hasta que alguien se suscribe al Mono.
        return Mono.fromCallable(
                        () -> aiService.generarPublicidad(producto, audiencia)
                )

                // La llamada HTTP se ejecuta fuera del event loop de Netty.
                .subscribeOn(Schedulers.boundedElastic())

                // Cancela la espera si el proveedor tarda más de 30 segundos.
                .timeout(Duration.ofSeconds(30))

                // Un error de red, cuota o timeout se convierte en una respuesta controlada.
                .onErrorResume(error -> Mono.just(
                        "Publicidad no disponible en este momento ("
                                + error.getClass().getSimpleName()
                                + ")"
                ));
    }
}