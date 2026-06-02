package ru.nsu.vorona.result;

/**
 * Статистика тестов
 */
public record TestStats(int passed, int failed, int skipped) {

    public int total() {
        return passed + failed + skipped;
    }
}
