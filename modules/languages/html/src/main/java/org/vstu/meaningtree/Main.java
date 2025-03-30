package org.vstu.meaningtree;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException, TemplateException {
        ClassLoader classLoader = Main.class.getClassLoader();

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_34);
        File templateDir = new File(
                Objects.requireNonNull(classLoader.getResource("templates")).getPath()
        );

        cfg.setDirectoryForTemplateLoading(templateDir);
        cfg.setLogTemplateExceptions(true);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(true);

        /* Create data-model */
        Map<String, String> root = new HashMap<>();
        root.put("name", "John");

        Template template = cfg.getTemplate("hello.ftlh");

        Writer out = new OutputStreamWriter(System.out);
        template.process(root, out);

    }
}