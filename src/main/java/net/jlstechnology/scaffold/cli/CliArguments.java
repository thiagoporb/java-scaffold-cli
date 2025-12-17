package net.jlstechnology.scaffold.cli;

import java.nio.file.Path;

/**
 * Representa os argumentos informados pelo usuário na execução do CLI.
 *
 * @param groupId   identificador de grupo Maven.
 * @param artifactId identificador do artefato Maven.
 * @param outputDir diretório de saída.
 * @param force     indica se o conteúdo existente deve ser sobrescrito.
 * @param cloud     provider de cloud (ex: "aws"), opcional.
 */
public record CliArguments(
        String groupId,
        String artifactId,
        Path outputDir,
        boolean force,
        String cloud) {

    /**
     * Cria uma instância de {@link CliArguments} garantindo valores padrão para campos opcionais.
     *
     * @param groupId valor informado para o groupId.
     * @param artifactId valor informado para o artifactId.
     * @param outputDir caminho de saída, opcional.
     * @param force indica se sobrescreve diretório existente.
     * @param cloud provider de cloud, opcional.
     * @return instância normalizada com valores padrão aplicados.
     */
    public static CliArguments of(String groupId, String artifactId, Path outputDir, boolean force, String cloud) {
        Path normalizedOutput = outputDir != null ? outputDir : Path.of(".");
        return new CliArguments(groupId, artifactId, normalizedOutput, force, cloud);
    }
}

