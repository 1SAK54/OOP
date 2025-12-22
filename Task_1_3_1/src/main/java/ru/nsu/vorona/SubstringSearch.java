package ru.nsu.vorona;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Поиск всех вхождений подстроки в тексте с помощью алгоритма Knuth-Morris-Pratt.
 * Поддерживает файлы любого размера и UTF-8 кодировку.
 */
public final class SubstringSearch {

    /**
     * Размер буфера чтения (4KB)
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Размер буферизованного ридера (8KB)
     * Нужен для более быстрой обработки больший файлов
     */
    private static final int BUFFERED_READER_SIZE = 8192;

    /**
     * Поиск подстроки в файле по имени.
     *
     * @param filename путь к файлу
     * @param pattern искомая подстрока
     * @return список позиций вхождений (Unicode символы)
     * @throws IOException при ошибке чтения файла
     */
    public static List<Long> findInFile(String filename, String pattern) throws IOException {
        return findInFile(Path.of(filename), pattern);
    }

    /**
     * Поиск подстроки в файле по пути.
     *
     * @param filePath путь к файлу
     * @param pattern искомая подстрока
     * @return список позиций вхождений (Unicode символы)
     * @throws IOException при ошибке чтения файла
     */
    public static List<Long> findInFile(Path filePath, String pattern) throws IOException {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            return find(reader, pattern);
        }
    }

    /**
     * Вычисляет префиксную функцию для массива Unicode code points.
     *
     * @param patternCps массив code points паттерна
     * @return массив префиксной функции pi
     */
    private static int[] computePrefixFunctionForCodePoints(int[] patternCps) {
        int n = patternCps.length;
        int[] pi = new int[n];
        int k = 0;
        for (int i = 1; i < n; i++) {
            while (k > 0 && patternCps[k] != patternCps[i]) {
                k = pi[k - 1];
            }
            if (patternCps[k] == patternCps[i]) {
                k++;
            }
            pi[i] = k;
        }
        return pi;
    }

    /**
     * Потоковый поиск всех вхождений подстроки в тексте с помощью алгоритма Knuth-Morris-Pratt.
     *
     * @param reader источник текста (файл, строка, поток)
     * @param pattern искомая подстрока (не null, не пустая)
     * @return список позиций начала вхождений (Unicode code points)
     * @throws IllegalArgumentException если pattern = null или пустая
     * @throws IOException при ошибке чтения из reader
     */
    public static List<Long> find(Reader reader, String pattern) throws IOException {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern cannot be null or empty");
        }

        int patternCpCount = pattern.codePointCount(0, pattern.length());
        int[] patternCps = new int[patternCpCount];
        for (int i = 0, cpIndex = 0; i < pattern.length(); cpIndex++) {
            int cp = pattern.codePointAt(i);
            patternCps[cpIndex] = cp;
            i += Character.charCount(cp);
        }
        int[] pi = computePrefixFunctionForCodePoints(patternCps);

        List<Long> positions = new ArrayList<>();
        int q = 0;
        long position = 0;

        char[] buffer = new char[BUFFER_SIZE];
        int charsRead;

        try (BufferedReader br = new BufferedReader(reader, BUFFERED_READER_SIZE)) {
            while ((charsRead = br.read(buffer)) != -1) {
                for (int i = 0; i < charsRead; ) {
                    int codePoint = buffer[i] >= Character.MIN_HIGH_SURROGATE &&
                            buffer[i] <= Character.MAX_HIGH_SURROGATE &&
                            i + 1 < charsRead &&
                            Character.isLowSurrogate(buffer[i + 1])
                            ? Character.toCodePoint(buffer[i], buffer[i + 1])
                            : buffer[i];

                    char firstChar = buffer[i];

                    while (q > 0 && pattern.charAt(q) != firstChar) {
                        q = pi[q - 1];
                    }
                    if (pattern.charAt(q) == firstChar) {
                        q++;
                    }

                    if (q == patternCpCount) {
                        positions.add(position - patternCpCount + 1);
                        q = pi[q - 1];
                    }

                    i += Character.charCount(codePoint);
                    position++;
                }
            }
        }

        return positions;
    }

    /**
     * Запрещаем создание экземпляров утилитного класса
     */
    private SubstringSearch() {
        throw new UnsupportedOperationException("Utility class");
    }
}
