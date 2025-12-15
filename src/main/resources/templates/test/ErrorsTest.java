package %1$s.web.rest.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * Garante que as classes de erro funcionem corretamente.
 */
class ErrorsTest {

    @Test
    void problemTypeDeveRetornarUriETitle() {
        assertEquals("Requisição inválida", ProblemType.VALIDATION.getTitle());
        assertEquals("Recurso não encontrado", ProblemType.RESOURCE_NOT_FOUND.getTitle());
        assertEquals("Violação de regra de negócio", ProblemType.BUSINESS_RULE.getTitle());
        assertEquals("Erro interno inesperado", ProblemType.INTERNAL.getTitle());

        assertNotNull(ProblemType.VALIDATION.getUri());
        assertNotNull(ProblemType.RESOURCE_NOT_FOUND.getUri());
        assertNotNull(ProblemType.BUSINESS_RULE.getUri());
        assertNotNull(ProblemType.INTERNAL.getUri());
    }

    @Test
    void problemDetailsDeveCriarInstanciaCorretamente() {
        ProblemDetails.Violation violation = new ProblemDetails.Violation("campo", "erro");
        List<ProblemDetails.Violation> violations = List.of(violation);
        URI instance = URI.create("/teste");

        ProblemDetails problem = ProblemDetails.of(
                ProblemType.VALIDATION,
                HttpStatus.BAD_REQUEST,
                "Teste de erro",
                instance,
                violations);

        assertNotNull(problem);
        assertEquals(ProblemType.VALIDATION.getUri(), problem.getType());
        assertEquals(ProblemType.VALIDATION.getTitle(), problem.getTitle());
        assertEquals(Integer.valueOf(400), problem.getStatus());
        assertEquals("Teste de erro", problem.getDetail());
        assertEquals(instance, problem.getInstance());
        assertNotNull(problem.getTimestamp());
        assertNotNull(problem.getInvalidParams());
        assertEquals(1, problem.getInvalidParams().size());
        assertEquals("campo", problem.getInvalidParams().get(0).getName());
        assertEquals("erro", problem.getInvalidParams().get(0).getReason());
    }

    @Test
    void problemDetailsDevePermitirInvalidParamsNullo() {
        ProblemDetails problem = ProblemDetails.of(
                ProblemType.INTERNAL,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno",
                null,
                null);

        assertNotNull(problem);
        assertNull(problem.getInvalidParams());
        assertNull(problem.getInstance());
        assertEquals("Erro interno", problem.getDetail());
    }

    @Test
    void problemDetailsDevePermitirInvalidParamsVazio() {
        ProblemDetails problem = ProblemDetails.of(
                ProblemType.INTERNAL,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno",
                null,
                List.of());

        assertNotNull(problem);
        assertNull(problem.getInvalidParams());
    }

    @Test
    void problemDetailsDevePermitirDetailNull() {
        ProblemDetails problem = ProblemDetails.of(
                ProblemType.BUSINESS_RULE,
                HttpStatus.UNPROCESSABLE_ENTITY,
                null,
                URI.create("/test"),
                null);

        assertNotNull(problem);
        assertNull(problem.getDetail());
        assertEquals(URI.create("/test"), problem.getInstance());
    }

    @Test
    void resourceNotFoundProblemDeveCriarComMensagemCorreta() {
        ResourceNotFoundProblem exception = new ResourceNotFoundProblem("Exemplo", "123");

        assertEquals(ProblemType.RESOURCE_NOT_FOUND, exception.getProblemType());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Recurso Exemplo com identificador 123 não foi encontrado.", exception.getMessage());
    }

    @Test
    void validationProblemDeveCriarComViolations() {
        ProblemDetails.Violation v1 = new ProblemDetails.Violation("campo1", "erro1");
        ProblemDetails.Violation v2 = new ProblemDetails.Violation("campo2", "erro2");

        ValidationProblem exception = new ValidationProblem(List.of(v1, v2));

        assertEquals(ProblemType.VALIDATION, exception.getProblemType());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(2, exception.getViolations().size());
    }

    @Test
    void businessRuleProblemDeveCriarComDetail() {
        BusinessRuleProblem exception = new BusinessRuleProblem("Regra violada");

        assertEquals(ProblemType.BUSINESS_RULE, exception.getProblemType());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());
        assertEquals("Regra violada", exception.getMessage());
    }

    @Test
    void violationDeveArmazenarNameEReason() {
        ProblemDetails.Violation violation = new ProblemDetails.Violation("campo", "motivo");

        assertEquals("campo", violation.getName());
        assertEquals("motivo", violation.getReason());
    }
}

