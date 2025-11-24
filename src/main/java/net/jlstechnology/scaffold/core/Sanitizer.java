package net.jlstechnology.scaffold.core;

import java.util.regex.Pattern;

/**
 * Conjunto de utilidades para normalização de identificadores e nomes de classes.
 */
public final class Sanitizer {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^A-Za-z0-9]");

    private Sanitizer() {
        // Classe utilitária, não deve ser instanciada.
    }

    /**
     * Converte o artifactId em um segmento válido para uso em pacotes Java.
     *
     * @param artifactId valor informado pelo usuário.
     * @return segmento sanitizado.
     */
    public static String sanitizePackageSegment(String artifactId) {
        String cleaned = NON_ALPHANUMERIC.matcher(artifactId).replaceAll(" ");
        StringBuilder builder = new StringBuilder();
        for (String part : cleaned.trim().split("\\s+")) {
            if (!part.isBlank()) {
                builder.append(part.toLowerCase());
            }
        }
        return builder.length() > 0 ? builder.toString() : "app";
    }

    /**
     * Converte uma cadeia em PascalCase para uso em nomes de classes Java.
     *
     * @param raw valor bruto.
     * @return representação em PascalCase.
     */
    public static String toPascalCase(String raw) {
        String cleaned = NON_ALPHANUMERIC.matcher(raw).replaceAll(" ");
        StringBuilder builder = new StringBuilder();
        for (String part : cleaned.trim().split("\\s+")) {
            if (!part.isBlank()) {
                builder.append(capitalize(part));
            }
        }
        return builder.length() > 0 ? builder.toString() : "App";
    }

    private static String capitalize(String value) {
        if (value.length() == 1) {
            return value.toUpperCase();
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1).toLowerCase();
    }
}

