package com.groupe2cs.generator.service;

import com.groupe2cs.generator.config.GeneratorProperties;
import com.groupe2cs.generator.model.EntityDefinition;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateControllerGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public UpdateControllerGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        Map<String, Object> context = new HashMap<>(definition.toMap());

        String outputDir = baseDir + "/" + generatorProperties.getControllerPackage();
        context.put("package", Utils.getPackage(outputDir));
        context.put("nameLowercase", definition.getName().toLowerCase());
        context.put("mapperPackage", Utils.getPackage(baseDir + "/" + generatorProperties.getMapperPackage()));
        context.put("dtoPackage", Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()));
        context.put("commandPackage", Utils.getPackage(baseDir + "/" + generatorProperties.getCommandPackage()));

        var fields = definition.getFields();
        context.put("fields", FieldTransformer.transform(fields, definition.getName()));

        String content = templateEngine.render("updateController.mustache", context);
        fileWriterService.write(outputDir, "Update" + definition.getName() + "Controller.java", content);
    }
}
