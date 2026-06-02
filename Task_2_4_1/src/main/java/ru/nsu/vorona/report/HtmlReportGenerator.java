package ru.nsu.vorona.report;

import ru.nsu.vorona.model.CourseConfig;
import ru.nsu.vorona.result.CheckReport;
import ru.nsu.vorona.result.StageResult;
import ru.nsu.vorona.result.StudentReport;
import ru.nsu.vorona.result.TaskCheckResult;

import java.util.Comparator;
import java.util.Map;

/**
 * Генерирует HTML-отчёт
 */
public class HtmlReportGenerator {

    /**
     * Генерирует HTML
     *
     * @param report отчёт проверки
     * @return HTML строка
     */
    public String generate(CheckReport report) {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html>\n<html lang=\"ru\">\n<head>\n")
                .append("<meta charset=\"UTF-8\">\n")
                .append("<title>OOP Checker Report</title>\n")
                .append("<style>")
                .append("body{font-family:Arial,sans-serif;margin:24px;}")
                .append("table{border-collapse:collapse;width:100%;margin-bottom:24px;}")
                .append("th,td{border:1px solid #ccc;padding:8px;text-align:left;vertical-align:top;}")
                .append("th{background:#f2f2f2;}")
                .append(".ok{color:green;font-weight:bold;}")
                .append(".fail{color:#b00020;font-weight:bold;}")
                .append(".skip{color:#777;font-weight:bold;}")
                .append("</style>\n</head>\n<body>\n");

        html.append("<h1>Отчёт автоматической проверки ООП</h1>\n");
        appendSummary(html, report);
        appendStudents(html, report);
        appendCheckpoints(html, report);
        html.append("</body>\n</html>\n");
        return html.toString();
    }

    private void appendSummary(StringBuilder html, CheckReport report) {
        html.append("<h2>Итоги по студентам</h2>\n")
                .append("<table>\n")
                .append("<tr><th>Студент</th><th>GitHub</th><th>Баллы</th></tr>\n");

        for (StudentReport studentReport : report.getStudentReports()) {
            html.append("<tr><td>").append(escape(studentReport.getStudent().fullName()))
                    .append("</td><td>").append(escape(studentReport.getStudent().github()))
                    .append("</td><td>").append(format(studentReport.totalPoints()))
                    .append("</td></tr>\n");
        }

        html.append("</table>\n");
    }

    private void appendStudents(StringBuilder html, CheckReport report) {
        html.append("<h2>Подробные результаты</h2>\n");
        for (StudentReport studentReport : report.getStudentReports()) {
            html.append("<h3>").append(escape(studentReport.getStudent().fullName())).append("</h3>\n")
                    .append("<table>\n")
                    .append("<tr><th>Задача</th><th>Компиляция</th><th>Javadoc</th><th>Style</th>")
                    .append("<th>Тесты</th><th>Дата сдачи</th><th>Баллы</th></tr>\n");

            for (TaskCheckResult result : studentReport.getTaskResults().values()) {
                html.append("<tr><td>").append(escape(result.getTaskId()))
                        .append("<br>").append(escape(result.getTaskTitle()))
                        .append("</td><td>").append(stage(result.getCompileResult()))
                        .append("</td><td>").append(stage(result.getDocsResult()))
                        .append("</td><td>").append(stage(result.getStyleResult()))
                        .append("</td><td>").append(stage(result.getTestResult()))
                        .append("<br>passed: ").append(result.getTestStats().passed())
                        .append(", failed: ").append(result.getTestStats().failed())
                        .append(", skipped: ").append(result.getTestStats().skipped())
                        .append("</td><td>").append(result.getSubmitDate() == null ? "-" : result.getSubmitDate())
                        .append("</td><td>").append(format(result.getPoints()))
                        .append(" / ").append(format(result.getMaxPoints()))
                        .append("</td></tr>\n");
            }
            html.append("</table>\n");
        }
    }

    private void appendCheckpoints(StringBuilder html, CheckReport report) {
        if (report.getConfig().getCheckpoints().isEmpty()) {
            return;
        }

        html.append("<h2>Контрольные точки</h2>\n")
                .append("<table>\n")
                .append("<tr><th>Студент</th>");
        for (CourseConfig.CheckpointInfo checkpoint : report.getConfig().getCheckpoints()) {
            html.append("<th>").append(escape(checkpoint.name())).append("<br>")
                    .append(checkpoint.date()).append("</th>");
        }
        html.append("</tr>\n");

        for (StudentReport studentReport : report.getStudentReports()) {
            html.append("<tr><td>").append(escape(studentReport.getStudent().fullName())).append("</td>");
            for (CourseConfig.CheckpointInfo checkpoint : report.getConfig().getCheckpoints()) {
                double points = checkpointPoints(report, studentReport, checkpoint);
                double max = checkpointMax(report, checkpoint);
                html.append("<td>").append(format(points)).append(" / ").append(format(max))
                        .append("<br>Оценка: ").append(grade(report.getConfig(), points, max))
                        .append("</td>");
            }
            html.append("</tr>\n");
        }
        html.append("</table>\n");
    }

    private double checkpointPoints(
            CheckReport report,
            StudentReport studentReport,
            CourseConfig.CheckpointInfo checkpoint
    ) {
        return studentReport.getTaskResults().values().stream()
                .filter(result -> {
                    CourseConfig.TaskInfo task = report.getConfig().getTasks().get(result.getTaskId());
                    return task != null && !task.hardDeadline().isAfter(checkpoint.date());
                })
                .mapToDouble(TaskCheckResult::getPoints)
                .sum();
    }

    private double checkpointMax(CheckReport report, CourseConfig.CheckpointInfo checkpoint) {
        return report.getConfig().getTasks().values().stream()
                .filter(task -> !task.hardDeadline().isAfter(checkpoint.date()))
                .mapToDouble(CourseConfig.TaskInfo::maxPoints)
                .sum();
    }

    private String grade(CourseConfig config, double points, double maxPoints) {
        if (maxPoints <= 0) {
            return "-";
        }
        double percent = points / maxPoints * 100.0;
        return config.getSettings().getGradeBoundaries().entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .filter(entry -> percent >= entry.getValue())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("2");
    }

    private String stage(StageResult result) {
        if (result == null) {
            return "<span class=\"skip\">-</span>";
        }
        return switch (result.status()) {
            case SUCCESS -> "<span class=\"ok\">OK</span>";
            case FAILED -> "<span class=\"fail\">FAIL</span><br>" + escape(result.message());
            case SKIPPED -> "<span class=\"skip\">SKIP</span><br>" + escape(result.message());
        };
    }

    private String format(double value) {
        return String.format(java.util.Locale.US, "%.2f", value);
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
