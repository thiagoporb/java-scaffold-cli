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

