package net.jlstechnology.scaffold.templates;

import net.jlstechnology.scaffold.core.ScaffoldConfig;
import net.jlstechnology.scaffold.core.TemplateLoader;

/**
 * Templates dedicados aos testes gerados automaticamente.
 */
public final class TestTemplate {

    private static final String APPLICATION_TEST_TEMPLATE = TemplateLoader.load("test/ApplicationTest.java");
    private static final String ARQUITETURA_TEST_TEMPLATE = TemplateLoader.load("test/ArquiteturaTest.java");
    private static final String MAPPER_TEST_TEMPLATE = TemplateLoader.load("test/ExemploMapperTest.java");
    private static final String SERVICE_TEST_TEMPLATE = TemplateLoader.load("test/ExemploServiceTest.java");
    private static final String RESOURCE_TEST_TEMPLATE = TemplateLoader.load("test/ExemploResourceTest.java");
    private static final String ERRORS_TEST_TEMPLATE = TemplateLoader.load("test/ErrorsTest.java");
    private static final String CONTROLLER_ADVICE_TEST_TEMPLATE = TemplateLoader.load("test/ProblemDetailsControllerAdviceTest.java");

    private TestTemplate() {
        // Classe utilitária, não deve ser instanciada.
    }

    /**
     * Teste de carga de contexto Spring.
     *
     * @param config configuração calculada.
     * @return conteúdo do teste.
     */
    public static String applicationTest(ScaffoldConfig config) {
        return String.format(APPLICATION_TEST_TEMPLATE, config.basePackage(), config.artifactPascalCase());
    }

    /**
     * Testes de arquitetura com ArchUnit.
     *
     * @param config configuração calculada.
     * @return conteúdo do teste de arquitetura.
     */
    public static String arquiteturaTest(ScaffoldConfig config) {
        return String.format(ARQUITETURA_TEST_TEMPLATE, config.basePackage());
    }

    /**
     * Teste unitário do mapper.
     *
     * @param config configuração calculada.
     * @return conteúdo do teste.
     */
    public static String mapperTest(ScaffoldConfig config) {
        return String.format(MAPPER_TEST_TEMPLATE, config.basePackage());
    }

    /**
     * Teste do recurso REST.
     *
     * @param config configuração calculada.
     * @return conteúdo do teste.
     */
    public static String resourceTest(ScaffoldConfig config) {
        return resourceTestImproved(config);
    }

    /**
     * Teste unitário do serviço.
     *
     * @param config configuração calculada.
     * @return conteúdo do teste.
     */
    public static String serviceTest(ScaffoldConfig config) {
        return String.format(SERVICE_TEST_TEMPLATE, config.basePackage());
    }

    /**
     * Teste das classes de erro (ProblemDetails, ProblemType, etc.).
     *
     * @param config configuração calculada.
     * @return conteúdo do teste.
     */
    public static String errorsTest(ScaffoldConfig config) {
        return String.format(ERRORS_TEST_TEMPLATE, config.basePackage());
    }

    /**
     * Teste do ControllerAdvice de tratamento de erros.
     *
     * @param config configuração calculada.
     * @return conteúdo do teste.
     */
    public static String controllerAdviceTest(ScaffoldConfig config) {
        return String.format(CONTROLLER_ADVICE_TEST_TEMPLATE, config.basePackage());
    }

    /**
     * Teste melhorado do recurso REST com assertions adequadas.
     *
     * @param config configuração calculada.
     * @return conteúdo do teste melhorado.
     */
    public static String resourceTestImproved(ScaffoldConfig config) {
        return String.format(RESOURCE_TEST_TEMPLATE, config.basePackage());
    }
}

