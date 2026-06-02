package ru.nsu.vorona.git;

import ru.nsu.vorona.model.CourseConfig;
import ru.nsu.vorona.process.CommandResult;
import ru.nsu.vorona.process.ProcessRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Работает с репозиториями через консольный git
 */
public class GitService {
    private final ProcessRunner processRunner;
    private final Duration timeout;

    public GitService(ProcessRunner processRunner, Duration timeout) {
        this.processRunner = processRunner;
        this.timeout = timeout;
    }

    /**
     * Проверяет наличие git
     *
     * @throws IOException если git недоступен
     */
    public void checkGitAvailable() throws IOException {
        CommandResult result = processRunner.run(Path.of("."), List.of("git", "--version"), timeout);
        if (!result.isSuccess()) {
            throw new IOException("git is not available: " + result.output());
        }
    }

    /**
     * Скачивает или обновляет репозиторий
     *
     * @param student студент
     * @param repositoriesDir папка репозиториев
     * @return путь к репозиторию
     * @throws IOException если git завершился с ошибкой
     */
    public Path prepareRepository(CourseConfig.StudentInfo student, Path repositoriesDir) throws IOException {
        Files.createDirectories(repositoriesDir);
        Path repositoryDir = repositoriesDir.resolve(safeName(student.github()));

        if (Files.exists(repositoryDir.resolve(".git"))) {
            runGit(repositoryDir, "fetch", "--all", "--prune");
        } else {
            runGit(repositoriesDir, "clone", student.repo(), repositoryDir.getFileName().toString());
        }

        checkoutMainBranch(repositoryDir);
        return repositoryDir;
    }

    /**
     * Возвращает дату последнего коммита по задаче
     *
     * @param repositoryDir репозиторий
     * @param taskId id задачи
     * @return дата коммита
     */
    public Optional<LocalDate> lastCommitDate(Path repositoryDir, String taskId) {
        try {
            CommandResult result = processRunner.run(
                    repositoryDir,
                    List.of("git", "log", "-1", "--format=%cI", "--", taskId),
                    timeout
            );
            if (!result.isSuccess() || result.output().isBlank()) {
                return Optional.empty();
            }
            return Optional.of(OffsetDateTime.parse(result.output().trim()).toLocalDate());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private void checkoutMainBranch(Path repositoryDir) throws IOException {
        CommandResult mainResult = runGitAllowFail(repositoryDir, "checkout", "main");
        if (mainResult.isSuccess()) {
            runGit(repositoryDir, "pull", "--ff-only", "origin", "main");
            return;
        }

        CommandResult masterResult = runGitAllowFail(repositoryDir, "checkout", "master");
        if (masterResult.isSuccess()) {
            runGit(repositoryDir, "pull", "--ff-only", "origin", "master");
            return;
        }

        throw new IOException("Cannot checkout main or master branch in " + repositoryDir);
    }

    private void runGit(Path directory, String... args) throws IOException {
        CommandResult result = runGitAllowFail(directory, args);
        if (!result.isSuccess()) {
            throw new IOException(result.output());
        }
    }

    private CommandResult runGitAllowFail(Path directory, String... args) throws IOException {
        List<String> command = new java.util.ArrayList<>();
        command.add("git");
        command.addAll(List.of(args));
        return processRunner.run(directory, command, timeout);
    }

    private String safeName(String value) {
        return value.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
