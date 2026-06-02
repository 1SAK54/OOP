package ru.nsu.vorona;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class BenchmarkMainTest {

    private static final Path OUTPUT_DIR = Paths.get("benchmark_results");

    @AfterEach
    void cleanUp() throws IOException {
        if (Files.exists(OUTPUT_DIR)) {
            Files.walk(OUTPUT_DIR)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @Test
    void mainShouldCreateOutputDirectoryAndFiles() {
        assertDoesNotThrow(() -> BenchmarkMain.main(new String[0]));

        assertTrue(Files.exists(OUTPUT_DIR));
        assertTrue(Files.isDirectory(OUTPUT_DIR));

        assertTrue(Files.exists(OUTPUT_DIR.resolve("small_small.csv")));
        assertTrue(Files.exists(OUTPUT_DIR.resolve("large_small.csv")));
        assertTrue(Files.exists(OUTPUT_DIR.resolve("small_large.csv")));
        assertTrue(Files.exists(OUTPUT_DIR.resolve("large_large.csv")));
        assertTrue(Files.exists(OUTPUT_DIR.resolve("benchmark_report.txt")));
    }

    @Test
    void csvFilesShouldContainHeaderAndData() throws IOException {
        BenchmarkMain.main(new String[0]);

        checkCsvFile(OUTPUT_DIR.resolve("small_small.csv"));
        checkCsvFile(OUTPUT_DIR.resolve("large_small.csv"));
        checkCsvFile(OUTPUT_DIR.resolve("small_large.csv"));
        checkCsvFile(OUTPUT_DIR.resolve("large_large.csv"));
    }

    @Test
    void reportShouldContainAllScenarios() throws IOException {
        BenchmarkMain.main(new String[0]);

        Path reportFile = OUTPUT_DIR.resolve("benchmark_report.txt");
        assertTrue(Files.exists(reportFile));

        String report = Files.readString(reportFile);

        assertTrue(report.contains("=== PRIME CHECKER BENCHMARK REPORT ==="));
        assertTrue(report.contains("SCENARIO: small_small"));
        assertTrue(report.contains("SCENARIO: large_small"));
        assertTrue(report.contains("SCENARIO: small_large"));
        assertTrue(report.contains("SCENARIO: large_large"));
        assertTrue(report.contains("Algorithm"));
        assertTrue(report.contains("Mean(ms)"));
        assertTrue(report.contains("Speedup"));
    }

    @Test
    void csvShouldContainExpectedAlgorithms() throws IOException {
        BenchmarkMain.main(new String[0]);

        String csv = Files.readString(OUTPUT_DIR.resolve("large_large.csv"));

        assertTrue(csv.contains("sequential"));
        assertTrue(csv.contains("threaded"));
        assertTrue(csv.contains("parallel_stream"));
        assertTrue(csv.contains("Sequential"));
        assertTrue(csv.contains("Threaded 1"));
        assertTrue(csv.contains("Threaded 2"));
        assertTrue(csv.contains("Threaded 4"));
        assertTrue(csv.contains("Threaded 8"));
        assertTrue(csv.contains("Parallel Stream"));
    }

    private void checkCsvFile(Path csvFile) throws IOException {
        assertTrue(Files.exists(csvFile));

        String content = Files.readString(csvFile);
        assertFalse(content.isBlank());

        String[] lines = content.split("\\R");
        assertTrue(lines.length > 1);

        assertEquals(
                "scenario,algorithm,thread_count,iteration,time_ns,time_ms,label",
                lines[0]
        );
    }
}