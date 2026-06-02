package ru.nsu.vorona.process;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Запускает внешние процессы
 */
public class ProcessRunner {

    /**
     * Запускает команду в рабочей директории
     *
     * @param directory рабочая директория
     * @param command команда
     * @param timeout таймаут
     * @return результат выполнения
     * @throws IOException если процесс не запущен
     */
    public CommandResult run(Path directory, List<String> command, Duration timeout) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(directory.toFile());
        builder.redirectErrorStream(true);

        Map<String, String> environment = builder.environment();
        environment.put("GIT_TERMINAL_PROMPT", "0");

        Process process = builder.start();
        CompletableFuture<String> outputFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                return e.getMessage();
            }
        });

        try {
            boolean finished = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroyForcibly();
                return new CommandResult(-1, true, outputFuture.get(2, TimeUnit.SECONDS));
            }
            return new CommandResult(process.exitValue(), false, outputFuture.get(2, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
            return new CommandResult(-1, true, "Interrupted");
        } catch (ExecutionException | TimeoutException e) {
            return new CommandResult(process.exitValue(), false, e.getMessage());
        }
    }
}
