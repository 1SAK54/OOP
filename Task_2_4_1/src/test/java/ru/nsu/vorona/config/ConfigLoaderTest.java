package ru.nsu.vorona.config;

import org.junit.jupiter.api.Test;
import ru.nsu.vorona.model.CourseConfig;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты загрузки DSL-конфигурации
 */
class ConfigLoaderTest {

    /**
     * Проверяет загрузку конфигурации
     *
     * @throws Exception если файл не создан
     */
    @Test
    void shouldLoadDslConfig() throws Exception {
        Path dir = Files.createTempDirectory("oop-dsl-test");
        Path config = dir.resolve("oop-checker.groovy");
        Files.writeString(config, """
                task('Task_1') {
                    title 'First task'
                    maxPoints 10
                    softDeadline '2026-01-10'
                    hardDeadline '2026-01-20'
                }

                group('24215') {
                    student 'Ivan Ivanov', github: 'ivanov', repo: 'https://github.com/ivanov/OOP.git'
                }

                check {
                    tasks 'Task_1'
                    groups '24215'
                }

                checkpoint('KT1') {
                    date '2026-01-25'
                }
                """);

        CourseConfig result = new ConfigLoader().load(config);

        assertEquals(1, result.getTasks().size());
        assertEquals("First task", result.getTasks().get("Task_1").title());
        assertEquals(1, result.getGroups().get("24215").students().size());
        assertTrue(result.getCheckRequest().getTaskIds().contains("Task_1"));
        assertEquals(1, result.getCheckpoints().size());
    }
}
