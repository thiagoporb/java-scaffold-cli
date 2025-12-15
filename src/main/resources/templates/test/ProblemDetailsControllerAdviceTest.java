package %1$s.web.rest.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

/**
 * Garante que o ControllerAdvice trate as exceções corretamente.
 */
class ProblemDetailsControllerAdviceTest {

    private ProblemDetailsControllerAdvice controllerAdvice;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        controllerAdvice = new ProblemDetailsControllerAdvice();
        webRequest = mock(WebRequest.class);
    }

    @Test
    void deveTratarResourceNotFoundProblem() {
        ResourceNotFoundProblem exception = new ResourceNotFoundProblem("Exemplo", "123");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/exemplos/123");

        ResponseEntity<ProblemDetails> response = controllerAdvice.handleStructuredProblem(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ProblemType.RESOURCE_NOT_FOUND.getUri(), response.getBody().getType());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void deveTratarValidationProblem() {
        ProblemDetails.Violation violation = new ProblemDetails.Violation("campo", "erro");
        ValidationProblem exception = new ValidationProblem(List.of(violation));
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/exemplos");

        ResponseEntity<ProblemDetails> response = controllerAdvice.handleStructuredProblem(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ProblemType.VALIDATION.getUri(), response.getBody().getType());
        assertEquals(1, response.getBody().getInvalidParams().size());
    }

    @Test
    void deveTratarBusinessRuleProblem() {
        BusinessRuleProblem exception = new BusinessRuleProblem("Regra violada");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/exemplos");

        ResponseEntity<ProblemDetails> response = controllerAdvice.handleStructuredProblem(exception, webRequest);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ProblemType.BUSINESS_RULE.getUri(), response.getBody().getType());
    }

    @Test
    void deveTratarExceptionGenerica() {
        Exception exception = new RuntimeException("Erro inesperado");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/exemplos");

        ResponseEntity<ProblemDetails> response = controllerAdvice.handleUnexpected(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ProblemType.INTERNAL.getUri(), response.getBody().getType());
        assertEquals(500, response.getBody().getStatus());
    }

    @Test
    void deveConstruirInstanceUriCorretamente() {
        BusinessRuleProblem exception = new BusinessRuleProblem("Erro");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/exemplos/123");

        ResponseEntity<ProblemDetails> response = controllerAdvice.handleStructuredProblem(exception, webRequest);

        assertNotNull(response.getBody());
        assertEquals(URI.create("/api/v1/exemplos/123"), response.getBody().getInstance());
    }

    @Test
    void deveUsarUriPadraoQuandoDescriptionNaoComecaComUri() {
        BusinessRuleProblem exception = new BusinessRuleProblem("Erro");
        when(webRequest.getDescription(false)).thenReturn("invalid description");

        ResponseEntity<ProblemDetails> response = controllerAdvice.handleStructuredProblem(exception, webRequest);

        assertNotNull(response.getBody());
        assertEquals(URI.create("/"), response.getBody().getInstance());
    }

    @Test
    void deveUsarUriPadraoQuandoDescriptionEhNull() {
        BusinessRuleProblem exception = new BusinessRuleProblem("Erro");
        when(webRequest.getDescription(false)).thenReturn(null);

        ResponseEntity<ProblemDetails> response = controllerAdvice.handleStructuredProblem(exception, webRequest);

        assertNotNull(response.getBody());
        assertEquals(URI.create("/"), response.getBody().getInstance());
    }
}

