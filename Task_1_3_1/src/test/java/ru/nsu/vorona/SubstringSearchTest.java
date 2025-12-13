package ru.nsu.vorona;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Тесты для поиска подстроки с помощью KMP алгоритма.
 */
class SubstringSearchTest {

    @Test
    void testEmptyPattern() {
        assertThrows(IllegalArgumentException.class,
                () -> SubstringSearch.find(new StringReader("test"), ""));
    }

    @Test
    void testPatternLongerThanText() {
        List<Long> result = assertDoesNotThrow(() ->
                SubstringSearch.find(new StringReader("abc"), "abcdef"));
        assertTrue(result.isEmpty());
    }

    @Test
    void testSingleCharacterMatch() throws IOException {
        List<Long> result = SubstringSearch.find(new StringReader("abc"), "b");
        assertEquals(List.of(1L), result);
    }

    @Test
    void testMultipleMatches() throws IOException {
        List<Long> result = SubstringSearch.find(new StringReader("aaa aaa aaa"), "aaa");
        assertEquals(List.of(0L, 4L, 8L), result);
    }

    @Test
    void testOverlappingMatches() throws IOException {
        List<Long> result = SubstringSearch.find(new StringReader("aaaaaaa"), "aaa");
        assertEquals(List.of(0L, 1L, 2L, 3L, 4L), result);
    }

    @Test
    void testNoMatches() throws IOException {
        List<Long> result = SubstringSearch.find(new StringReader("abc"), "xyz");
        assertTrue(result.isEmpty());
    }

    @Test
    void testUTF8Characters() throws IOException {
        String text = "Привет, мир! Hello, world! привет, мир!";
        String pattern = "мир";
        List<Long> result = SubstringSearch.find(new StringReader(text), pattern);
        assertEquals(List.of(8L, 35L), result);
    }

    @Test
    void testExactMatch() throws IOException {
        List<Long> result = SubstringSearch.find(new StringReader("hello"), "hello");
        assertEquals(List.of(0L), result);
    }

    @Test
    void testCaseSensitive() throws IOException {
        List<Long> result = SubstringSearch.find(new StringReader("AaAaA"), "A");
        assertEquals(List.of(0L, 2L, 4L), result);

        List<Long> resultLower = SubstringSearch.find(new StringReader("AaAaA"), "a");
        assertEquals(List.of(1L, 3L), resultLower);
    }

    @Test
    void testFileWithNewlines() throws IOException {
        Path tempFile = Path.of("src/test/resources/test-input.txt");
        List<Long> result = SubstringSearch.findInFile(tempFile, "test");
        assertFalse(result.isEmpty());
    }

    @Test
    void testLargeFile(@TempDir Path tempDir) throws IOException {
        // Генерируем большой файл
        Path largeFile = tempDir.resolve("large.txt");
        StringBuilder content = new StringBuilder();
        String pattern = "TEST123";

        // Создаём повторяющийся текст
        for (int i = 0; i < 10000; i++) {
            content.append("Some text ").append(pattern).append(" more text\n");
        }

        Files.writeString(largeFile, content.toString());

        List<Long> result = SubstringSearch.findInFile(largeFile, pattern);

        // Проверяем, что найдены все вхождения
        assertEquals(10000, result.size());

        // Проверяем несколько первых позиций
        assertEquals(10L, result.get(0).longValue()); // Первое вхождение
    }

    @Test
    void unicodeChars() throws IOException {
        String s = "\uD83C\uDF27\uD83C\uDF27\uD83D\uDE0A";
        System.out.println(s);
        String pattern = "\uD83C\uDF27";
        System.out.println(pattern);
        List<Long> positions = SubstringSearch.find(new StringReader(s), pattern);
        assertEquals(List.of(0L,1L), positions);

    }
}