package net.jlstechnology.scaffold.core;

/**
 * Enumera os perfis Maven suportados pelo scaffold.
 */
public enum ExecutionProfile {
    DEV("dev"),
    HOMOL("homol"),
    PROD("prod"),
    DOCKER("docker"),
    DOCKER_EXTERNO("docker-externo");

    private final String name;

    ExecutionProfile(String name) {
        this.name = name;
    }

    /**
     * Obtém o identificador utilizado nas configurações.
     *
     * @return nome do profile.
     */
    public String profileName() {
        return name;
    }
}

