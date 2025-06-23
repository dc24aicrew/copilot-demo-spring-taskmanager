package com.demo.copilot.taskmanager.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Architecture tests to validate Clean Architecture principles.
 */
class CleanArchitectureTest {

    private final JavaClasses importedClasses = new ClassFileImporter()
            .importPackages("com.demo.copilot.taskmanager");

    @Test
    void domainLayerShouldNotDependOnOtherLayers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..infrastructure..",
                        "..application..",
                        "..presentation.."
                );

        rule.check(importedClasses);
    }

    @Test
    void domainEntitiesShouldNotUseJPAAnnotations() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain.entity..")
                .should().beAnnotatedWith("jakarta.persistence.Entity")
                .orShould().beAnnotatedWith("jakarta.persistence.Table")
                .orShould().beAnnotatedWith("org.springframework.data.annotation.CreatedDate");

        rule.check(importedClasses);
    }

    @Test
    void infrastructureLayerShouldNotDependOnPresentationLayer() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..infrastructure..")
                .should().dependOnClassesThat().resideInAPackage("..presentation..");

        rule.check(importedClasses);
    }

    @Test
    void applicationLayerShouldOnlyDependOnDomain() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..infrastructure..",
                        "..presentation.."
                );

        rule.check(importedClasses);
    }

    @Test
    void repositoryContractsShouldBeInDomainLayer() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*RepositoryContract")
                .should().resideInAPackage("..domain.repository..");

        rule.check(importedClasses);
    }

    @Test
    void useCasesShouldBeInApplicationLayer() {
        ArchRule rule = classes()
                .that().haveNameMatching(".*UseCase")
                .should().resideInAPackage("..application.usecase..");

        rule.check(importedClasses);
    }

    @Test
    void jpaEntitiesShouldBeInInfrastructureLayer() {
        ArchRule rule = classes()
                .that().areAnnotatedWith("jakarta.persistence.Entity")
                .should().resideInAPackage("..infrastructure.persistence.entity..");

        rule.check(importedClasses);
    }
}