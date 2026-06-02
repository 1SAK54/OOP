package ru.nsu.vorona.check;

import ru.nsu.vorona.git.GitService;
import ru.nsu.vorona.model.CourseConfig;
import ru.nsu.vorona.process.CommandResult;
import ru.nsu.vorona.process.ProcessRunner;
import ru.nsu.vorona.result.StageResult;
import ru.nsu.vorona.result.TaskCheckResult;
import ru.nsu.vorona.result.TestStats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Проверяет одну задачу в репозитории
 */
public class TaskChecker {
    private final CourseConfig.Settings settings;
    private final ProcessRunner processRunner;
    private final GitService gitService;
    private final TestReportParser testReportParser = new TestReportParser();
    private final PointsCalculator pointsCalculator = new PointsCalculator();

    public TaskChecker(CourseConfig.Settings settings, ProcessRunner processRunner, GitService gitService) {
        this.settings = settings;
        this.processRunner = processRunner;
        this.gitService = gitService;
    }

    /**
     * Проверяет задачу
     *
     * @param repositoryDir репозиторий
     * @param task задача
     * @param bonuses бонусы студента
     * @return результат проверки
     */
    public TaskCheckResult check(
            Path repositoryDir,
            CourseConfig.TaskInfo task,
            List<CourseConfig.BonusInfo> bonuses
    ) {
        LocalDate submitDate = gitService.lastCommitDate(repositoryDir, task.id()).orElse(null);
        TaskCheckResult result = new TaskCheckResult(task.id(), task.title(), task.maxPoints(), submitDate);
        Path taskDir = repositoryDir.resolve(task.id());

        if (!Files.isDirectory(taskDir)) {
            result.setCompileResult(StageResult.failed("compile", "Task directory not found"));
            result.setDocsResult(StageResult.skipped("docs", "Compilation failed"));
            result.setStyleResult(StageResult.skipped("style", "Compilation failed"));
            result.setTestResult(StageResult.skipped("tests", "Compilation failed"));
            result.setPoints(0.0);
            return result;
        }

        result.setCompileResult(runGradleStage(taskDir, "compile", settings.getCompileTask()));

        if (!result.getCompileResult().isSuccess()) {
            skipAfterCompileFailure(result);
            result.setPoints(pointsCalculator.calculate(task, result, bonuses));
            return result;
        }

        result.setDocsResult(runGradleStage(taskDir, "docs", settings.getDocsTask()));
        result.setStyleResult(runStyleStage(taskDir));

        if (!canRunTests(result)) {
            result.setTestResult(StageResult.skipped("tests", "Previous required step failed"));
            result.setPoints(pointsCalculator.calculate(task, result, bonuses));
            return result;
        }

        result.setTestResult(runGradleStage(taskDir, "tests", settings.getTestTask()));
        TestStats testStats = testReportParser.parse(taskDir);
        result.setTestStats(testStats);
        result.setPoints(pointsCalculator.calculate(task, result, bonuses));

        return result;
    }

    private StageResult runStyleStage(Path taskDir) {
        StageResult result = runGradleStage(taskDir, "style", settings.getStyleTask());
        if (!result.isSuccess()
                && settings.isAllowMissingStyleTask()
                && result.message().contains("Task '" + settings.getStyleTask() + "' not found")) {
            return StageResult.skipped("style", "Style task not found");
        }
        return result;
    }

    private StageResult runGradleStage(Path taskDir, String stageName, String gradleTask) {
        try {
            CommandResult result = processRunner.run(
                    taskDir,
                    gradleCommand(taskDir, gradleTask),
                    Duration.ofSeconds(settings.getTimeoutSeconds())
            );
            if (result.isSuccess()) {
                return StageResult.success(stageName);
            }
            if (result.timedOut()) {
                return StageResult.failed(stageName, "Timeout");
            }
            return StageResult.failed(stageName, trim(result.output()));
        } catch (IOException e) {
            return StageResult.failed(stageName, e.getMessage());
        }
    }

    private List<String> gradleCommand(Path taskDir, String gradleTask) {
        boolean windows = System.getProperty("os.name").toLowerCase().contains("win");
        if (windows && Files.exists(taskDir.resolve("gradlew.bat"))) {
            return List.of("cmd", "/c", "gradlew.bat", gradleTask);
        }
        if (!windows && Files.exists(taskDir.resolve("gradlew"))) {
            return List.of("sh", "gradlew", gradleTask);
        }
        return List.of("gradle", gradleTask);
    }

    private void skipAfterCompileFailure(TaskCheckResult result) {
        result.setDocsResult(StageResult.skipped("docs", "Compilation failed"));
        result.setStyleResult(StageResult.skipped("style", "Compilation failed"));
        result.setTestResult(StageResult.skipped("tests", "Compilation failed"));
    }

    private boolean canRunTests(TaskCheckResult result) {
        return result.getCompileResult().isSuccess()
                && result.getDocsResult().isSuccess();
    }

    private String trim(String text) {
        if (text == null) {
            return "";
        }
        return text.length() > 500 ? text.substring(0, 500) : text;
    }
}
