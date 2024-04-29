package com.example.samplemvc;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(
        packages = "com.example.samplemvc"
)
public class ArchitectureTest {

    private final JavaClasses importedClasses = new ClassFileImporter().importPackages("com.example.samplemvc");


    @Test
    public void testLayeredArchitecture() {

        layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Controllers").definedBy("..controller..")
                .layer("Services").definedBy("..service..")
                .layer("Repositories").definedBy("..repository..")
                .whereLayer("Controllers").mayNotBeAccessedByAnyLayer()
                .whereLayer("Services").mayOnlyBeAccessedByLayers("Controllers")
                .whereLayer("Repositories").mayOnlyBeAccessedByLayers("Services")
                .check(importedClasses);

    }


    @Test
    public void testServiceClassesShouldBeAnnotatedWithService() {
        ArchRule serviceClassesShouldBeAnnotated = classes()
                .that().resideInAPackage("..service..")
                .should().beAnnotatedWith(Service.class);

        serviceClassesShouldBeAnnotated.check(importedClasses);
    }

    @Test
    public void testRepositoryClassesShouldBeAnnotatedWithRepository() {
        ArchRule repositoryClassesShouldBeAnnotated = classes()
                .that().resideInAPackage("..repository..")
                .should().beAnnotatedWith(Repository.class);

        repositoryClassesShouldBeAnnotated.check(importedClasses);
    }

}