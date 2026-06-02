package ru.nsu.vorona.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты результата этапа
 */
class StageResultTest {

    /**
     * Проверяет успешный этап
     */
    @Test
    void shouldCreateSuccessResult() {
        assertTrue(StageResult.success("compile").isSuccess());
    }

    /**
     * Проверяет неуспешный этап
     */
    @Test
    void shouldCreateFailedResult() {
        assertFalse(StageResult.failed("compile", "error").isSuccess());
    }
}
