package ru.nsu.vorona.server.service;

import org.junit.jupiter.api.Test;
import ru.nsu.vorona.core.model.Cell;
import ru.nsu.vorona.core.model.Direction;
import ru.nsu.vorona.server.model.MoveInfo;
import ru.nsu.vorona.server.model.PlayerSnake;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты проверки столкновений
 */
class CollisionDetectorTest {

    /**
     * Проверяет столкновение со стеной
     */
    @Test
    void shouldDetectWallCollision() {
        CollisionDetector detector = new CollisionDetector(10, 10);
        PlayerSnake snake = new PlayerSnake(1, "Alice", new Cell(0, 0), Direction.UP);

        Map<PlayerSnake, MoveInfo> moves = new LinkedHashMap<>();
        moves.put(snake, new MoveInfo(new Cell(-1, 0), false));

        Set<PlayerSnake> deadSnakes = detector.findDeadSnakes(moves);

        assertTrue(deadSnakes.contains(snake));
    }

    /**
     * Проверяет столкновение головами в одну клетку
     */
    @Test
    void shouldDetectSameCellHeadCollision() {
        CollisionDetector detector = new CollisionDetector(10, 10);

        PlayerSnake firstSnake = new PlayerSnake(1, "Alice", new Cell(5, 4), Direction.RIGHT);
        PlayerSnake secondSnake = new PlayerSnake(2, "Bob", new Cell(5, 6), Direction.LEFT);

        Map<PlayerSnake, MoveInfo> moves = new LinkedHashMap<>();
        moves.put(firstSnake, new MoveInfo(new Cell(5, 5), false));
        moves.put(secondSnake, new MoveInfo(new Cell(5, 5), false));

        Set<PlayerSnake> deadSnakes = detector.findDeadSnakes(moves);

        assertTrue(deadSnakes.contains(firstSnake));
        assertTrue(deadSnakes.contains(secondSnake));
    }

    /**
     * Проверяет встречное столкновение головами
     */
    @Test
    void shouldDetectHeadSwapCollision() {
        CollisionDetector detector = new CollisionDetector(10, 10);

        PlayerSnake firstSnake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.RIGHT);
        PlayerSnake secondSnake = new PlayerSnake(2, "Bob", new Cell(5, 6), Direction.LEFT);

        Map<PlayerSnake, MoveInfo> moves = new LinkedHashMap<>();
        moves.put(firstSnake, new MoveInfo(new Cell(5, 6), false));
        moves.put(secondSnake, new MoveInfo(new Cell(5, 5), false));

        Set<PlayerSnake> deadSnakes = detector.findDeadSnakes(moves);

        assertTrue(deadSnakes.contains(firstSnake));
        assertTrue(deadSnakes.contains(secondSnake));
    }

    /**
     * Проверяет столкновение с телом
     */
    @Test
    void shouldDetectBodyCollision() {
        CollisionDetector detector = new CollisionDetector(10, 10);

        PlayerSnake firstSnake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.RIGHT);
        firstSnake.move(new Cell(5, 6), true);

        PlayerSnake secondSnake = new PlayerSnake(2, "Bob", new Cell(4, 6), Direction.DOWN);

        Map<PlayerSnake, MoveInfo> moves = new LinkedHashMap<>();
        moves.put(firstSnake, new MoveInfo(new Cell(5, 7), false));
        moves.put(secondSnake, new MoveInfo(new Cell(5, 6), false));

        Set<PlayerSnake> deadSnakes = detector.findDeadSnakes(moves);

        assertTrue(deadSnakes.contains(secondSnake));
    }

    /**
     * Проверяет проход через хвост, который уходит
     */
    @Test
    void shouldAllowMoveToTailCellWhenTailMoves() {
        CollisionDetector detector = new CollisionDetector(10, 10);

        PlayerSnake firstSnake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.RIGHT);
        firstSnake.move(new Cell(5, 6), true);

        PlayerSnake secondSnake = new PlayerSnake(2, "Bob", new Cell(4, 5), Direction.DOWN);

        Map<PlayerSnake, MoveInfo> moves = new LinkedHashMap<>();
        moves.put(firstSnake, new MoveInfo(new Cell(5, 7), false));
        moves.put(secondSnake, new MoveInfo(new Cell(5, 5), false));

        Set<PlayerSnake> deadSnakes = detector.findDeadSnakes(moves);

        assertFalse(deadSnakes.contains(secondSnake));
    }
}