package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

import static org.junit.jupiter.api.Assertions.*;

class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(Application.class);

    @Test
    void verifiesModularStructure() {
        assertDoesNotThrow(modules::verify);
    }

    @Test
    void createModuleDocumentation() {
        assertDoesNotThrow(() -> new Documenter(modules).writeDocumentation());
    }

}
