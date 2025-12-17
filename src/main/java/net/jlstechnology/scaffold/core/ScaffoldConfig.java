package net.jlstechnology.scaffold.core;

import net.jlstechnology.scaffold.cli.CliArguments;
import java.nio.file.Path;

/**
 * Mantém os valores derivados das entradas do usuário necessários para geração do projeto.
 *
 * @param groupId identificador do grupo Maven.
 * @param artifactId identificador do artefato Maven.
 * @param artifactPackageSegment segmento sanitizado do artifactId.
 * @param basePackage pacote raiz do projeto.
 * @param artifactPascalCase representação PascalCase do artifactId.
 * @param artifactLowerCase representação minúscula para uso em arquivos.
 * @param projectRoot diretório raiz a ser criado.
 * @param basePackagePath caminho relativo utilizado para criação dos pacotes.
 * @param cloud provider de cloud (ex: "aws"), opcional.
 */
public record ScaffoldConfig(
        String groupId,
        String artifactId,
        String artifactPackageSegment,
        String basePackage,
        String artifactPascalCase,
        String artifactLowerCase,
        Path projectRoot,
        Path basePackagePath,
        String cloud) {

    /**
     * Cria a configuração a partir dos argumentos fornecidos.
     *
     * @param arguments argumentos resolvidos.
     * @return configuração completa.
     */
    public static ScaffoldConfig from(CliArguments arguments) {
        String artifactPackageSegment = Sanitizer.sanitizePackageSegment(arguments.artifactId());
        String basePackage = arguments.groupId() + "." + artifactPackageSegment;
        String artifactPascal = Sanitizer.toPascalCase(arguments.artifactId());
        String artifactLower = arguments.artifactId().toLowerCase();
        Path projectRoot = arguments.outputDir().resolve(arguments.artifactId()).toAbsolutePath().normalize();
        Path basePackagePath = Path.of(basePackage.replace(".", "/"));
        return new ScaffoldConfig(
                arguments.groupId(),
                arguments.artifactId(),
                artifactPackageSegment,
                basePackage,
                artifactPascal,
                artifactLower,
                projectRoot,
                basePackagePath,
                arguments.cloud());
    }
}

