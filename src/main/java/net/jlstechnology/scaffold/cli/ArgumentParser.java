package net.jlstechnology.scaffold.cli;

import java.nio.file.Path;

/**
 * Responsável por interpretar os argumentos de linha de comando fornecidos ao CLI.
 */
public final class ArgumentParser {

    private static final String GROUP_ID_LONG = "--groupId";
    private static final String GROUP_ID_LONG_ALT = "--group-id";
    private static final String ARTIFACT_ID_LONG = "--artifactId";
    private static final String ARTIFACT_ID_LONG_ALT = "--artifact-id";
    private static final String OUTPUT_DIR_LONG = "--outputDir";
    private static final String OUTPUT_DIR_LONG_ALT = "--output-dir";
    private static final String FORCE_LONG = "--force";
    private static final String CLOUD_LONG = "--cloud";

    /**
     * Analisa os parâmetros fornecidos e devolve uma representação estruturada.
     *
     * @param args vetor de argumentos.
     * @return instância de {@link CliArguments} com os valores informados.
     */
    public CliArguments parse(String[] args) {
        String groupId = null;
        String artifactId = null;
        Path outputDir = null;
        boolean force = false;
        String cloud = null;

        for (int index = 0; index < args.length; index++) {
            String current = args[index];
            switch (current) {
                case GROUP_ID_LONG, GROUP_ID_LONG_ALT -> {
                    groupId = nextValue(args, index, current);
                    index++;
                }
                case ARTIFACT_ID_LONG, ARTIFACT_ID_LONG_ALT -> {
                    artifactId = nextValue(args, index, current);
                    index++;
                }
                case OUTPUT_DIR_LONG, OUTPUT_DIR_LONG_ALT -> {
                    String pathValue = nextValue(args, index, current);
                    outputDir = Path.of(pathValue);
                    index++;
                }
                case FORCE_LONG -> force = true;
                case CLOUD_LONG -> {
                    String cloudValue = nextValue(args, index, current);
                    if (!"aws".equals(cloudValue)) {
                        throw new IllegalArgumentException("O argumento --cloud aceita apenas 'aws' como valor. Valor informado: " + cloudValue);
                    }
                    cloud = cloudValue;
                    index++;
                }
                default -> throw new IllegalArgumentException("Argumento desconhecido: " + current);
            }
        }

        return CliArguments.of(groupId, artifactId, outputDir, force, cloud);
    }

    private String nextValue(String[] args, int currentIndex, String argumentName) {
        int valueIndex = currentIndex + 1;
        if (valueIndex >= args.length) {
            throw new IllegalArgumentException("O argumento " + argumentName + " requer um valor.");
        }
        return args[valueIndex];
    }
}

