package %1$s.web.rest.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;

/**
 * Representa o schema de Problem Details em conformidade com a RFC 9457.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ProblemDetails {

    private final URI type;
    private final String title;
    private final Integer status;
    private final String detail;
    private final URI instance;
    private final Instant timestamp;
    @JsonProperty("invalid-params")
    private final List<Violation> invalidParams;

    private ProblemDetails(
            URI type,
            String title,
            Integer status,
            String detail,
            URI instance,
            Instant timestamp,
            List<Violation> invalidParams) {
        this.type = Objects.requireNonNull(type, "type não pode ser nulo");
        this.title = Objects.requireNonNull(title, "title não pode ser nulo");
        this.status = Objects.requireNonNull(status, "status não pode ser nulo");
        this.detail = detail;
        this.instance = instance;
        this.timestamp = timestamp;
        this.invalidParams = invalidParams == null || invalidParams.isEmpty()
                ? null
                : List.copyOf(invalidParams);
    }

    /**
     * Cria um Problem Details com informações básicas.
     */
    public static ProblemDetails of(
            ProblemType type,
            HttpStatus status,
            String detail,
            URI instance,
            List<Violation> invalidParams) {
        return new ProblemDetails(
                type.getUri(),
                type.getTitle(),
                status.value(),
                detail,
                instance,
                Instant.now(),
                invalidParams);
    }

    public URI getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public URI getInstance() {
        return instance;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public List<Violation> getInvalidParams() {
        return invalidParams;
    }

    /**
     * Detalha cada validação específica que falhou.
     */
    public static final class Violation {

        private final String name;
        private final String reason;

        public Violation(String name, String reason) {
            this.name = Objects.requireNonNull(name, "name não pode ser nulo");
            this.reason = Objects.requireNonNull(reason, "reason não pode ser nulo");
        }

        public String getName() {
            return name;
        }

        public String getReason() {
            return reason;
        }
    }
}

