package ru.nsu.vorona.process;

/**
 * Результат выполнения процесса
 */
public record CommandResult(int exitCode, boolean timedOut, String output) {

    /**
     * Проверяет успешное завершение
     *
     * @return true, если процесс завершился с кодом 0
     */
    public boolean isSuccess() {
        return exitCode == 0 && !timedOut;
    }
}
