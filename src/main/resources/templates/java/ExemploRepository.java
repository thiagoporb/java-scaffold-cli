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

