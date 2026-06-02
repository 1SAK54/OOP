package ru.nsu.vorona.check;

import ru.nsu.vorona.model.CourseConfig;
import ru.nsu.vorona.result.TaskCheckResult;
import ru.nsu.vorona.result.TestStats;

import java.time.LocalDate;
import java.util.List;

/**
 * Вычисляет баллы за задачу
 */
public class PointsCalculator {

    /**
     * Вычисляет балл за задачу
     *
     * @param task задача
     * @param result результат проверки
     * @param bonuses дополнительные баллы
     * @return балл
     */
    public double calculate(
            CourseConfig.TaskInfo task,
            TaskCheckResult result,
            List<CourseConfig.BonusInfo> bonuses
    ) {
        if (result.getCompileResult() == null || !result.getCompileResult().isSuccess()) {
            return bonusPoints(task.id(), bonuses);
        }

        double points = task.maxPoints() * 0.30;

        if (result.getDocsResult() != null && result.getDocsResult().isSuccess()) {
            points += task.maxPoints() * 0.10;
        }

        points += task.maxPoints() * 0.60 * testRatio(result.getTestStats());
        points *= deadlineMultiplier(task, result.getSubmitDate());
        points += bonusPoints(task.id(), bonuses);

        return Math.min(task.maxPoints() + bonusPoints(task.id(), bonuses), points);
    }

    private double testRatio(TestStats stats) {
        if (stats.total() == 0) {
            return 0.0;
        }
        return (double) stats.passed() / stats.total();
    }

    private double deadlineMultiplier(CourseConfig.TaskInfo task, LocalDate submitDate) {
        if (submitDate == null) {
            return 1.0;
        }
        if (task.hardDeadline() != null && submitDate.isAfter(task.hardDeadline())) {
            return 0.0;
        }
        if (task.softDeadline() != null && submitDate.isAfter(task.softDeadline())) {
            return 0.8;
        }
        return 1.0;
    }

    private double bonusPoints(String taskId, List<CourseConfig.BonusInfo> bonuses) {
        return bonuses.stream()
                .filter(bonus -> bonus.taskId().equals(taskId))
                .mapToDouble(CourseConfig.BonusInfo::points)
                .sum();
    }
}
