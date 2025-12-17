package net.jlstechnology.scaffold.cli;

import java.io.Console;
import java.util.Scanner;

/**
 * Cuida da interação com o usuário para coletar valores ausentes na linha de comando.
 */
public final class InteractivePrompter {

    @SuppressWarnings("resource")
    private static final Scanner STANDARD_INPUT = new Scanner(System.in);

    private InteractivePrompter() {
        // Classe utilitária, não deve ser instanciada.
    }

    /**
     * Garante que todas as informações obrigatórias estejam preenchidas, solicitando ao usuário caso necessário.
     *
     * @param arguments argumentos já interpretados.
     * @return argumentos com valores obrigatórios garantidos.
     */
    public static CliArguments resolve(CliArguments arguments) {
        String groupId = ensureValue(arguments.groupId(), "groupId");
        String artifactId = ensureValue(arguments.artifactId(), "artifactId");
        return CliArguments.of(groupId, artifactId, arguments.outputDir(), arguments.force(), arguments.cloud());
    }

    private static String ensureValue(String value, String label) {
        if (value != null && !value.isBlank()) {
            return value.trim();
        }
        return promptUntilFilled(label);
    }

    private static String promptUntilFilled(String label) {
        Console console = System.console();
        if (console != null) {
            return readFromConsole(console, label);
        }
        return readFromScanner(label);
    }

    private static String readFromConsole(Console console, String label) {
        String typed = "";
        while (typed == null || typed.isBlank()) {
            typed = console.readLine("Informe o %s: ", label);
        }
        return typed.trim();
    }

    private static String readFromScanner(String label) {
        String typed = "";
        while (typed == null || typed.isBlank()) {
            System.out.printf("Informe o %s: ", label);
            typed = STANDARD_INPUT.nextLine();
        }
        return typed.trim();
    }
}

