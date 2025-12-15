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

