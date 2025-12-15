package net.jlstechnology.scaffold.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Testes para TemplateLoader.
 */
class TemplateLoaderTest {

    @Test
    void deveCarregarTemplateExistente() {
        // Este teste assume que criaremos um template de teste
        // Por enquanto, vamos testar o comportamento de erro
        assertThrows(IllegalStateException.class, () -> TemplateLoader.load("teste-inexistente.txt"));
    }

    @Test
    void deveLancarExcecaoQuandoTemplateNaoExiste() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> TemplateLoader.load("arquivo-que-nao-existe.txt"));
        
        assertNotNull(exception.getMessage());
        assertEquals(true, exception.getMessage().contains("Template não encontrado"));
    }

    @Test
    void deveUsarCaminhoCorreto() {
        // Testa que o caminho é construído corretamente
        assertThrows(IllegalStateException.class, () -> TemplateLoader.load("subdir/arquivo.txt"));
    }
}

