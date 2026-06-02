package ru.nsu.vorona.result;

import java.time.LocalDate;

/**
 * Результат проверки задачи
 */
public class TaskCheckResult {
    private final String taskId;
    private final String taskTitle;
    private final double maxPoints;
    private final LocalDate submitDate;
    private StageResult compileResult;
    private StageResult docsResult;
    private StageResult styleResult;
    private StageResult testResult;
    private TestStats testStats = new TestStats(0, 0, 0);
    private double points;

    public TaskCheckResult(String taskId, String taskTitle, double maxPoints, LocalDate submitDate) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.maxPoints = maxPoints;
        this.submitDate = submitDate;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public double getMaxPoints() {
        return maxPoints;
    }

    public LocalDate getSubmitDate() {
        return submitDate;
    }

    public StageResult getCompileResult() {
        return compileResult;
    }

    public void setCompileResult(StageResult compileResult) {
        this.compileResult = compileResult;
    }

    public StageResult getDocsResult() {
        return docsResult;
    }

    public void setDocsResult(StageResult docsResult) {
        this.docsResult = docsResult;
    }

    public StageResult getStyleResult() {
        return styleResult;
    }

    public void setStyleResult(StageResult styleResult) {
        this.styleResult = styleResult;
    }

    public StageResult getTestResult() {
        return testResult;
    }

    public void setTestResult(StageResult testResult) {
        this.testResult = testResult;
    }

    public TestStats getTestStats() {
        return testStats;
    }

    public void setTestStats(TestStats testStats) {
        this.testStats = testStats;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }
}
