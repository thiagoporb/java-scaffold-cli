package net.jlstechnology.scaffold;

import net.jlstechnology.scaffold.cli.ArgumentParser;
import net.jlstechnology.scaffold.cli.CliArguments;
import net.jlstechnology.scaffold.cli.InteractivePrompter;
import net.jlstechnology.scaffold.core.ProjectGenerator;
import net.jlstechnology.scaffold.core.ScaffoldConfig;

/**
 * Ponto de entrada da aplicação CLI responsável por orquestrar o fluxo de geração.
 */
public final class Main {

    private Main() {
        // Classe utilitária, não deve ser instanciada.
    }

    /**
     * Executa o fluxo principal do CLI.
     *
     * @param rawArgs argumentos de linha de comando recebidos.
     */
    public static void main(String[] rawArgs) {
        try {
            ArgumentParser parser = new ArgumentParser();
            CliArguments parsed = parser.parse(rawArgs);
            CliArguments resolved = InteractivePrompter.resolve(parsed);

            ScaffoldConfig config = ScaffoldConfig.from(resolved);
            ProjectGenerator generator = new ProjectGenerator();
            generator.generate(config, resolved.force());

            System.out.printf("%n✅ Projeto gerado com sucesso!%n📂 Caminho: %s%n", config.projectRoot());
        } catch (Exception exception) {
            System.err.printf("❌ Erro ao gerar o projeto: %s%n", exception.getMessage());
            System.exit(1);
        }
    }
}

