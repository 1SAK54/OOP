package ru.nsu.vorona.client.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты строки лидерборда
 */
class LeaderboardItemTest {

    /**
     * Проверяет создание строки лидерборда
     */
    @Test
    void shouldCreateLeaderboardItem() {
        LeaderboardItem item = new LeaderboardItem(1, "Alice", 10);

        assertEquals(1, item.getPlayerId());
        assertEquals("Alice", item.getNickname());
        assertEquals(10, item.getScore());
    }
}