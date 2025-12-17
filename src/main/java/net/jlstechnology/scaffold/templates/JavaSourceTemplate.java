package net.jlstechnology.scaffold.templates;

import net.jlstechnology.scaffold.core.ScaffoldConfig;
import net.jlstechnology.scaffold.core.TemplateLoader;

/**
 * Agrupa os templates relacionados às classes Java principais geradas pelo scaffold.
 */
public final class JavaSourceTemplate {

    private static final String APPLICATION_TEMPLATE = TemplateLoader.load("java/Application.java");
    private static final String EXEMPLO_MODEL_TEMPLATE = TemplateLoader.load("java/ExemploModel.java");
    private static final String EXEMPLO_REPOSITORY_TEMPLATE = TemplateLoader.load("java/ExemploRepository.java");
    private static final String EXEMPLO_MAPPER_TEMPLATE = TemplateLoader.load("java/ExemploMapper.java");
    private static final String EXEMPLO_SERVICE_TEMPLATE = TemplateLoader.load("java/ExemploService.java");
    private static final String EXEMPLO_DTO_TEMPLATE = TemplateLoader.load("java/ExemploDTO.java");
    private static final String EXEMPLO_RESOURCE_TEMPLATE = TemplateLoader.load("java/ExemploResource.java");
    private static final String PROBLEM_TYPE_TEMPLATE = TemplateLoader.load("java/ProblemType.java");
    private static final String PROBLEM_DETAILS_TEMPLATE = TemplateLoader.load("java/ProblemDetails.java");
    private static final String PROBLEM_DETAILS_EXCEPTION_TEMPLATE = TemplateLoader.load("java/ProblemDetailsException.java");
    private static final String RESOURCE_NOT_FOUND_PROBLEM_TEMPLATE = TemplateLoader.load("java/ResourceNotFoundProblem.java");
    private static final String VALIDATION_PROBLEM_TEMPLATE = TemplateLoader.load("java/ValidationProblem.java");
    private static final String BUSINESS_RULE_PROBLEM_TEMPLATE = TemplateLoader.load("java/BusinessRuleProblem.java");
    private static final String PROBLEM_DETAILS_CONTROLLER_ADVICE_TEMPLATE = TemplateLoader.load("java/ProblemDetailsControllerAdvice.java");
    private static final String BASIC_SIMULATION_TEMPLATE = TemplateLoader.load("java/BasicSimulation.java");
    private static final String AWS_CONFIG_TEMPLATE = TemplateLoader.load("java/AwsConfig.java");
    private static final String DATABASE_CONFIG_TEMPLATE = TemplateLoader.load("java/DatabaseConfig.java");

    private JavaSourceTemplate() {
        // Classe utilitária, não deve ser instanciada.
    }

    /**
     * Classe principal do Spring Boot.
     *
     * @param config configuração calculada.
     * @return conteúdo do arquivo.
     */
    public static String applicationClass(ScaffoldConfig config) {
        return String.format(APPLICATION_TEMPLATE, config.basePackage(), config.artifactPascalCase());
    }

    /**
     * Modelo de entidade exemplo.
     *
     * @param config configuração calculada.
     * @return conteúdo da classe.
     */
    public static String exemploModel(ScaffoldConfig config) {
        return String.format(EXEMPLO_MODEL_TEMPLATE, config.basePackage());
    }

    /**
     * Interface de repositório exemplo.
     *
     * @param config configuração calculada.
     * @return conteúdo da interface.
     */
    public static String exemploRepository(ScaffoldConfig config) {
        return String.format(EXEMPLO_REPOSITORY_TEMPLATE, config.basePackage());
    }

    /**
     * Interface de mapper exemplo.
     *
     * @param config configuração calculada.
     * @return conteúdo da interface de mapper.
     */
    public static String exemploMapperInterface(ScaffoldConfig config) {
        // Nota: Este método não é usado, mas mantido para compatibilidade
        // A implementação real está em exemploMapperImpl
        return exemploMapperImpl(config);
    }

    /**
     * Implementação concreta do mapper.
     *
     * @param config configuração calculada.
     * @return conteúdo da classe de implementação.
     */
    public static String exemploMapperImpl(ScaffoldConfig config) {
        return String.format(EXEMPLO_MAPPER_TEMPLATE, config.basePackage());
    }

    /**
     * Serviço exemplo.
     *
     * @param config configuração calculada.
     * @return conteúdo da classe de serviço.
     */
    public static String exemploService(ScaffoldConfig config) {
        return String.format(EXEMPLO_SERVICE_TEMPLATE, config.basePackage());
    }

    /**
     * DTO de exemplo.
     *
     * @param config configuração calculada.
     * @return conteúdo da classe DTO.
     */
    public static String exemploDto(ScaffoldConfig config) {
        return String.format(EXEMPLO_DTO_TEMPLATE, config.basePackage());
    }

    /**
     * Recurso REST de exemplo.
     *
     * @param config configuração calculada.
     * @return conteúdo da classe REST.
     */
    public static String exemploResource(ScaffoldConfig config) {
        String basePackage = config.basePackage();
        return String.format(EXEMPLO_RESOURCE_TEMPLATE, basePackage, basePackage);
    }

    public static String problemType(ScaffoldConfig config) {
        return String.format(PROBLEM_TYPE_TEMPLATE, config.basePackage());
    }

    public static String problemDetails(ScaffoldConfig config) {
        return String.format(PROBLEM_DETAILS_TEMPLATE, config.basePackage());
    }

    public static String problemDetailsException(ScaffoldConfig config) {
        return String.format(PROBLEM_DETAILS_EXCEPTION_TEMPLATE, config.basePackage());
    }

    public static String resourceNotFoundProblem(ScaffoldConfig config) {
        return String.format(RESOURCE_NOT_FOUND_PROBLEM_TEMPLATE, config.basePackage());
    }

    public static String validationProblem(ScaffoldConfig config) {
        return String.format(VALIDATION_PROBLEM_TEMPLATE, config.basePackage());
    }

    public static String businessRuleProblem(ScaffoldConfig config) {
        return String.format(BUSINESS_RULE_PROBLEM_TEMPLATE, config.basePackage());
    }

    public static String problemDetailsControllerAdvice(ScaffoldConfig config) {
        return String.format(PROBLEM_DETAILS_CONTROLLER_ADVICE_TEMPLATE, config.basePackage());
    }

    public static String gatlingSimulation(ScaffoldConfig config) {
        return String.format(BASIC_SIMULATION_TEMPLATE, config.basePackage());
    }

    /**
     * Classe de configuração AWS.
     *
     * @param config configuração calculada.
     * @return conteúdo da classe AwsConfig.
     */
    public static String awsConfig(ScaffoldConfig config) {
        return String.format(AWS_CONFIG_TEMPLATE, config.basePackage());
    }

    /**
     * Classe de configuração do banco usando Secrets Manager.
     *
     * @param config configuração calculada.
     * @return conteúdo da classe DatabaseConfig.
     */
    public static String databaseConfig(ScaffoldConfig config) {
        return String.format(DATABASE_CONFIG_TEMPLATE, config.basePackage(), config.artifactLowerCase());
    }
}

