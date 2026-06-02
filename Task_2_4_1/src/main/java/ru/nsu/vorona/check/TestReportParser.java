package ru.nsu.vorona.check;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ru.nsu.vorona.result.TestStats;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Читает XML-отчёты JUnit
 */
public class TestReportParser {

    /**
     * Парсит отчёты Gradle test
     *
     * @param taskDir директория задачи
     * @return статистика тестов
     */
    public TestStats parse(Path taskDir) {
        Path reportsDir = taskDir.resolve("build/test-results/test");
        if (!Files.isDirectory(reportsDir)) {
            return new TestStats(0, 0, 0);
        }

        int total = 0;
        int failed = 0;
        int skipped = 0;

        try (Stream<Path> files = Files.list(reportsDir)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".xml")).toList()) {
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document document = builder.parse(file.toFile());
                Element root = document.getDocumentElement();

                total += intAttribute(root, "tests");
                failed += intAttribute(root, "failures") + intAttribute(root, "errors");
                skipped += intAttribute(root, "skipped");

                NodeList suites = root.getElementsByTagName("testsuite");
                for (int i = 0; i < suites.getLength(); i++) {
                    Element suite = (Element) suites.item(i);
                    total += intAttribute(suite, "tests");
                    failed += intAttribute(suite, "failures") + intAttribute(suite, "errors");
                    skipped += intAttribute(suite, "skipped");
                }
            }
        } catch (Exception ignored) {
            return new TestStats(0, 0, 0);
        }

        return new TestStats(Math.max(0, total - failed - skipped), failed, skipped);
    }

    private int intAttribute(Element element, String name) {
        if (!element.hasAttribute(name)) {
            return 0;
        }
        return Integer.parseInt(element.getAttribute(name));
    }
}
