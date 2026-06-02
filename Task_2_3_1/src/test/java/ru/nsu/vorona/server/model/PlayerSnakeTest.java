package ru.nsu.vorona.server.model;

import org.junit.jupiter.api.Test;
import ru.nsu.vorona.core.model.Cell;
import ru.nsu.vorona.core.model.Direction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты змейки игрока
 */
class PlayerSnakeTest {

    /**
     * Проверяет создание змейки
     */
    @Test
    void shouldCreateSnake() {
        PlayerSnake snake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.RIGHT);

        assertEquals(1, snake.getId());
        assertEquals("Alice", snake.getNickname());
        assertEquals(0, snake.getScore());
        assertTrue(snake.isAlive());
        assertEquals(1, snake.getBody().size());
        assertEquals(new Cell(5, 5), snake.getHead());
        assertEquals(0, snake.getRespawnTimeMillis());
    }

    /**
     * Проверяет следующую голову
     */
    @Test
    void shouldCalculateNextHead() {
        PlayerSnake snake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.RIGHT);

        assertEquals(new Cell(5, 6), snake.nextHead());
    }

    /**
     * Проверяет движение без роста
     */
    @Test
    void shouldMoveWithoutGrow() {
        PlayerSnake snake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.RIGHT);

        snake.move(new Cell(5, 6), false);

        assertEquals(1, snake.getBody().size());
        assertEquals(new Cell(5, 6), snake.getHead());
    }

    /**
     * Проверяет движение с ростом
     */
    @Test
    void shouldMoveWithGrow() {
        PlayerSnake snake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.RIGHT);

        snake.move(new Cell(5, 6), true);

        assertEquals(2, snake.getBody().size());
        assertEquals(new Cell(5, 6), snake.getHead());
        assertTrue(snake.contains(new Cell(5, 5)));
    }

    /**
     * Проверяет отложенное направление
     */
    @Test
    void shouldApplyNextDirection() {
        PlayerSnake snake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.UP);

        snake.changeDirection(Direction.LEFT);
        snake.applyNextDirection();

        assertEquals(new Cell(5, 4), snake.nextHead());
    }

    /**
     * Проверяет, что за тик применяется только первое направление
     */
    @Test
    void shouldApplyOnlyFirstDirectionBeforeTick() {
        PlayerSnake snake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.UP);

        snake.changeDirection(Direction.LEFT);
        snake.changeDirection(Direction.DOWN);
        snake.applyNextDirection();

        assertEquals(new Cell(5, 4), snake.nextHead());
    }

    /**
     * Проверяет запрет разворота назад
     */
    @Test
    void shouldIgnoreOppositeDirection() {
        PlayerSnake snake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.UP);

        snake.changeDirection(Direction.DOWN);
        snake.applyNextDirection();

        assertEquals(new Cell(4, 5), snake.nextHead());
    }

    /**
     * Проверяет запрет смены направления после смерти
     */
    @Test
    void shouldNotChangeDirectionWhenDead() {
        PlayerSnake snake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.UP);

        snake.kill(3000);
        snake.changeDirection(Direction.LEFT);
        snake.applyNextDirection();

        assertEquals(new Cell(4, 5), snake.nextHead());
    }

    /**
     * Проверяет смерть змейки
     */
    @Test
    void shouldKillSnake() {
        PlayerSnake snake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.RIGHT);

        snake.move(new Cell(5, 6), true);
        snake.kill(3000);

        assertFalse(snake.isAlive());
        assertEquals(2, snake.getBody().size());
        assertTrue(snake.getRespawnTimeMillis() > System.currentTimeMillis());
    }

    /**
     * Проверяет возрождение змейки
     */
    @Test
    void shouldRespawnSnake() {
        PlayerSnake snake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.RIGHT);

        snake.move(new Cell(5, 6), true);
        snake.kill(3000);
        snake.respawn(new Cell(2, 2), Direction.DOWN);

        assertTrue(snake.isAlive());
        assertEquals(1, snake.getBody().size());
        assertEquals(new Cell(2, 2), snake.getHead());
        assertEquals(new Cell(3, 2), snake.nextHead());
        assertEquals(0, snake.getRespawnTimeMillis());
    }

    /**
     * Проверяет увеличение счёта
     */
    @Test
    void shouldIncreaseScore() {
        PlayerSnake snake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.RIGHT);

        snake.increaseScore();
        snake.increaseScore();

        assertEquals(2, snake.getScore());
    }
}