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

