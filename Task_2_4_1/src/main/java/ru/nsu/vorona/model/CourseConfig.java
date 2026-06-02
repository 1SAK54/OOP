package ru.nsu.vorona.model;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Конфигурация проверки курса
 */
public class CourseConfig {
    private final Map<String, TaskInfo> tasks = new LinkedHashMap<>();
    private final Map<String, GroupInfo> groups = new LinkedHashMap<>();
    private final List<CheckpointInfo> checkpoints = new ArrayList<>();
    private final List<BonusInfo> bonuses = new ArrayList<>();
    private final CheckRequest checkRequest = new CheckRequest();
    private final Settings settings = new Settings();

    public Map<String, TaskInfo> getTasks() {
        return tasks;
    }

    public Map<String, GroupInfo> getGroups() {
        return groups;
    }

    public List<CheckpointInfo> getCheckpoints() {
        return checkpoints;
    }

    public List<BonusInfo> getBonuses() {
        return bonuses;
    }

    public CheckRequest getCheckRequest() {
        return checkRequest;
    }

    public Settings getSettings() {
        return settings;
    }

    public void addTask(TaskInfo task) {
        tasks.put(task.id(), task);
    }

    public void addGroup(GroupInfo group) {
        groups.put(group.name(), group);
    }

    public void addCheckpoint(CheckpointInfo checkpoint) {
        checkpoints.add(checkpoint);
    }

    public void addBonus(BonusInfo bonus) {
        bonuses.add(bonus);
    }

    /**
     * Описание задачи
     */
    public record TaskInfo(
            String id,
            String title,
            double maxPoints,
            LocalDate softDeadline,
            LocalDate hardDeadline
    ) {
    }

    /**
     * Описание студента
     */
    public record StudentInfo(String fullName, String github, String repo) {
    }

    /**
     * Описание группы
     */
    public record GroupInfo(String name, List<StudentInfo> students) {
    }

    /**
     * Описание контрольной точки
     */
    public record CheckpointInfo(String name, LocalDate date) {
    }

    /**
     * Дополнительные баллы
     */
    public record BonusInfo(String github, String taskId, double points, String reason) {
    }

    /**
     * Задание на проверку
     */
    public static class CheckRequest {
        private final Set<String> taskIds = new LinkedHashSet<>();
        private final Set<String> studentGithubs = new LinkedHashSet<>();
        private final Set<String> groupNames = new LinkedHashSet<>();
        private boolean allTasks;
        private boolean allStudents;

        public Set<String> getTaskIds() {
            return taskIds;
        }

        public Set<String> getStudentGithubs() {
            return studentGithubs;
        }

        public Set<String> getGroupNames() {
            return groupNames;
        }

        public boolean isAllTasks() {
            return allTasks;
        }

        public void setAllTasks(boolean allTasks) {
            this.allTasks = allTasks;
        }

        public boolean isAllStudents() {
            return allStudents;
        }

        public void setAllStudents(boolean allStudents) {
            this.allStudents = allStudents;
        }
    }

    /**
     * Настройки проверки
     */
    public static class Settings {
        private Path workDir = Path.of(".oop-checker-work");
        private int timeoutSeconds = 120;
        private String compileTask = "compileJava";
        private String docsTask = "javadoc";
        private String styleTask = "checkstyleMain";
        private String testTask = "test";
        private boolean allowMissingStyleTask;
        private final Map<String, Double> gradeBoundaries = new LinkedHashMap<>();

        public Settings() {
            gradeBoundaries.put("5", 85.0);
            gradeBoundaries.put("4", 70.0);
            gradeBoundaries.put("3", 55.0);
        }

        public Path getWorkDir() {
            return workDir;
        }

        public void setWorkDir(Path workDir) {
            this.workDir = workDir;
        }

        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public void setTimeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }

        public String getCompileTask() {
            return compileTask;
        }

        public void setCompileTask(String compileTask) {
            this.compileTask = compileTask;
        }

        public String getDocsTask() {
            return docsTask;
        }

        public void setDocsTask(String docsTask) {
            this.docsTask = docsTask;
        }

        public String getStyleTask() {
            return styleTask;
        }

        public void setStyleTask(String styleTask) {
            this.styleTask = styleTask;
        }

        public String getTestTask() {
            return testTask;
        }

        public void setTestTask(String testTask) {
            this.testTask = testTask;
        }

        public boolean isAllowMissingStyleTask() {
            return allowMissingStyleTask;
        }

        public void setAllowMissingStyleTask(boolean allowMissingStyleTask) {
            this.allowMissingStyleTask = allowMissingStyleTask;
        }

        public Map<String, Double> getGradeBoundaries() {
            return gradeBoundaries;
        }
    }
}
