package ec.edu.espe.agrosmart.config;

import ec.edu.espe.agrosmart.entity.ProductoEntity;
import ec.edu.espe.agrosmart.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner sembrarProductos(ProductoRepository repository) {
        return args -> {

            if (repository.count() == 0) {

                repository.saveAll(List.of(

                        new ProductoEntity(
                                "Rosas premium ecuatorianas",
                                new BigDecimal("24.50"),
                                120,
                                "Flores",
                                "ventas@agrosmart.ec,compras@floristeria.ec"
                        ),

                        new ProductoEntity(
                                "Orquídeas blancas",
                                new BigDecimal("35.75"),
                                65,
                                "Flores",
                                "pedidos@agrosmart.ec"
                        ),

                        new ProductoEntity(
                                "Girasoles de exportación",
                                new BigDecimal("18.90"),
                                90,
                                "Flores",
                                "ventas@agrosmart.ec"
                        ),

                        new ProductoEntity(
                                "Tulipanes sin precio",
                                new BigDecimal("0.00"),
                                40,
                                "Flores",
                                "alertas@agrosmart.ec"
                        ),

                        new ProductoEntity(
                                "Claveles sin notificación",
                                new BigDecimal("12.25"),
                                75,
                                "Flores",
                                ""
                        )

                ));

                System.out.println("Se sembraron los productos correctamente.");
            }
        };
    }
}