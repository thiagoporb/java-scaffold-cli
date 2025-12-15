package net.jlstechnology.scaffold.templates;

import net.jlstechnology.scaffold.core.ExecutionProfile;
import net.jlstechnology.scaffold.core.TemplateLoader;

/**
 * Responsável por gerar os arquivos de configuração YAML.
 */
public final class ApplicationTemplate {

    private static final String BASE_TEMPLATE = TemplateLoader.load("application/application-base.yml");
    private static final String PROFILE_LOCAL_TEMPLATE = TemplateLoader.load("application/application-profile-local.yml");
    private static final String PROFILE_REMOTE_TEMPLATE = TemplateLoader.load("application/application-profile-remote.yml");
    private static final String TEST_TEMPLATE = TemplateLoader.load("application/application-test.yml");

    private ApplicationTemplate() {
        // Classe utilitária, não deve ser instanciada.
    }

    /**
     * Produz o conteúdo do arquivo application.yml comum a todos os ambientes.
     *
     * @return texto configurado.
     */
    public static String renderBase() {
        return BASE_TEMPLATE;
    }

    /**
     * Produz o conteúdo específico de um profile.
     *
     * @param profile profile considerado.
     * @param databaseName nome do banco de dados (geralmente o artifactId em minúsculas).
     * @return configuração YAML dedicada ao profile.
     */
    public static String renderProfile(ExecutionProfile profile, String databaseName) {
        String profileName = profile.profileName();
        
        // Configurações para ambientes locais (dev, docker, docker-externo)
        if (profile == ExecutionProfile.DEV || profile == ExecutionProfile.DOCKER 
                || profile == ExecutionProfile.DOCKER_EXTERNO) {
            return String.format(PROFILE_LOCAL_TEMPLATE, profileName, databaseName);
        }
        
        // Configurações para ambientes remotos (homol, prod)
        return String.format(PROFILE_REMOTE_TEMPLATE, profileName, databaseName);
    }

    /**
     * Produz o conteúdo do arquivo application-test.yml para testes.
     *
     * @return configuração YAML dedicada aos testes.
     */
    public static String renderTest() {
        return TEST_TEMPLATE;
    }
}

