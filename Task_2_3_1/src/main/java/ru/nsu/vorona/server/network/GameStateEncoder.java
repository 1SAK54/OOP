package ru.nsu.vorona.server.network;

import ru.nsu.vorona.server.model.Cell;
import ru.nsu.vorona.server.model.PlayerSnake;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Кодирует состояние игры в строку сетевого протокола
 */
public class GameStateEncoder {

    /**
     * Кодирует полное состояние игры
     *
     * @param viewerId id клиента
     * @param rows количество строк поля
     * @param cols количество столбцов поля
     * @param foods список еды
     * @param snakes коллекция змей игроков
     * @return строка состояния игры
     */
    public String encode(
            int viewerId,
            int rows,
            int cols,
            List<Cell> foods,
            Collection<PlayerSnake> snakes
    ) {
        return "STATE"
                + "|" + viewerId
                + "|" + rows
                + "|" + cols
                + "|" + cellsToString(foods)
                + "|" + snakesToString(snakes)
                + "|" + leaderboardToString(snakes);
    }

    /**
     * Преобразует клетки в строку
     *
     * @param cells список клеток
     * @return строка клеток
     */
    private String cellsToString(List<Cell> cells) {
        return cells.stream()
                .map(cell -> cell.row() + "," + cell.col())
                .collect(Collectors.joining(";"));
    }

    /**
     * Преобразует змей в строку
     *
     * @param snakes коллекция змей
     * @return строка змей
     */
    private String snakesToString(Collection<PlayerSnake> snakes) {
        long now = System.currentTimeMillis();

        return snakes.stream()
                .map(snake -> snake.getId()
                        + "," + snake.getNickname()
                        + "," + snake.getScore()
                        + "," + snake.isAlive()
                        + "," + respawnSecondsLeft(snake, now)
                        + "," + cellsToString(new ArrayList<>(snake.getBody())))
                .collect(Collectors.joining("#"));
    }

    /**
     * Преобразует лидерборд в строку
     *
     * @param snakes коллекция змей
     * @return строка лидерборда
     */
    private String leaderboardToString(Collection<PlayerSnake> snakes) {
        return snakes.stream()
                .sorted(Comparator.comparingInt(PlayerSnake::getScore).reversed())
                .map(snake -> snake.getId() + "," + snake.getNickname() + "," + snake.getScore())
                .collect(Collectors.joining(";"));
    }

    /**
     * Считает время до респавна
     *
     * @param snake змейка игрока
     * @param now текущее время
     * @return количество секунд до респавна
     */
    private int respawnSecondsLeft(PlayerSnake snake, long now) {
        if (snake.isAlive()) {
            return 0;
        }

        long left = Math.max(0, snake.getRespawnTimeMillis() - now);
        return (int) Math.ceil(left / 1000.0);
    }
}