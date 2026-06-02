package ru.nsu.vorona.dsl

import ru.nsu.vorona.config.ConfigLoader
import ru.nsu.vorona.model.CourseConfig

import java.nio.file.Path
import java.time.LocalDate

/**
 * Groovy DSL для конфигурации проверки
 */
class Dsl {
    private final CourseConfig config
    private final ConfigLoader loader
    private final Path baseDir

    Dsl(CourseConfig config, ConfigLoader loader, Path baseDir) {
        this.config = config
        this.loader = loader
        this.baseDir = baseDir
    }

    void include(String fileName) {
        loader.loadInto(baseDir.resolve(fileName).normalize(), config)
    }

    void task(String id, Closure closure) {
        TaskBuilder builder = new TaskBuilder(id)
        configure(builder, closure)
        config.addTask(builder.build())
    }

    void group(String name, Closure closure) {
        GroupBuilder builder = new GroupBuilder(name)
        configure(builder, closure)
        config.addGroup(builder.build())
    }

    void check(Closure closure) {
        CheckBuilder builder = new CheckBuilder(config.getCheckRequest())
        configure(builder, closure)
    }

    void checkpoint(String name, Closure closure) {
        CheckpointBuilder builder = new CheckpointBuilder(name)
        configure(builder, closure)
        config.addCheckpoint(builder.build())
    }

    void settings(Closure closure) {
        SettingsBuilder builder = new SettingsBuilder(config.getSettings())
        configure(builder, closure)
    }

    void bonus(Map args) {
        config.addBonus(new CourseConfig.BonusInfo(
                args.github as String,
                args.task as String,
                (args.points as Number).doubleValue(),
                args.reason == null ? "" : args.reason as String
        ))
    }

    private static void configure(Object target, Closure closure) {
        closure.delegate = target
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
    }

    void tasks(Closure closure) {
        configure(this, closure)
    }

    void groups(Closure closure) {
        configure(this, closure)
    }

    void checkpoints(Closure closure) {
        configure(this, closure)
    }

    void bonuses(Closure closure) {
        configure(this, closure)
    }

    void checking(Closure closure) {
        check(closure)
    }
}

class TaskBuilder {
    private final String id
    private String title
    private double maxPoints
    private LocalDate softDeadline
    private LocalDate hardDeadline

    TaskBuilder(String id) {
        this.id = id
        this.title = id
    }

    void title(String value) {
        title = value
    }

    void maxPoints(Number value) {
        maxPoints = value.doubleValue()
    }

    void softDeadline(String value) {
        softDeadline = LocalDate.parse(value)
    }

    void hardDeadline(String value) {
        hardDeadline = LocalDate.parse(value)
    }

    CourseConfig.TaskInfo build() {
        return new CourseConfig.TaskInfo(id, title, maxPoints, softDeadline, hardDeadline)
    }
}

class GroupBuilder {
    private final String name
    private final List<CourseConfig.StudentInfo> students = []

    GroupBuilder(String name) {
        this.name = name
    }

    void student(Map args, String fullName) {
        students.add(new CourseConfig.StudentInfo(fullName, args.github as String, args.repo as String))
    }

    void student(Map args) {
        students.add(new CourseConfig.StudentInfo(
                args.fullName as String,
                args.github as String,
                args.repo as String
        ))
    }

    CourseConfig.GroupInfo build() {
        return new CourseConfig.GroupInfo(name, students)
    }
}

class CheckBuilder {
    private final CourseConfig.CheckRequest request

    CheckBuilder(CourseConfig.CheckRequest request) {
        this.request = request
    }

    void tasks(Object... ids) {
        ids.each { request.getTaskIds().add(it.toString()) }
    }

    void students(Object... githubs) {
        githubs.each { request.getStudentGithubs().add(it.toString()) }
    }

    void groups(Object... names) {
        names.each { request.getGroupNames().add(it.toString()) }
    }

    void allTasks() {
        request.setAllTasks(true)
    }

    void allStudents() {
        request.setAllStudents(true)
    }
}

class CheckpointBuilder {
    private final String name
    private LocalDate date

    CheckpointBuilder(String name) {
        this.name = name
    }

    void date(String value) {
        date = LocalDate.parse(value)
    }

    CourseConfig.CheckpointInfo build() {
        return new CourseConfig.CheckpointInfo(name, date)
    }
}

class SettingsBuilder {
    private final CourseConfig.Settings settings

    SettingsBuilder(CourseConfig.Settings settings) {
        this.settings = settings
    }

    void workDir(String value) {
        settings.setWorkDir(Path.of(value))
    }

    void timeoutSeconds(Number value) {
        settings.setTimeoutSeconds(value.intValue())
    }

    void compileTask(String value) {
        settings.setCompileTask(value)
    }

    void docsTask(String value) {
        settings.setDocsTask(value)
    }

    void styleTask(String value) {
        settings.setStyleTask(value)
    }

    void testTask(String value) {
        settings.setTestTask(value)
    }

    void allowMissingStyleTask(boolean value) {
        settings.setAllowMissingStyleTask(value)
    }

    void grade(Map args, Object gradeName) {
        settings.getGradeBoundaries().put(gradeName.toString(), (args.from as Number).doubleValue())
    }
}
