package ru.nsu.vorona.result;

import ru.nsu.vorona.model.CourseConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Итоговый отчёт проверки
 */
public class CheckReport {
    private final CourseConfig config;
    private final List<StudentReport> studentReports = new ArrayList<>();

    public CheckReport(CourseConfig config) {
        this.config = config;
    }

    public CourseConfig getConfig() {
        return config;
    }

    public List<StudentReport> getStudentReports() {
        return studentReports;
    }
}
