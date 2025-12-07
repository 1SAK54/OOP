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

    public static List<Long> findInFile(String filename, String pattern) throws IOException {
        return findInFile(Path.of(filename), pattern);
    }

    public static List<Long> findInFile(Path filePath, String pattern) throws IOException {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            return find(reader, pattern);
        }
    }

    public static List<Long> find(Reader reader, String pattern) throws IOException {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern cannot be null or empty");
        }

        List<Long> positions = new ArrayList<>();
        int[] pi = computePrefixFunction(pattern);
        long position = 0;

        int q = 0;
        char[] buffer = new char[4096];
        int charsRead;

        try (BufferedReader bufferedReader = new BufferedReader(reader, 8192)) {
            while ((charsRead = bufferedReader.read(buffer)) != -1) {
                for (int i = 0; i < charsRead; i++) {
                    char currentChar = buffer[i];

                    while (q > 0 && pattern.charAt(q) != currentChar) {
                        q = pi[q - 1];
                    }

                    if (pattern.charAt(q) == currentChar) {
                        q++;
                    }

                    if (q == pattern.length()) {
                        positions.add(position - pattern.length() + 1);
                        q = pi[q - 1];
                    }

                    position++;
                }
            }
        }

        return positions;
    }

    private static int[] computePrefixFunction(String pattern) {
        int n = pattern.length();
        int[] pi = new int[n];
        int k = 0;

        for (int i = 1; i < n; i++) {
            while (k > 0 && pattern.charAt(k) != pattern.charAt(i)) {
                k = pi[k - 1];
            }
            if (pattern.charAt(k) == pattern.charAt(i)) {
                k++;
            }
            pi[i] = k;
        }

        return pi;
    }

    private SubstringSearch() {
        throw new UnsupportedOperationException("Utility class");
    }
}
