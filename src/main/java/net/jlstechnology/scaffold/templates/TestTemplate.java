package net.jlstechnology.scaffold.templates;

import net.jlstechnology.scaffold.core.ScaffoldConfig;

/**
 * Templates dedicados aos testes gerados automaticamente.
 */
public final class TestTemplate {

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
        return """
                package %1$s;

                import org.junit.jupiter.api.Test;
                import org.springframework.boot.test.context.SpringBootTest;

                /**
                 * Verifica se o contexto Spring é carregado utilizando as configurações de teste.
                 */
                @SpringBootTest
                class %2$sApplicationTests {

                    @Test
                    void contextoCarregado() {
                        // Teste intencionalmente vazio: garante carregamento do contexto.
                    }
                }
                """.formatted(config.basePackage(), config.artifactPascalCase());
    }

    /**
     * Testes de arquitetura com ArchUnit.
     *
     * @param config configuração calculada.
     * @return conteúdo do teste de arquitetura.
     */
    public static String arquiteturaTest(ScaffoldConfig config) {
        return """
                package %1$s.arquitetura;

                import com.tngtech.archunit.core.domain.JavaClasses;
                import com.tngtech.archunit.core.importer.ClassFileImporter;
                import com.tngtech.archunit.lang.ArchRule;
                import org.junit.jupiter.api.Test;

                import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

                /**
                 * Valida a arquitetura básica do projeto conforme os padrões estabelecidos.
                 */
                class ArquiteturaTest {

                    private static final String PACOTE_BASE = "%1$s";

                    @Test
                    void classesEmModelDevemTerminarComModel() {
                        JavaClasses importedClasses = importar();
                        ArchRule rule = classes()
                                .that().resideInAPackage(PACOTE_BASE + ".model..")
                                .should().haveSimpleNameEndingWith("Model");
                        rule.check(importedClasses);
                    }

                    @Test
                    void classesEmRepositoryDevemTerminarComRepository() {
                        JavaClasses importedClasses = importar();
                        ArchRule rule = classes()
                                .that().resideInAPackage(PACOTE_BASE + ".repository..")
                                .should().haveSimpleNameEndingWith("Repository");
                        rule.check(importedClasses);
                    }

                    @Test
                    void classesEmServiceDevemTerminarComService() {
                        JavaClasses importedClasses = importar();
                        ArchRule rule = classes()
                                .that().resideInAPackage(PACOTE_BASE + ".service..")
                                .and().resideOutsideOfPackage(PACOTE_BASE + ".service.dto..")
                                .and().resideOutsideOfPackage(PACOTE_BASE + ".service.mapper..")
                                .and().haveSimpleNameNotEndingWith("Test")
                                .should().haveSimpleNameEndingWith("Service");
                        rule.check(importedClasses);
                    }

                    @Test
                    void classesEmRestDevemTerminarComResource() {
                        JavaClasses importedClasses = importar();
                        ArchRule rule = classes()
                                .that().resideInAPackage(PACOTE_BASE + ".web.rest..")
                                .and().haveSimpleNameNotEndingWith("Test")
                                .and().resideOutsideOfPackage(PACOTE_BASE + ".web.rest.errors..")
                                .should().haveSimpleNameEndingWith("Resource");
                        rule.check(importedClasses);
                    }

                    private JavaClasses importar() {
                        return new ClassFileImporter().importPackages(PACOTE_BASE);
                    }
                }
                """.formatted(config.basePackage());
    }

    /**
     * Teste unitário do mapper.
     *
     * @param config configuração calculada.
     * @return conteúdo do teste.
     */
    public static String mapperTest(ScaffoldConfig config) {
        return """
                package %1$s.service.mapper;

                import static org.junit.jupiter.api.Assertions.assertEquals;
                import static org.junit.jupiter.api.Assertions.assertNotNull;
                import static org.junit.jupiter.api.Assertions.assertNull;

                import java.util.UUID;
                import org.junit.jupiter.api.Test;
                import %1$s.model.ExemploModel;
                import %1$s.service.dto.ExemploDTO;
                import static %1$s.service.mapper.ExemploMapper.toDTO;
                import static %1$s.service.mapper.ExemploMapper.toModel;

                /**
                 * Garante que o mapeamento bidirecional funcione corretamente.
                 */
                class ExemploMapperTest {

                    @Test
                    void deveConverterModelParaDto() {
                        ExemploModel model = new ExemploModel();
                        model.setId(UUID.randomUUID());
                        model.setValor("teste");

                        ExemploDTO dto = toDTO(model);

                        assertNotNull(dto);
                        assertEquals(model.getId(), dto.getId());
                        assertEquals(model.getValor(), dto.getValor());
                    }

                    @Test
                    void deveConverterDtoParaModel() {
                        ExemploDTO dto = new ExemploDTO();
                        dto.setId(UUID.randomUUID());
                        dto.setValor("teste");

                        ExemploModel model = toModel(dto);

                        assertNotNull(model);
                        assertEquals(dto.getId(), model.getId());
                        assertEquals(dto.getValor(), model.getValor());
                    }

                    @Test
                    void deveGerarIdAutomaticamenteQuandoDtoNaoTemId() {
                        ExemploDTO dto = new ExemploDTO();
                        dto.setValor("teste");

                        ExemploModel model = toModel(dto);

                        assertNotNull(model);
                        assertNotNull(model.getId());
                        assertEquals(dto.getValor(), model.getValor());
                    }

                    @Test
                    void deveRetornarNullQuandoModelEhNull() {
                        ExemploDTO dto = toDTO(null);
                        assertNull(dto);
                    }

                    @Test
                    void deveRetornarNullQuandoDtoEhNull() {
                        ExemploModel model = toModel(null);
                        assertNull(model);
                    }

                    @Test
                    void deveManterIdQuandoDtoJaTemId() {
                        UUID id = UUID.randomUUID();
                        ExemploDTO dto = new ExemploDTO();
                        dto.setId(id);
                        dto.setValor("teste");

                        ExemploModel model = toModel(dto);

                        assertNotNull(model);
                        assertEquals(id, model.getId());
                        assertEquals("teste", model.getValor());
                    }
                }
                """.formatted(config.basePackage());
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
        return """
                package %1$s.service;

                import static org.junit.jupiter.api.Assertions.assertEquals;
                import static org.junit.jupiter.api.Assertions.assertNotNull;
                import static org.junit.jupiter.api.Assertions.assertThrows;
                import static org.mockito.Mockito.verify;
                import static org.mockito.Mockito.when;

                import java.util.List;
                import java.util.Optional;
                import java.util.UUID;
                import org.junit.jupiter.api.Test;
                import org.junit.jupiter.api.extension.ExtendWith;
                import org.mockito.InjectMocks;
                import org.mockito.Mock;
                import org.mockito.junit.jupiter.MockitoExtension;
                import %1$s.model.ExemploModel;
                import %1$s.repository.ExemploRepository;
                import %1$s.service.dto.ExemploDTO;
                import %1$s.web.rest.errors.ResourceNotFoundProblem;

                /**
                 * Garante que o serviço funcione corretamente com seus métodos.
                 */
                @ExtendWith(MockitoExtension.class)
                class ExemploServiceTest {

                    @Mock
                    private ExemploRepository exemploRepository;

                    @InjectMocks
                    private ExemploService exemploService;

                    @Test
                    void deveSalvarNovoRegistro() {
                        ExemploDTO dto = new ExemploDTO();
                        dto.setValor("teste");

                        ExemploModel modelSalvo = new ExemploModel();
                        UUID id = UUID.randomUUID();
                        modelSalvo.setId(id);
                        modelSalvo.setValor("teste");

                        when(exemploRepository.save(org.mockito.ArgumentMatchers.any(ExemploModel.class)))
                                .thenReturn(modelSalvo);

                        ExemploDTO resultado = exemploService.salvar(dto);

                        assertNotNull(resultado);
                        assertEquals(id, resultado.getId());
                        assertEquals("teste", resultado.getValor());
                        verify(exemploRepository).save(org.mockito.ArgumentMatchers.any(ExemploModel.class));
                    }

                    @Test
                    void deveBuscarPorIdQuandoExiste() {
                        UUID id = UUID.randomUUID();
                        ExemploModel model = new ExemploModel();
                        model.setId(id);
                        model.setValor("teste");

                        when(exemploRepository.findById(id)).thenReturn(Optional.of(model));

                        ExemploDTO resultado = exemploService.buscarPorId(id);

                        assertNotNull(resultado);
                        assertEquals(id, resultado.getId());
                        assertEquals("teste", resultado.getValor());
                        verify(exemploRepository).findById(id);
                    }

                    @Test
                    void deveLancarExcecaoQuandoNaoEncontrado() {
                        UUID id = UUID.randomUUID();

                        when(exemploRepository.findById(id)).thenReturn(Optional.empty());

                        assertThrows(ResourceNotFoundProblem.class, () -> exemploService.buscarPorId(id));
                        verify(exemploRepository).findById(id);
                    }

                    @Test
                    void deveListarTodosOsRegistros() {
                        ExemploModel model1 = new ExemploModel();
                        UUID id1 = UUID.randomUUID();
                        model1.setId(id1);
                        model1.setValor("teste1");

                        ExemploModel model2 = new ExemploModel();
                        UUID id2 = UUID.randomUUID();
                        model2.setId(id2);
                        model2.setValor("teste2");

                        when(exemploRepository.findAll())
                                .thenReturn(List.of(model1, model2));

                        List<ExemploDTO> resultado = exemploService.listarTodos();

                        assertNotNull(resultado);
                        assertEquals(2, resultado.size());
                        assertEquals(id1, resultado.get(0).getId());
                        assertEquals("teste1", resultado.get(0).getValor());
                        assertEquals(id2, resultado.get(1).getId());
                        assertEquals("teste2", resultado.get(1).getValor());
                        verify(exemploRepository).findAll();
                    }
                }
                """.formatted(config.basePackage());
    }

    /**
     * Teste das classes de erro (ProblemDetails, ProblemType, etc.).
     *
     * @param config configuração calculada.
     * @return conteúdo do teste.
     */
    public static String errorsTest(ScaffoldConfig config) {
        return """
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
                """.formatted(config.basePackage());
    }

    /**
     * Teste do ControllerAdvice de tratamento de erros.
     *
     * @param config configuração calculada.
     * @return conteúdo do teste.
     */
    public static String controllerAdviceTest(ScaffoldConfig config) {
        return """
                package %1$s.web.rest.errors;

                import static org.junit.jupiter.api.Assertions.assertEquals;
                import static org.junit.jupiter.api.Assertions.assertNotNull;
                import static org.mockito.Mockito.mock;
                import static org.mockito.Mockito.when;

                import java.net.URI;
                import java.util.List;
                import org.junit.jupiter.api.BeforeEach;
                import org.junit.jupiter.api.Test;
                import org.springframework.http.HttpStatus;
                import org.springframework.http.ResponseEntity;
                import org.springframework.web.context.request.WebRequest;

                /**
                 * Garante que o ControllerAdvice trate as exceções corretamente.
                 */
                class ProblemDetailsControllerAdviceTest {

                    private ProblemDetailsControllerAdvice controllerAdvice;
                    private WebRequest webRequest;

                    @BeforeEach
                    void setUp() {
                        controllerAdvice = new ProblemDetailsControllerAdvice();
                        webRequest = mock(WebRequest.class);
                    }

                    @Test
                    void deveTratarResourceNotFoundProblem() {
                        ResourceNotFoundProblem exception = new ResourceNotFoundProblem("Exemplo", "123");
                        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/exemplos/123");

                        ResponseEntity<ProblemDetails> response = controllerAdvice.handleStructuredProblem(exception, webRequest);

                        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                        assertNotNull(response.getBody());
                        assertEquals(ProblemType.RESOURCE_NOT_FOUND.getUri(), response.getBody().getType());
                        assertEquals(404, response.getBody().getStatus());
                    }

                    @Test
                    void deveTratarValidationProblem() {
                        ProblemDetails.Violation violation = new ProblemDetails.Violation("campo", "erro");
                        ValidationProblem exception = new ValidationProblem(List.of(violation));
                        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/exemplos");

                        ResponseEntity<ProblemDetails> response = controllerAdvice.handleStructuredProblem(exception, webRequest);

                        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                        assertNotNull(response.getBody());
                        assertEquals(ProblemType.VALIDATION.getUri(), response.getBody().getType());
                        assertEquals(1, response.getBody().getInvalidParams().size());
                    }

                    @Test
                    void deveTratarBusinessRuleProblem() {
                        BusinessRuleProblem exception = new BusinessRuleProblem("Regra violada");
                        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/exemplos");

                        ResponseEntity<ProblemDetails> response = controllerAdvice.handleStructuredProblem(exception, webRequest);

                        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
                        assertNotNull(response.getBody());
                        assertEquals(ProblemType.BUSINESS_RULE.getUri(), response.getBody().getType());
                    }

                    @Test
                    void deveTratarExceptionGenerica() {
                        Exception exception = new RuntimeException("Erro inesperado");
                        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/exemplos");

                        ResponseEntity<ProblemDetails> response = controllerAdvice.handleUnexpected(exception, webRequest);

                        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                        assertNotNull(response.getBody());
                        assertEquals(ProblemType.INTERNAL.getUri(), response.getBody().getType());
                        assertEquals(500, response.getBody().getStatus());
                    }

                    @Test
                    void deveConstruirInstanceUriCorretamente() {
                        BusinessRuleProblem exception = new BusinessRuleProblem("Erro");
                        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/exemplos/123");

                        ResponseEntity<ProblemDetails> response = controllerAdvice.handleStructuredProblem(exception, webRequest);

                        assertNotNull(response.getBody());
                        assertEquals(URI.create("/api/v1/exemplos/123"), response.getBody().getInstance());
                    }

                    @Test
                    void deveUsarUriPadraoQuandoDescriptionNaoComecaComUri() {
                        BusinessRuleProblem exception = new BusinessRuleProblem("Erro");
                        when(webRequest.getDescription(false)).thenReturn("invalid description");

                        ResponseEntity<ProblemDetails> response = controllerAdvice.handleStructuredProblem(exception, webRequest);

                        assertNotNull(response.getBody());
                        assertEquals(URI.create("/"), response.getBody().getInstance());
                    }

                    @Test
                    void deveUsarUriPadraoQuandoDescriptionEhNull() {
                        BusinessRuleProblem exception = new BusinessRuleProblem("Erro");
                        when(webRequest.getDescription(false)).thenReturn(null);

                        ResponseEntity<ProblemDetails> response = controllerAdvice.handleStructuredProblem(exception, webRequest);

                        assertNotNull(response.getBody());
                        assertEquals(URI.create("/"), response.getBody().getInstance());
                    }
                }
                """.formatted(config.basePackage());
    }

    /**
     * Teste melhorado do recurso REST com assertions adequadas.
     *
     * @param config configuração calculada.
     * @return conteúdo do teste melhorado.
     */
    public static String resourceTestImproved(ScaffoldConfig config) {
        return """
                package %1$s.web.rest;

                import static org.junit.jupiter.api.Assertions.assertEquals;
                import static org.junit.jupiter.api.Assertions.assertNotNull;
                import static org.mockito.ArgumentMatchers.any;
                import static org.mockito.ArgumentMatchers.isNull;
                import static org.mockito.Mockito.verify;
                import static org.mockito.Mockito.when;

                import java.net.URI;
                import java.util.List;
                import java.util.UUID;
                import org.junit.jupiter.api.Test;
                import org.junit.jupiter.api.extension.ExtendWith;
                import org.mockito.InjectMocks;
                import org.mockito.Mock;
                import org.mockito.junit.jupiter.MockitoExtension;
                import org.springframework.http.HttpStatus;
                import org.springframework.http.ResponseEntity;
                import %1$s.service.ExemploService;
                import %1$s.service.dto.ExemploDTO;
                import %1$s.web.api.dto.RequestExemploDTO;
                import %1$s.web.api.dto.ResponseExemploDTO;

                /**
                 * Verifica o fluxo básico do recurso REST de exemplo.
                 */
                @ExtendWith(MockitoExtension.class)
                class ExemploResourceTest {

                    @Mock
                    private ExemploService exemploService;

                    @InjectMocks
                    private ExemploResource recurso;

                    @Test
                    void deveDelegarCriacaoAoServico() {
                        RequestExemploDTO requestDto = new RequestExemploDTO();
                        requestDto.setValor("valor");

                        ExemploDTO salvo = new ExemploDTO();
                        UUID id = UUID.randomUUID();
                        salvo.setId(id);
                        salvo.setValor("valor");

                        when(exemploService.salvar(any(ExemploDTO.class))).thenReturn(salvo);

                        ResponseEntity<ResponseExemploDTO> resposta = recurso.criarExemplo(requestDto);

                        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
                        assertNotNull(resposta.getBody());
                        assertEquals(id, resposta.getBody().getId());
                        assertEquals("valor", resposta.getBody().getValor());
                        URI location = resposta.getHeaders().getLocation();
                        assertNotNull(location);
                        assertEquals("/api/v1/exemplos/" + id, location.toString());
                        verify(exemploService).salvar(any(ExemploDTO.class));
                    }

                    @Test
                    void deveListarRegistrosUtilizandoServico() {
                        ExemploDTO dto = new ExemploDTO();
                        UUID id = UUID.randomUUID();
                        dto.setId(id);
                        dto.setValor("valor");

                        when(exemploService.listarTodos()).thenReturn(List.of(dto));

                        ResponseEntity<List<ResponseExemploDTO>> resposta = recurso.listarExemplos();

                        assertEquals(HttpStatus.OK, resposta.getStatusCode());
                        assertNotNull(resposta.getBody());
                        assertEquals(1, resposta.getBody().size());
                        assertEquals(id, resposta.getBody().get(0).getId());
                        assertEquals("valor", resposta.getBody().get(0).getValor());
                        verify(exemploService).listarTodos();
                    }

                    @Test
                    void deveTratarRequestNull() {
                        ExemploDTO dtoSalvo = new ExemploDTO();
                        UUID id = UUID.randomUUID();
                        dtoSalvo.setId(id);
                        dtoSalvo.setValor(null);

                        when(exemploService.salvar(isNull())).thenReturn(dtoSalvo);

                        ResponseEntity<ResponseExemploDTO> resposta = recurso.criarExemplo(null);

                        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
                        assertNotNull(resposta.getBody());
                        verify(exemploService).salvar(isNull());
                    }

                    @Test
                    void deveTratarListaVazia() {
                        when(exemploService.listarTodos()).thenReturn(List.of());

                        ResponseEntity<List<ResponseExemploDTO>> resposta = recurso.listarExemplos();

                        assertEquals(HttpStatus.OK, resposta.getStatusCode());
                        assertNotNull(resposta.getBody());
                        assertEquals(0, resposta.getBody().size());
                        verify(exemploService).listarTodos();
                    }
                }
                """.formatted(config.basePackage());
    }
}

