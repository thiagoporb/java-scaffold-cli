package %1$s.web.rest.errors;

import java.util.List;
import org.springframework.http.HttpStatus;

/**
 * Problema lançado quando a requisição contém dados inválidos.
 */
public final class ValidationProblem extends ProblemDetailsException {

    public ValidationProblem(List<ProblemDetails.Violation> violations) {
        super(
                ProblemType.VALIDATION,
                HttpStatus.BAD_REQUEST,
                "A requisição contém campos inválidos.",
                null,
                violations);
    }
}

