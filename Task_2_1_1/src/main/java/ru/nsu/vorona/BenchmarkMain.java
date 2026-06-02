package ru.nsu.vorona;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class BenchmarkMain {

    private static final int WARMUP_ITERATIONS = 2;
    private static final int MEASURE_ITERATIONS = 5;

    private static final int SMALL_SIZE = 300;
    private static final int LARGE_SIZE = 5000;
    private static final int LARGE_LARGE_SIZE = 1500;

    private static final int SMALL_PRIME_MAX = 100_000;
    private static final int LARGE_PRIME_BIT_LENGTH = 24;

    private static final long SMALL_RANDOM_SEED = 42L;
    private static final long LARGE_RANDOM_SEED = 4242L;

    private static final double Z_90 = 1.645;

    private static final int[] THREAD_COUNTS = {1, 2, 4, 8};
    private static final int PARALLEL_STREAM_THREADS = 8;

    /**
     * Запускает бенчмарк для всех сценариев и сохраняет результаты в файлы.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        try {
            Locale.setDefault(Locale.US);

            Path outputDir = Paths.get("benchmark_results");
            Files.createDirectories(outputDir);

            System.setProperty(
                    "java.util.concurrent.ForkJoinPool.common.parallelism",
                    String.valueOf(PARALLEL_STREAM_THREADS)
            );

            System.out.println("=== PRIME CHECKER BENCHMARK ===");
            System.out.println("Потоки для threaded: 1, 2, 4, 8");
            System.out.println("Параллелизм для parallelStream: " + PARALLEL_STREAM_THREADS);
            System.out.println("Прогревов: " + WARMUP_ITERATIONS);
            System.out.println("Измерений: " + MEASURE_ITERATIONS);
            System.out.println("Папка результатов: " + outputDir.toAbsolutePath());
            System.out.println();

            Map<String, int[]> scenarios = new LinkedHashMap<>();
            scenarios.put("small_small", generateSmallPrimes(SMALL_SIZE, SMALL_PRIME_MAX, SMALL_RANDOM_SEED));
            scenarios.put("large_small", generateSmallPrimes(LARGE_SIZE, SMALL_PRIME_MAX, SMALL_RANDOM_SEED + 1));
            scenarios.put("small_large", generateLargePrimes(SMALL_SIZE, LARGE_PRIME_BIT_LENGTH, LARGE_RANDOM_SEED));
            scenarios.put("large_large", generateLargePrimes(LARGE_LARGE_SIZE, LARGE_PRIME_BIT_LENGTH, LARGE_RANDOM_SEED + 1));

            StringBuilder report = new StringBuilder();
            report.append("=== PRIME CHECKER BENCHMARK REPORT ===\n\n");
            report.append("Threaded thread counts: 1, 2, 4, 8\n");
            report.append("Parallel stream parallelism: ").append(PARALLEL_STREAM_THREADS).append('\n');
            report.append("Warmup iterations: ").append(WARMUP_ITERATIONS).append('\n');
            report.append("Measurement iterations: ").append(MEASURE_ITERATIONS).append('\n');
            report.append("Datasets contain only prime numbers.\n\n");

            for (Map.Entry<String, int[]> scenarioEntry : scenarios.entrySet()) {
                String scenarioName = scenarioEntry.getKey();
                int[] numbers = scenarioEntry.getValue();

                System.out.println(">>> Сценарий: " + scenarioName + " (size = " + numbers.length + ")");

                List<BenchmarkCase> benchmarkCases = buildBenchmarkCases();

                for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                    for (BenchmarkCase benchmarkCase : benchmarkCases) {
                        boolean result = benchmarkCase.checker.hasNonPrime(numbers);
                        if (result) {
                            throw new IllegalStateException("Ожидалось false, так как набор состоит только из простых чисел");
                        }
                    }
                }

                List<ResultRow> resultRows = new ArrayList<>();

                for (int iteration = 1; iteration <= MEASURE_ITERATIONS; iteration++) {
                    for (BenchmarkCase benchmarkCase : benchmarkCases) {
                        long start = System.nanoTime();
                        boolean result = benchmarkCase.checker.hasNonPrime(numbers);
                        long end = System.nanoTime();

                        if (result) {
                            throw new IllegalStateException("Ожидалось false, так как набор состоит только из простых чисел");
                        }

                        resultRows.add(new ResultRow(
                                scenarioName,
                                benchmarkCase.algorithmName,
                                benchmarkCase.threadCount,
                                iteration,
                                end - start
                        ));
                    }
                }

                Path csvFile = outputDir.resolve(scenarioName + ".csv");
                writeRawCsv(csvFile, resultRows);

                report.append("SCENARIO: ").append(scenarioName).append('\n');
                report.append("Array size: ").append(numbers.length).append('\n');
                report.append("First 10 numbers: ").append(preview(numbers, 10)).append('\n');
                report.append('\n');

                appendScenarioStats(report, resultRows);

                report.append('\n');
                report.append("--------------------------------------------------\n\n");

                System.out.println("CSV сохранён: " + csvFile.toAbsolutePath());
            }

            Path reportFile = outputDir.resolve("benchmark_report.txt");
            Files.writeString(reportFile, report.toString());

            System.out.println();
            System.out.println("=== ГОТОВО ===");
            System.out.println("TXT отчёт: " + reportFile.toAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Формирует список вариантов алгоритмов для тестирования.
     *
     * @return список конфигураций бенчмарка
     */
    private static List<BenchmarkCase> buildBenchmarkCases() {
        List<BenchmarkCase> cases = new ArrayList<>();
        cases.add(new BenchmarkCase("sequential", 1, new SequentialPrimeChecker()));

        for (int threads : THREAD_COUNTS) {
            cases.add(new BenchmarkCase("threaded", threads, new ThreadedPrimeChecker(threads)));
        }

        cases.add(new BenchmarkCase("parallel_stream", PARALLEL_STREAM_THREADS, new ParallelStreamPrimeChecker()));
        return cases;
    }

    /**
     * Сохраняет сырые результаты измерений в CSV-файл.
     *
     * @param file путь к файлу
     * @param rows список измерений
     * @throws IOException если произошла ошибка записи
     */
    private static void writeRawCsv(Path file, List<ResultRow> rows) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                file,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            writer.write("scenario,algorithm,thread_count,iteration,time_ns,time_ms,label");
            writer.newLine();

            for (ResultRow row : rows) {
                writer.write(String.format(
                        Locale.US,
                        "%s,%s,%d,%d,%d,%.6f,%s",
                        row.scenarioName,
                        row.algorithmName,
                        row.threadCount,
                        row.iteration,
                        row.elapsedNs,
                        row.elapsedNs / 1_000_000.0,
                        buildLabel(row.algorithmName, row.threadCount)
                ));
                writer.newLine();
            }
        }
    }

    /**
     * Добавляет в текстовый отчёт статистику по сценарию.
     *
     * @param report текст отчёта
     * @param rows сырые результаты измерений
     */
    private static void appendScenarioStats(StringBuilder report, List<ResultRow> rows) {
        Map<String, List<Double>> grouped = new LinkedHashMap<>();

        for (ResultRow row : rows) {
            String key = buildLabel(row.algorithmName, row.threadCount);
            grouped.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(row.elapsedNs / 1_000_000.0);
        }

        List<Double> sequentialValues = grouped.get("Sequential");
        if (sequentialValues == null || sequentialValues.isEmpty()) {
            throw new IllegalStateException("Не найдены результаты для Sequential");
        }

        double sequentialMean = mean(sequentialValues);

        report.append(String.format(
                Locale.US,
                "%-24s %-12s %-12s %-12s %-12s %-12s%n",
                "Algorithm", "Mean(ms)", "Min(ms)", "Max(ms)", "CI90(ms)", "Speedup"
        ));

        for (Map.Entry<String, List<Double>> entry : grouped.entrySet()) {
            List<Double> values = entry.getValue();
            double mean = mean(values);
            double min = min(values);
            double max = max(values);
            double ci90 = confidenceInterval90(values);
            double speedup = sequentialMean / mean;

            report.append(String.format(
                    Locale.US,
                    "%-24s %-12.3f %-12.3f %-12.3f %-12.3f %-12.3f%n",
                    entry.getKey(),
                    mean,
                    min,
                    max,
                    ci90,
                    speedup
            ));
        }
    }

    /**
     * Возвращает удобное имя алгоритма для отчёта и CSV.
     *
     * @param algorithmName внутреннее имя алгоритма
     * @param threadCount количество потоков
     * @return читаемая подпись алгоритма
     */
    private static String buildLabel(String algorithmName, int threadCount) {
        if ("sequential".equals(algorithmName)) {
            return "Sequential";
        }
        if ("threaded".equals(algorithmName)) {
            return "Threaded " + threadCount;
        }
        return "Parallel Stream";
    }

    /**
     * Генерирует массив случайных небольших простых чисел.
     *
     * @param size размер массива
     * @param maxValue верхняя граница поиска простых чисел
     * @param seed начальное значение генератора случайных чисел
     * @return массив простых чисел
     */
    private static int[] generateSmallPrimes(int size, int maxValue, long seed) {
        List<Integer> primes = new ArrayList<>();

        for (int n = 2; n <= maxValue; n++) {
            if (PrimeChecker.isPrime(n)) {
                primes.add(n);
            }
        }

        if (primes.isEmpty()) {
            throw new IllegalStateException("Не удалось сгенерировать малые простые числа");
        }

        Random random = new Random(seed);
        int[] result = new int[size];

        for (int i = 0; i < size; i++) {
            result[i] = primes.get(random.nextInt(primes.size()));
        }

        return result;
    }

    /**
     * Генерирует массив случайных больших простых чисел.
     *
     * @param size размер массива
     * @param bitLength длина числа в битах
     * @param seed начальное значение генератора случайных чисел
     * @return массив простых чисел
     */
    private static int[] generateLargePrimes(int size, int bitLength, long seed) {
        Random random = new Random(seed);
        int[] result = new int[size];

        for (int i = 0; i < size; i++) {
            int value;
            do {
                value = BigInteger.probablePrime(bitLength, random).intValue();
            } while (value <= 1);

            result[i] = value;
        }

        return result;
    }

    /**
     * Формирует строку с первыми элементами массива для отчёта.
     *
     * @param numbers массив чисел
     * @param limit максимальное число выводимых элементов
     * @return строковое представление начала массива
     */
    private static String preview(int[] numbers, int limit) {
        StringBuilder sb = new StringBuilder("[");
        int actualLimit = Math.min(limit, numbers.length);

        for (int i = 0; i < actualLimit; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(numbers[i]);
        }

        if (numbers.length > actualLimit) {
            sb.append(", ...");
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Вычисляет среднее значение списка.
     *
     * @param values список значений
     * @return среднее значение
     */
    private static double mean(List<Double> values) {
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    /**
     * Находит минимальное значение в списке.
     *
     * @param values список значений
     * @return минимальное значение
     */
    private static double min(List<Double> values) {
        double result = Double.POSITIVE_INFINITY;
        for (double value : values) {
            result = Math.min(result, value);
        }
        return result;
    }

    /**
     * Находит максимальное значение в списке.
     *
     * @param values список значений
     * @return максимальное значение
     */
    private static double max(List<Double> values) {
        double result = Double.NEGATIVE_INFINITY;
        for (double value : values) {
            result = Math.max(result, value);
        }
        return result;
    }

    /**
     * Вычисляет выборочное стандартное отклонение.
     *
     * @param values список значений
     * @return стандартное отклонение
     */
    private static double standardDeviation(List<Double> values) {
        if (values.size() < 2) {
            return 0.0;
        }

        double mean = mean(values);
        double sum = 0.0;

        for (double value : values) {
            double diff = value - mean;
            sum += diff * diff;
        }

        return Math.sqrt(sum / (values.size() - 1));
    }

    /**
     * Вычисляет 90%-й доверительный интервал.
     *
     * @param values список значений
     * @return половина ширины доверительного интервала
     */
    private static double confidenceInterval90(List<Double> values) {
        if (values.size() < 2) {
            return 0.0;
        }

        double stdDev = standardDeviation(values);
        return Z_90 * stdDev / Math.sqrt(values.size());
    }

    private static class BenchmarkCase {
        private final String algorithmName;
        private final int threadCount;
        private final PrimeChecker checker;

        /**
         * Создаёт описание одного варианта бенчмарка.
         *
         * @param algorithmName имя алгоритма
         * @param threadCount количество потоков
         * @param checker объект проверки
         */
        private BenchmarkCase(String algorithmName, int threadCount, PrimeChecker checker) {
            this.algorithmName = algorithmName;
            this.threadCount = threadCount;
            this.checker = checker;
        }
    }

    private static class ResultRow {
        private final String scenarioName;
        private final String algorithmName;
        private final int threadCount;
        private final int iteration;
        private final long elapsedNs;

        /**
         * Создаёт одну строку результата измерения.
         *
         * @param scenarioName имя сценария
         * @param algorithmName имя алгоритма
         * @param threadCount количество потоков
         * @param iteration номер измерения
         * @param elapsedNs время выполнения в наносекундах
         */
        private ResultRow(String scenarioName, String algorithmName, int threadCount, int iteration, long elapsedNs) {
            this.scenarioName = scenarioName;
            this.algorithmName = algorithmName;
            this.threadCount = threadCount;
            this.iteration = iteration;
            this.elapsedNs = elapsedNs;
        }
    }
}