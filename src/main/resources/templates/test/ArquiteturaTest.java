package %1$s.arquitetura;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Valida a arquitetura básica do projeto conforme os padrões estabelecidos.
 */
class ArquiteturaTest {

    private static final String PACOTE_BASE = "%1$s";

    @Test
    void classesEmModelDevemTerminarComModel() {
        JavaClasses importedClasses = importar();
        ArchRule rule = classes()
                .that().resideInAPackage(PACOTE_BASE + ".model..")
                .should().haveSimpleNameEndingWith("Model");
        rule.check(importedClasses);
    }

    @Test
    void classesEmRepositoryDevemTerminarComRepository() {
        JavaClasses importedClasses = importar();
        ArchRule rule = classes()
                .that().resideInAPackage(PACOTE_BASE + ".repository..")
                .should().haveSimpleNameEndingWith("Repository");
        rule.check(importedClasses);
    }

    @Test
    void classesEmServiceDevemTerminarComService() {
        JavaClasses importedClasses = importar();
        ArchRule rule = classes()
                .that().resideInAPackage(PACOTE_BASE + ".service..")
                .and().resideOutsideOfPackage(PACOTE_BASE + ".service.dto..")
                .and().resideOutsideOfPackage(PACOTE_BASE + ".service.mapper..")
                .and().haveSimpleNameNotEndingWith("Test")
                .should().haveSimpleNameEndingWith("Service");
        rule.check(importedClasses);
    }

    @Test
    void classesEmRestDevemTerminarComResource() {
        JavaClasses importedClasses = importar();
        ArchRule rule = classes()
                .that().resideInAPackage(PACOTE_BASE + ".web.rest..")
                .and().haveSimpleNameNotEndingWith("Test")
                .and().resideOutsideOfPackage(PACOTE_BASE + ".web.rest.errors..")
                .should().haveSimpleNameEndingWith("Resource");
        rule.check(importedClasses);
    }

    private JavaClasses importar() {
        return new ClassFileImporter().importPackages(PACOTE_BASE);
    }
}

