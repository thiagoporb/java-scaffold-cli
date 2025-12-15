package %1$s.web.rest.errors;

import org.springframework.http.HttpStatus;

/**
 * Problema lançado quando um recurso esperado não é encontrado.
 */
public final class ResourceNotFoundProblem extends ProblemDetailsException {

    public ResourceNotFoundProblem(String resource, String identifier) {
        super(
                ProblemType.RESOURCE_NOT_FOUND,
                HttpStatus.NOT_FOUND,
                String.format("Recurso %%s com identificador %%s não foi encontrado.", resource, identifier));
    }
}

