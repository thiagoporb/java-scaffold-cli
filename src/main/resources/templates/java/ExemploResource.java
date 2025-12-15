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

