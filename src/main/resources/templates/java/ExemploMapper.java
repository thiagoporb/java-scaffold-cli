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

