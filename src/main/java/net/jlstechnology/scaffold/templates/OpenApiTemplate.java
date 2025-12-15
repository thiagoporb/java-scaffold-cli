package net.jlstechnology.scaffold.templates;

import net.jlstechnology.scaffold.core.ExecutionProfile;
import net.jlstechnology.scaffold.core.TemplateLoader;

/**
 * Constrói os contratos OpenAPI baseados nos profiles definidos.
 */
public final class OpenApiTemplate {

    private static final String PROFILE_TEMPLATE = TemplateLoader.load("openapi/openapi-profile.yaml");

    private OpenApiTemplate() {
        // Classe utilitária, não deve ser instanciada.
    }

    /**
     * Gera o conteúdo do arquivo OpenAPI para um profile específico.
     *
     * @param profile profile considerado.
     * @return contrato em formato YAML.
     */
    public static String render(ExecutionProfile profile) {
        String profileName = profile.profileName();
        return String.format(PROFILE_TEMPLATE, profileName);
    }

}

