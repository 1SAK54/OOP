package ru.nsu.vorona.check;

import org.junit.jupiter.api.Test;
import ru.nsu.vorona.model.CourseConfig;
import ru.nsu.vorona.result.StageResult;
import ru.nsu.vorona.result.TaskCheckResult;
import ru.nsu.vorona.result.TestStats;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты расчёта баллов
 */
class PointsCalculatorTest {

    /**
     * Проверяет расчёт полного балла
     */
    @Test
    void shouldCalculateFullPoints() {
        CourseConfig.TaskInfo task = new CourseConfig.TaskInfo(
                "Task_1",
                "Task",
                10.0,
                LocalDate.parse("2026-01-10"),
                LocalDate.parse("2026-01-20")
        );
        TaskCheckResult result = new TaskCheckResult("Task_1", "Task", 10.0, LocalDate.parse("2026-01-09"));
        result.setCompileResult(StageResult.success("compile"));
        result.setDocsResult(StageResult.success("docs"));
        result.setStyleResult(StageResult.success("style"));
        result.setTestStats(new TestStats(8, 0, 0));

        double points = new PointsCalculator().calculate(task, result, List.of());

        assertEquals(10.0, points, 0.001);
    }

    /**
     * Проверяет штраф после мягкого дедлайна
     */
    @Test
    void shouldApplySoftDeadlinePenalty() {
        CourseConfig.TaskInfo task = new CourseConfig.TaskInfo(
                "Task_1",
                "Task",
                10.0,
                LocalDate.parse("2026-01-10"),
                LocalDate.parse("2026-01-20")
        );
        TaskCheckResult result = new TaskCheckResult("Task_1", "Task", 10.0, LocalDate.parse("2026-01-15"));
        result.setCompileResult(StageResult.success("compile"));
        result.setDocsResult(StageResult.success("docs"));
        result.setStyleResult(StageResult.success("style"));
        result.setTestStats(new TestStats(10, 0, 0));

        double points = new PointsCalculator().calculate(task, result, List.of());

        assertEquals(8.0, points, 0.001);
    }
}
