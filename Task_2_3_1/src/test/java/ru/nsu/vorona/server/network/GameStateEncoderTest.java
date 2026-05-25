package ru.nsu.vorona.server.network;

import org.junit.jupiter.api.Test;
import ru.nsu.vorona.server.model.Cell;
import ru.nsu.vorona.server.model.Direction;
import ru.nsu.vorona.server.model.PlayerSnake;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты кодирования состояния игры
 */
class GameStateEncoderTest {

    /**
     * Проверяет кодирование состояния игры
     */
    @Test
    void shouldEncodeGameState() {
        GameStateEncoder encoder = new GameStateEncoder();

        PlayerSnake firstSnake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.RIGHT);
        firstSnake.move(new Cell(5, 6), true);
        firstSnake.increaseScore();
        firstSnake.increaseScore();

        PlayerSnake secondSnake = new PlayerSnake(2, "Bob", new Cell(6, 6), Direction.LEFT);
        secondSnake.increaseScore();

        String result = encoder.encode(
                1,
                10,
                20,
                List.of(new Cell(1, 2), new Cell(3, 4)),
                List.of(firstSnake, secondSnake)
        );

        String expected = "STATE|1|10|20|1,2;3,4|"
                + "1,Alice,2,true,0,5,6;5,5#"
                + "2,Bob,1,true,0,6,6|"
                + "1,Alice,2;2,Bob,1";

        assertEquals(expected, result);
    }

    /**
     * Проверяет кодирование мёртвой змейки
     */
    @Test
    void shouldEncodeDeadSnake() {
        GameStateEncoder encoder = new GameStateEncoder();

        PlayerSnake snake = new PlayerSnake(1, "Alice", new Cell(5, 5), Direction.RIGHT);
        snake.kill(3000);

        String result = encoder.encode(
                1,
                10,
                20,
                List.of(),
                List.of(snake)
        );

        assertTrue(result.contains("1,Alice,0,false,"));
    }
}