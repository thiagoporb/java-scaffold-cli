package %1$s.web.rest.errors;

import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;

/**
 * Exceção base para problemas que devem ser expostos via RFC 9457.
 */
public abstract class ProblemDetailsException extends RuntimeException {

    private final ProblemType problemType;
    private final HttpStatus status;
    private final URI instance;
    private final List<ProblemDetails.Violation> violations;

    protected ProblemDetailsException(
            ProblemType problemType,
            HttpStatus status,
            String message) {
        this(problemType, status, message, null, null);
    }

    protected ProblemDetailsException(
            ProblemType problemType,
            HttpStatus status,
            String message,
            URI instance,
            List<ProblemDetails.Violation> violations) {
        super(message);
        this.problemType = problemType;
        this.status = status;
        this.instance = instance;
        this.violations = violations == null ? List.of() : List.copyOf(violations);
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public URI getInstance() {
        return instance;
    }

    public List<ProblemDetails.Violation> getViolations() {
        return violations;
    }
}

