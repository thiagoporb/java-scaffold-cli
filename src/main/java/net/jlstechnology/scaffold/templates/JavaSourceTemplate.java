package net.jlstechnology.scaffold.templates;

import net.jlstechnology.scaffold.core.ScaffoldConfig;

/**
 * Agrupa os templates relacionados às classes Java principais geradas pelo scaffold.
 */
public final class JavaSourceTemplate {

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
        return """
                package %1$s;

                import org.springframework.boot.SpringApplication;
                import org.springframework.boot.autoconfigure.SpringBootApplication;

                /**
                 * Classe responsável por inicializar a aplicação Spring Boot.
                 */
                @SpringBootApplication
                public class %2$sApplication {

                    /**
                     * Método main que delega o bootstrap ao Spring.
                     *
                     * @param args argumentos de linha de comando.
                     */
                    public static void main(String[] args) {
                        SpringApplication.run(%2$sApplication.class, args);
                    }
                }
                """.formatted(config.basePackage(), config.artifactPascalCase());
    }

    /**
     * Modelo de entidade exemplo.
     *
     * @param config configuração calculada.
     * @return conteúdo da classe.
     */
    public static String exemploModel(ScaffoldConfig config) {
        return """
                package %1$s.model;

                import jakarta.persistence.Column;
                import jakarta.persistence.Entity;
                import jakarta.persistence.Id;
                import jakarta.persistence.Table;
                import java.util.UUID;
                import lombok.Getter;
                import lombok.Setter;

                /**
                 * Entidade de exemplo utilizada como base para novos modelos de domínio.
                 */
                @Getter
                @Setter
                @Entity
                @Table(name = "TB_EXEMPLO")
                public class ExemploModel {

                    /**
                     * Identificador único da entidade.
                     */
                    @Id
                    @Column(name = "ID_EXEMPLO", nullable = false)
                    private UUID id;

                    /**
                     * Valor demonstrativo. Substitua pelo atributo real do domínio.
                     */
                    @Column(name = "DS_VALOR", nullable = false, length = 120)
                    private String valor;
                }
                """.formatted(config.basePackage());
    }

    /**
     * Interface de repositório exemplo.
     *
     * @param config configuração calculada.
     * @return conteúdo da interface.
     */
    public static String exemploRepository(ScaffoldConfig config) {
        return """
                package %1$s.repository;

                import java.util.UUID;
                import org.springframework.data.repository.CrudRepository;
                import org.springframework.stereotype.Repository;
                import %1$s.model.ExemploModel;

                /**
                 * Repositório de exemplo responsável por operações de persistência da entidade {@link ExemploModel}.
                 */
                @Repository
                public interface ExemploRepository extends CrudRepository<ExemploModel, UUID> {
                }
                """.formatted(config.basePackage());
    }

    /**
     * Interface de mapper exemplo.
     *
     * @param config configuração calculada.
     * @return conteúdo da interface de mapper.
     */
    public static String exemploMapperInterface(ScaffoldConfig config) {
        return """
                package %1$s.service.mapper;

                import %1$s.service.dto.ExemploDTO;
                import %1$s.model.ExemploModel;

                /**
                 * Responsável por converter dados entre {@link ExemploModel} e {@link ExemploDTO}.
                 */
                public interface ExemploMapper {

                    /**
                     * Converte um {@link ExemploModel} em {@link ExemploDTO}.
                     *
                     * @param model entidade de origem.
                     * @return instância equivalente para transporte.
                     */
                    ExemploDTO toDto(ExemploModel model);

                    /**
                     * Converte um {@link ExemploDTO} em {@link ExemploModel}.
                     *
                     * @param dto objeto de transporte.
                     * @return entidade pronta para persistência.
                     */
                    ExemploModel toModel(ExemploDTO dto);
                }
                """.formatted(config.basePackage());
    }

    /**
     * Implementação concreta do mapper.
     *
     * @param config configuração calculada.
     * @return conteúdo da classe de implementação.
     */
    public static String exemploMapperImpl(ScaffoldConfig config) {
        return """
                package %1$s.service.mapper;

                import java.util.UUID;
                import %1$s.model.ExemploModel;
                import %1$s.service.dto.ExemploDTO;

                /**
                 * Classe utilitária para conversão entre {@link ExemploModel} e {@link ExemploDTO}.
                 * Utiliza métodos estáticos para simplificar o uso e melhorar a performance.
                 */
                public final class ExemploMapper {

                    private ExemploMapper() {
                        // Classe utilitária, não deve ser instanciada.
                    }

                    /**
                     * Converte um {@link ExemploModel} em {@link ExemploDTO}.
                     *
                     * @param model entidade de origem.
                     * @return instância equivalente para transporte.
                     */
                    public static ExemploDTO toDTO(ExemploModel model) {
                        if (model == null) {
                            return null;
                        }
                        ExemploDTO dto = new ExemploDTO();
                        dto.setId(model.getId());
                        dto.setValor(model.getValor());
                        return dto;
                    }

                    /**
                     * Converte um {@link ExemploDTO} em {@link ExemploModel}.
                     *
                     * @param dto objeto de transporte.
                     * @return entidade pronta para persistência.
                     */
                    public static ExemploModel toModel(ExemploDTO dto) {
                        if (dto == null) {
                            return null;
                        }
                        ExemploModel model = new ExemploModel();
                        UUID id = dto.getId();
                        model.setId(id != null ? id : UUID.randomUUID());
                        model.setValor(dto.getValor());
                        return model;
                    }
                }
                """.formatted(config.basePackage());
    }

    /**
     * Serviço exemplo.
     *
     * @param config configuração calculada.
     * @return conteúdo da classe de serviço.
     */
    public static String exemploService(ScaffoldConfig config) {
        return """
                package %1$s.service;

                import java.util.List;
                import java.util.UUID;
                import java.util.stream.StreamSupport;
                import org.springframework.stereotype.Service;
                import org.springframework.transaction.annotation.Transactional;
                import %1$s.model.ExemploModel;
                import %1$s.repository.ExemploRepository;
                import %1$s.service.dto.ExemploDTO;
                import %1$s.service.mapper.ExemploMapper;
                import %1$s.web.rest.errors.ResourceNotFoundProblem;
                import static %1$s.service.mapper.ExemploMapper.toDTO;
                import static %1$s.service.mapper.ExemploMapper.toModel;

                /**
                 * Serviço responsável pelas regras de negócio relacionadas a {@link ExemploModel}.
                 */
                @Service
                public class ExemploService {

    private final ExemploRepository exemploRepository;

    /**
     * Cria o serviço com suas dependências.
     *
     * @param exemploRepository repositório para acesso ao banco.
     */
    public ExemploService(ExemploRepository exemploRepository) {
        this.exemploRepository = exemploRepository;
    }

                    /**
                     * Persiste um novo registro a partir do DTO recebido.
                     *
                     * @param dto dados de entrada.
                     * @return DTO resultante após a persistência.
                     */
    @Transactional
    public ExemploDTO salvar(ExemploDTO dto) {
        ExemploModel salvo = exemploRepository.save(toModel(dto));
        return toDTO(salvo);
    }

                    /**
                     * Busca um registro pelo identificador informado.
                     *
                     * @param id identificador único.
                     * @return DTO correspondente.
                     */
    @Transactional(readOnly = true)
    public ExemploDTO buscarPorId(UUID id) {
        return exemploRepository
                .findById(id)
                .map(ExemploMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundProblem("Exemplo", String.valueOf(id)));
    }

                    /**
                     * Lista todos os registros disponíveis.
                     *
                     * @return lista de exemplos existentes.
                     */
    @Transactional(readOnly = true)
    public List<ExemploDTO> listarTodos() {
        return StreamSupport.stream(exemploRepository.findAll().spliterator(), false)
                .map(ExemploMapper::toDTO)
                .toList();
    }
                }
                """.formatted(config.basePackage());
    }

    /**
     * DTO de exemplo.
     *
     * @param config configuração calculada.
     * @return conteúdo da classe DTO.
     */
    public static String exemploDto(ScaffoldConfig config) {
        return """
                package %1$s.service.dto;

                import java.util.UUID;

                /**
                 * Objeto de transporte de dados para {@code ExemploModel}.
                 */
                public class ExemploDTO {

                    /**
                     * Identificador único do exemplo.
                     */
                    private UUID id;

                    /**
                     * Valor demonstrativo associado ao exemplo.
                     */
                    private String valor;

                    /**
                     * Obtém o identificador único.
                     *
                     * @return identificador.
                     */
                    public UUID getId() {
                        return id;
                    }

                    /**
                     * Define o identificador único.
                     *
                     * @param id identificador a ser atribuído.
                     */
                    public void setId(UUID id) {
                        this.id = id;
                    }

                    /**
                     * Obtém o valor demonstrativo.
                     *
                     * @return valor textual.
                     */
                    public String getValor() {
                        return valor;
                    }

                    /**
                     * Define o valor demonstrativo.
                     *
                     * @param valor valor a ser atribuído.
                     */
                    public void setValor(String valor) {
                        this.valor = valor;
                    }
                }
                """.formatted(config.basePackage());
    }

    /**
     * Recurso REST de exemplo.
     *
     * @param config configuração calculada.
     * @return conteúdo da classe REST.
     */
    public static String exemploResource(ScaffoldConfig config) {
        String basePackage = config.basePackage();
        return """
                package %1$s.web.rest;

                import java.net.URI;
                import java.util.UUID;
                import java.util.List;
                import org.springframework.http.ResponseEntity;
                import org.springframework.web.bind.annotation.RestController;
                import %1$s.service.ExemploService;
                import %1$s.service.dto.ExemploDTO;
                import %1$s.web.api.ExemploApi;
                import %1$s.web.api.dto.RequestExemploDTO;
                import %1$s.web.api.dto.ResponseExemploDTO;

                /**
                 * Implementação do contrato OpenAPI gerado automaticamente para recursos de exemplo.
                 */
                @RestController
                public class ExemploResource implements ExemploApi {

                    private final ExemploService exemploService;

                    /**
                     * Construtor que injeta o serviço necessário.
                     *
                     * @param exemploService componente responsável pelas regras de negócio.
                     */
                    public ExemploResource(ExemploService exemploService) {
                        this.exemploService = exemploService;
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public ResponseEntity<ResponseExemploDTO> criarExemplo(RequestExemploDTO requestExemploDTO) {
                        ExemploDTO dto = toInternalDto(requestExemploDTO);
                        ExemploDTO salvo = exemploService.salvar(dto);
                        URI location = URI.create("/api/v1/exemplos/" + salvo.getId());
                        return ResponseEntity.created(location).body(toResponseDto(salvo));
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public ResponseEntity<List<ResponseExemploDTO>> listarExemplos() {
                        List<ResponseExemploDTO> exemplos = exemploService.listarTodos().stream()
                                .map(this::toResponseDto)
                                .toList();
                        return ResponseEntity.ok(exemplos);
                    }

                    private ExemploDTO toInternalDto(RequestExemploDTO requestDto) {
                        if (requestDto == null) {
                            return null;
                        }
                        ExemploDTO dto = new ExemploDTO();
                        dto.setValor(requestDto.getValor());
                        return dto;
                    }

                    private ResponseExemploDTO toResponseDto(ExemploDTO dto) {
                        if (dto == null) {
                            return null;
                        }
                        ResponseExemploDTO responseDto = new ResponseExemploDTO();
                        if (dto.getId() != null) {
                            responseDto.setId(dto.getId());
                        }
                        responseDto.setValor(dto.getValor());
                        return responseDto;
                    }
                }
                """.formatted(basePackage, basePackage);
    }

    public static String problemType(ScaffoldConfig config) {
        return """
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
                """.formatted(config.basePackage());
    }

    public static String problemDetails(ScaffoldConfig config) {
        return """
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
                """.formatted(config.basePackage());
    }

    public static String problemDetailsException(ScaffoldConfig config) {
        return """
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
                """.formatted(config.basePackage());
    }

    public static String resourceNotFoundProblem(ScaffoldConfig config) {
        return """
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
                """.formatted(config.basePackage());
    }

    public static String validationProblem(ScaffoldConfig config) {
        return """
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
                """.formatted(config.basePackage());
    }

    public static String businessRuleProblem(ScaffoldConfig config) {
        return """
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
                """.formatted(config.basePackage());
    }

    public static String problemDetailsControllerAdvice(ScaffoldConfig config) {
        return """
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
                """.formatted(config.basePackage());
    }

    public static String gatlingSimulation(ScaffoldConfig config) {
        return """
                package %1$s.gatling.simulation;

                import static io.gatling.javaapi.core.CoreDsl.*;
                import static io.gatling.javaapi.http.HttpDsl.*;

                import io.gatling.javaapi.core.Simulation;
                import io.gatling.javaapi.core.ScenarioBuilder;
                import io.gatling.javaapi.http.HttpProtocolBuilder;
                import java.time.Duration;

                /**
                 * Simulação Gatling básica para validar o endpoint {@code /api/v1/exemplos}.
                 */
                public class BasicSimulation extends Simulation {

                    private static final HttpProtocolBuilder HTTP_PROTOCOL = http
                            .baseUrl("http://localhost:8080")
                            .acceptHeader("application/json")
                            .contentTypeHeader("application/json")
                            .acceptEncodingHeader("gzip, deflate")
                            .userAgentHeader("Gatling Simulation");

                    private static final ScenarioBuilder SCENARIO =
                            scenario("Listagem inicial")
                                    .exec(
                                            http("Listar Exemplos")
                                                    .get("/api/v1/exemplos")
                                                    .check(status().is(200)));

                    {
                        setUp(
                                        SCENARIO.injectOpen(
                                                atOnceUsers(5),
                                                rampUsers(20).during(Duration.ofSeconds(15))))
                                .protocols(HTTP_PROTOCOL);
                    }
                }
                """.formatted(config.basePackage());
    }
}

