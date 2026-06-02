package ru.nsu.vorona.check;

import ru.nsu.vorona.git.GitService;
import ru.nsu.vorona.model.CourseConfig;
import ru.nsu.vorona.process.ProcessRunner;
import ru.nsu.vorona.result.CheckReport;
import ru.nsu.vorona.result.StudentReport;
import ru.nsu.vorona.result.TaskCheckResult;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Выполняет проверку репозиториев
 */
public class RepositoryChecker {
    private final ProcessRunner processRunner = new ProcessRunner();

    /**
     * Выполняет проверку по конфигурации
     *
     * @param config конфигурация
     * @return отчёт проверки
     * @throws IOException если git недоступен
     */
    public CheckReport check(CourseConfig config) throws IOException {
        CourseConfig.Settings settings = config.getSettings();
        GitService gitService = new GitService(processRunner, Duration.ofSeconds(settings.getTimeoutSeconds()));
        gitService.checkGitAvailable();

        CheckReport report = new CheckReport(config);
        TaskChecker taskChecker = new TaskChecker(settings, processRunner, gitService);
        Path repositoriesDir = settings.getWorkDir().resolve("repositories");

        for (CourseConfig.StudentInfo student : selectedStudents(config)) {
            StudentReport studentReport = new StudentReport(student);
            Path repositoryDir;
            try {
                repositoryDir = gitService.prepareRepository(student, repositoriesDir);
            } catch (IOException e) {
                report.getStudentReports().add(studentReport);
                continue;
            }

            for (CourseConfig.TaskInfo task : selectedTasks(config)) {
                List<CourseConfig.BonusInfo> bonuses = bonusesFor(config, student.github(), task.id());
                TaskCheckResult taskResult = taskChecker.check(repositoryDir, task, bonuses);
                studentReport.getTaskResults().put(task.id(), taskResult);
            }

            report.getStudentReports().add(studentReport);
        }

        return report;
    }

    private List<CourseConfig.StudentInfo> selectedStudents(CourseConfig config) {
        Map<String, CourseConfig.StudentInfo> allStudents = new LinkedHashMap<>();
        for (CourseConfig.GroupInfo group : config.getGroups().values()) {
            for (CourseConfig.StudentInfo student : group.students()) {
                allStudents.put(student.github(), student);
            }
        }

        CourseConfig.CheckRequest request = config.getCheckRequest();
        if (request.isAllStudents()
                || (request.getStudentGithubs().isEmpty() && request.getGroupNames().isEmpty())) {
            return new ArrayList<>(allStudents.values());
        }

        Map<String, CourseConfig.StudentInfo> result = new LinkedHashMap<>();
        for (String github : request.getStudentGithubs()) {
            if (allStudents.containsKey(github)) {
                result.put(github, allStudents.get(github));
            }
        }
        for (String groupName : request.getGroupNames()) {
            CourseConfig.GroupInfo group = config.getGroups().get(groupName);
            if (group != null) {
                for (CourseConfig.StudentInfo student : group.students()) {
                    result.put(student.github(), student);
                }
            }
        }
        return new ArrayList<>(result.values());
    }

    private List<CourseConfig.TaskInfo> selectedTasks(CourseConfig config) {
        CourseConfig.CheckRequest request = config.getCheckRequest();
        if (request.isAllTasks() || request.getTaskIds().isEmpty()) {
            return new ArrayList<>(config.getTasks().values());
        }

        List<CourseConfig.TaskInfo> result = new ArrayList<>();
        for (String taskId : request.getTaskIds()) {
            CourseConfig.TaskInfo task = config.getTasks().get(taskId);
            if (task != null) {
                result.add(task);
            }
        }
        return result;
    }

    private List<CourseConfig.BonusInfo> bonusesFor(CourseConfig config, String github, String taskId) {
        return config.getBonuses().stream()
                .filter(bonus -> bonus.github().equals(github) && bonus.taskId().equals(taskId))
                .toList();
    }
}
