package ru.nsu.vorona.result;

import ru.nsu.vorona.model.CourseConfig;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Отчёт по одному студенту
 */
public class StudentReport {
    private final CourseConfig.StudentInfo student;
    private final Map<String, TaskCheckResult> taskResults = new LinkedHashMap<>();

    public StudentReport(CourseConfig.StudentInfo student) {
        this.student = student;
    }

    public CourseConfig.StudentInfo getStudent() {
        return student;
    }

    public Map<String, TaskCheckResult> getTaskResults() {
        return taskResults;
    }

    public double totalPoints() {
        return taskResults.values().stream()
                .mapToDouble(TaskCheckResult::getPoints)
                .sum();
    }
}
