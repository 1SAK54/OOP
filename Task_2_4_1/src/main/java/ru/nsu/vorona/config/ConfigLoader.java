package ru.nsu.vorona.config;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import ru.nsu.vorona.dsl.Dsl;
import ru.nsu.vorona.model.CourseConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Загружает Groovy DSL конфигурацию
 */
public class ConfigLoader {
    private final Set<Path> loadedFiles = new HashSet<>();

    /**
     * Загружает конфигурацию из файла
     *
     * @param script файл конфигурации
     * @return конфигурация курса
     * @throws IOException если файл не прочитан
     */
    public CourseConfig load(Path script) throws IOException {
        CourseConfig config = new CourseConfig();
        loadInto(script.toAbsolutePath().normalize(), config);
        return config;
    }

    /**
     * Загружает файл в существующую конфигурацию
     *
     * @param script файл конфигурации
     * @param config конфигурация курса
     * @throws IOException если файл не прочитан
     */
    public void loadInto(Path script, CourseConfig config) throws IOException {
        Path normalized = script.toAbsolutePath().normalize();
        if (loadedFiles.contains(normalized)) {
            return;
        }
        loadedFiles.add(normalized);

        String source = Files.readString(normalized, StandardCharsets.UTF_8);
        Binding binding = new Binding();
        binding.setVariable("dsl", new Dsl(config, this, normalized.getParent()));

        GroovyShell shell = new GroovyShell(getClass().getClassLoader(), binding);
        shell.evaluate("dsl.with {\n" + source + "\n}", normalized.toString());
    }
}
