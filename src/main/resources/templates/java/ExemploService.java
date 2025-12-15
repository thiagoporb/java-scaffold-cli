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

