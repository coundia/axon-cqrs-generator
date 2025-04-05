package com.groupe2cs.generator.tests.services;

import com.groupe2cs.generator.config.GeneratorProperties;
import com.groupe2cs.generator.model.EntityDefinition;
import com.groupe2cs.generator.service.FindByIdentifyControllerGeneratorService;
import com.groupe2cs.generator.tests.config.GeneratorPropertiesTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(classes = {GeneratorPropertiesTestConfig.class})
public class FindByIdentifyControllerTests {

    @Autowired
    FindByIdentifyControllerGeneratorService service;

    @Autowired
    GeneratorProperties generatorProperties;

    @Test
    void it_should_generate_find_by_identify_controller_file(@TempDir Path tempDir) throws Exception {
        Path templatesDir = tempDir.resolve("templates");
        Files.createDirectories(templatesDir);
        Files.writeString(
                templatesDir.resolve("findByIdentifyController.mustache"),
                "package {{package}};\n\npublic class FindBy{{identifier.nameCapitalized}}{{name}}Controller {}"
        );

        EntityDefinition definition = EntityDefinition.fromClass(MockEntity.class);
        service.generate(definition, tempDir.toString());

        File file = tempDir.resolve(generatorProperties.getControllerPackage() + "/FindByIdMockEntityController.java").toFile();
        assertThat(file).exists();

        String content = Files.readString(file.toPath());
        assertThat(content).contains("public class FindByIdMockEntityController");
    }
}
