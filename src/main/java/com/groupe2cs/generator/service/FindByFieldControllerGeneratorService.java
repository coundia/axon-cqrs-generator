package com.groupe2cs.generator.service;

import com.groupe2cs.generator.config.GeneratorProperties;
import com.groupe2cs.generator.model.EntityDefinition;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FindByFieldControllerGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public FindByFieldControllerGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        Map<String, Object> context = new HashMap<>(definition.toMap());

        String outputDir = baseDir + "/" + generatorProperties.getControllerPackage();
        context.put("package", Utils.getPackage(outputDir));
        context.put("nameLowercase", definition.getName().toLowerCase());
        context.put("queryPackage", Utils.getPackage(baseDir + "/" + generatorProperties.getQueryPackage()));
        context.put("dtoPackage", Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()));

        var fields = definition.getFields();
        context.put("fields", FieldTransformer.transform(fields, definition.getName()));

        fields = fields.stream().filter(
                p -> p.isFilable()
        ).toList();

        for (var field : fields) {
            Map<String, Object> fieldContext = new HashMap<>(context);
            field.setNameCapitalized(capitalize(field.getName()));
            fieldContext.put("field", field);
            String className = "FindBy" + capitalize(field.getName()) + definition.getName() + "Controller";
            fieldContext.put("className", className);
            String content = templateEngine.render("findByFieldController.mustache", fieldContext);
            fileWriterService.write(outputDir, className+".java", content);
        }
    }

    private String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
