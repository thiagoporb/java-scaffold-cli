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

