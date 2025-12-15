package %1$s.web.rest.errors;

import java.net.URI;

/**
 * Enumera os tipos de problema definidos pela RFC 9457 (Problem Details for HTTP APIs).
 */
public enum ProblemType {

    VALIDATION(URI.create("https://datatracker.ietf.org/doc/html/rfc9457#section-3.1"), "Requisição inválida"),
    RESOURCE_NOT_FOUND(URI.create("https://datatracker.ietf.org/doc/html/rfc9457#section-3.3"), "Recurso não encontrado"),
    BUSINESS_RULE(URI.create("https://datatracker.ietf.org/doc/html/rfc9457#section-3.2"), "Violação de regra de negócio"),
    INTERNAL(URI.create("https://datatracker.ietf.org/doc/html/rfc9457#section-4.1"), "Erro interno inesperado");

    private final URI uri;
    private final String title;

    ProblemType(URI uri, String title) {
        this.uri = uri;
        this.title = title;
    }

    /**
     * Retorna a URI que identifica o tipo de problema (campo <code>type</code>).
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Retorna o título amigável do problema (campo <code>title</code>).
     */
    public String getTitle() {
        return title;
    }
}

