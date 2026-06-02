package ru.nsu.vorona.result;

/**
 * Результат одного этапа проверки
 */
public record StageResult(String name, Status status, String message) {

    /**
     * Статус этапа
     */
    public enum Status {
        SUCCESS,
        FAILED,
        SKIPPED
    }

    public static StageResult success(String name) {
        return new StageResult(name, Status.SUCCESS, "");
    }

    public static StageResult failed(String name, String message) {
        return new StageResult(name, Status.FAILED, message);
    }

    public static StageResult skipped(String name, String message) {
        return new StageResult(name, Status.SKIPPED, message);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}
