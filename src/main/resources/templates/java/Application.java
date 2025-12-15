package %1$s;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe responsável por inicializar a aplicação Spring Boot.
 */
@SpringBootApplication
public class %2$sApplication {

    /**
     * Método main que delega o bootstrap ao Spring.
     *
     * @param args argumentos de linha de comando.
     */
    public static void main(String[] args) {
        SpringApplication.run(%2$sApplication.class, args);
    }
}

