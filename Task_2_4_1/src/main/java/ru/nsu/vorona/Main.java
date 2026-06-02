package ru.nsu.vorona;

import ru.nsu.vorona.check.RepositoryChecker;
import ru.nsu.vorona.config.ConfigLoader;
import ru.nsu.vorona.model.CourseConfig;
import ru.nsu.vorona.report.HtmlReportGenerator;
import ru.nsu.vorona.result.CheckReport;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Точка входа приложения
 */
public class Main {
    private static final String DEFAULT_CONFIG = "oop-checker.groovy";

    /**
     * Запускает проверку
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        Path configPath = Path.of(args.length > 0 ? args[0] : DEFAULT_CONFIG);
        if (!Files.exists(configPath)) {
            System.err.println("Config file not found: " + configPath.toAbsolutePath());
            return;
        }

        try {
            ConfigLoader loader = new ConfigLoader();
            CourseConfig config = loader.load(configPath);
            CheckReport report = new RepositoryChecker().check(config);
            String html = new HtmlReportGenerator().generate(report);

            System.out.println(html);

            Files.writeString(
                    Path.of("report.html"),
                    html,
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            System.err.println("Checker failed: " + e.getMessage());
        }
    }
}
