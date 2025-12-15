package %1$s.web.rest.errors;

import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Agrupamento centralizado de tratamento de exceções que traduzem erros para Problem Details (RFC 9457).
 */
@RestControllerAdvice
public class ProblemDetailsControllerAdvice {

    @ExceptionHandler(ProblemDetailsException.class)
    public ResponseEntity<ProblemDetails> handleStructuredProblem(
            ProblemDetailsException exception, WebRequest request) {
        ProblemDetails problemDetails = ProblemDetails.of(
                exception.getProblemType(),
                exception.getStatus(),
                exception.getMessage(),
                buildInstance(request),
                exception.getViolations());
        return ResponseEntity.status(exception.getStatus()).body(problemDetails);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleUnexpected(Exception exception, WebRequest request) {
        ProblemDetails problemDetails = ProblemDetails.of(
                ProblemType.INTERNAL,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno inesperado. Contate o suporte com o timestamp.",
                buildInstance(request),
                List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetails);
    }

    private URI buildInstance(WebRequest request) {
        String description = request.getDescription(false);
        if (description != null && description.startsWith("uri=")) {
            String uri = description.substring("uri=".length());
            try {
                return URI.create(uri);
            } catch (IllegalArgumentException e) {
                return URI.create("/");
            }
        }
        return URI.create("/");
    }
}

