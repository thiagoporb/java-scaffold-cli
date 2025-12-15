package net.jlstechnology.scaffold.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utilitário para carregar templates de arquivos de recursos do classpath.
 * Os templates são carregados uma única vez e armazenados em cache.
 */
public final class TemplateLoader {

    private static final String TEMPLATES_BASE_PATH = "templates/";

    private TemplateLoader() {
        // Classe utilitária, não deve ser instanciada.
    }

    /**
     * Carrega um template do classpath.
     *
     * @param path caminho relativo ao diretório templates/ (ex: "docker/postgres-compose.yml")
     * @return conteúdo do template como String
     * @throws IllegalStateException se o template não for encontrado ou houver erro de leitura
     */
    public static String load(String path) {
        String fullPath = TEMPLATES_BASE_PATH + path;
        try (InputStream inputStream = TemplateLoader.class.getClassLoader().getResourceAsStream(fullPath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Template não encontrado: " + fullPath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar template: " + fullPath, e);
        }
    }
}

