package ru.nsu.vorona.server;

/**
 * Направление движения
 */
public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int rowDelta;
    private final int colDelta;

    /**
     * Создаёт направление
     *
     * @param rowDelta изменение строки
     * @param colDelta изменение столбца
     */
    Direction(int rowDelta, int colDelta) {
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
    }

    /**
     * Возвращает изменение строки
     *
     * @return изменение строки
     */
    public int getRowDelta() {
        return rowDelta;
    }

    /**
     * Возвращает изменение столбца
     *
     * @return изменение столбца
     */
    public int getColDelta() {
        return colDelta;
    }

    /**
     * Проверяет противоположность направлений
     *
     * @param other другое направление
     * @return true, если направления противоположны
     */
    public boolean isOpposite(Direction other) {
        return rowDelta + other.rowDelta == 0 && colDelta + other.colDelta == 0;
    }
}