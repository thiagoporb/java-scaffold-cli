package %1$s.web.rest.errors;

import org.springframework.http.HttpStatus;

/**
 * Problema lançado quando uma regra de negócio é violada.
 */
public final class BusinessRuleProblem extends ProblemDetailsException {

    public BusinessRuleProblem(String detail) {
        super(ProblemType.BUSINESS_RULE, HttpStatus.UNPROCESSABLE_ENTITY, detail);
    }
}

