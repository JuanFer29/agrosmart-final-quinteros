package ec.edu.espe.agrosmart.service;

import ec.edu.espe.agrosmart.ai.AgroSmartAIService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;

class PublicidadServiceTest {

    @Test
    void generarPublicidad_cuandoElProveedorResponde_debeEmitirElTextoGenerado() {

        // Arrange
        AgroSmartAIService aiService = Mockito.mock(AgroSmartAIService.class);

        Mockito.when(
                aiService.generarPublicidad(
                        anyString(),
                        anyString()
                )
        ).thenReturn(
                "Fresas frescas para disfrutar en cualquier momento"
        );

        ProductoService service = new ProductoService(null, aiService);

        // Act & Assert
        StepVerifier.create(
                        service.generarPublicidad(
                                "Fresas frescas",
                                "familias"
                        )
                )
                .expectNext(
                        "Fresas frescas para disfrutar en cualquier momento"
                )
                .verifyComplete();
    }

    @Test
    void generarPublicidad_cuandoElProveedorFalla_debeEmitirMensajeDeRespaldo() {

        // Arrange
        AgroSmartAIService aiService = Mockito.mock(AgroSmartAIService.class);

        Mockito.when(
                aiService.generarPublicidad(
                        anyString(),
                        anyString()
                )
        ).thenThrow(
                new RuntimeException("429 Too Many Requests")
        );

        ProductoService service = new ProductoService(null, aiService);

        // Act & Assert
        StepVerifier.create(
                        service.generarPublicidad(
                                "Fresas frescas",
                                "familias"
                        )
                )
                .expectNextMatches(
                        texto -> texto.contains(
                                "Publicidad no disponible en este momento"
                        )
                )
                .verifyComplete();
    }
}