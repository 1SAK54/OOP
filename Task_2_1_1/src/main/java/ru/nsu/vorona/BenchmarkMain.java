package ru.nsu.vorona;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

/**
 * Task_2_1_1: Benchmark поиска непростого числа.
 * Записывает результаты в performance_results.csv и benchmark_report.txt
 */
public class BenchmarkMain {

    private static final String CSV_FILE = "performance_results.csv";
    private static final String TXT_FILE = "benchmark_report.txt";

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        System.out.println("=== Task_2_1_1 Benchmark ===\n");
        System.out.println("Файлы:");
        System.out.println("  " + CSV_FILE + " ← график");
        System.out.println("  " + TXT_FILE + " ← анализ\n");

        try (PrintWriter csvWriter = new PrintWriter(new FileWriter(CSV_FILE, StandardCharsets.UTF_8));
             PrintWriter txtWriter = new PrintWriter(new FileWriter(TXT_FILE, StandardCharsets.UTF_8))) {

            csvWriter.println("Method,Threads,Time_s,Speedup");

            // Генерация данных
            System.out.print("Генерация 10 000 больших простых... ");
            int[] testData = generateLargePrimes(10_000);
            System.out.println("Готово!\n");

            int cores = Runtime.getRuntime().availableProcessors();
            txtWriter.printf("=== Система ===%n");
            txtWriter.printf("Ядер CPU: %d%n", cores);
            txtWriter.printf("Тестовый набор: %d простых чисел (~10^7)%n%n", testData.length);

            warmUp(testData);

            // Бенчмарк
            System.out.println("=== Результаты ===\n");

            double seqTime = testMethod("Sequential", new SequentialPrimeChecker(),
                    testData, 1, 0.0, csvWriter, txtWriter);

            double t2Time = testMethod("Threaded", new ThreadedPrimeChecker(2),
                    testData, 2, seqTime, csvWriter, txtWriter);

            double t4Time = testMethod("Threaded", new ThreadedPrimeChecker(4),
                    testData, 4, seqTime, csvWriter, txtWriter);

            double t8Time = testMethod("Threaded", new ThreadedPrimeChecker(8),
                    testData, 8, seqTime, csvWriter, txtWriter);

            double psTime = testMethod("ParallelStream", new ParallelStreamPrimeChecker(),
                    testData, cores, seqTime, csvWriter, txtWriter);

            // Анализ
            analyze(seqTime, t2Time, t4Time, t8Time, psTime, cores, txtWriter);

            System.out.println("\nГотово! Проверь файлы:");
            System.out.println("   " + CSV_FILE);
            System.out.println("   " + TXT_FILE);

        } catch (IOException e) {
            System.err.println("Ошибка записи файлов: " + e.getMessage());
        }
    }

    private static double testMethod(String method, PrimeChecker checker,
                                     int[] data, int threads, double seqBaseline,
                                     PrintWriter csv, PrintWriter txt) {
        double timeSeconds = measureAverage(checker, data);

        double speedup = threads == 1 ? 1.0 : seqBaseline / timeSeconds;

        csv.printf(Locale.US, "%s,%d,%.4f,%.2f%n", method, threads, timeSeconds, speedup);
        txt.printf("%-15s (%d пот.): %6.4f с  |  ×%.2fx%n",
                method, threads, timeSeconds, speedup);
        System.out.printf("   %-15s (%d пот.): %6.4f с  |  ×%.2fx%n",
                method, threads, timeSeconds, speedup);

        return timeSeconds;
    }

    private static double measureAverage(PrimeChecker checker, int[] data) {
        // Прогрев
        for (int i = 0; i < 5; i++) {
            checker.hasNonPrime(data);
        }

        // 10 замеров
        long[] times = new long[10];
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            checker.hasNonPrime(data);
            times[i] = System.nanoTime() - start;
        }

        return Arrays.stream(times).average().getAsDouble() / 1_000_000_000.0;
    }

    private static void analyze(double seq, double t2, double t4, double t8, double ps,
                                int cores, PrintWriter txt) {
        double bestTime = Math.min(Math.min(t2, t4), Math.min(t8, ps));
        double maxSpeedup = seq / bestTime;

        txt.println("\n=== Итоговый анализ ===");
        txt.printf("Последовательное время: %.4f с%n", seq);
        txt.printf("Лучшее параллельное:   %.4f с (×%.2fx)%n", bestTime, maxSpeedup);
        txt.printf("Эффективность на %d ядрах: %.1f%%%n", cores, maxSpeedup / cores * 100);
        txt.printf("Рекомендация: Threaded-%d (оптимально)%n",
                (t2 < t4 ? 2 : t4 < t8 ? 4 : 8));
    }

    private static void warmUp(int[] data) {
        PrimeChecker[] checkers = {
                new SequentialPrimeChecker(),
                new ThreadedPrimeChecker(4),
                new ParallelStreamPrimeChecker()
        };

        for (PrimeChecker checker : checkers) {
            for (int i = 0; i < 3; i++) {
                checker.hasNonPrime(data);
            }
        }
    }

    /** Генерация БОЛЬШИХ простых чисел ~10^7 */
    private static int[] generateLargePrimes(int count) {
        int[] primes = new int[count];
        int num = 10_000_000; // старт с 10M
        int found = 0;

        while (found < count) {
            if (PrimeChecker.isPrime(num)) {
                primes[found++] = num;
            }
            num++;
        }
        return primes;
    }
}
