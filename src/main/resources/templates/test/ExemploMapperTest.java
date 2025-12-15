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

