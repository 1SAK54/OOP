package ru.nsu.vorona.server.service;

import ru.nsu.vorona.server.model.Cell;
import ru.nsu.vorona.server.model.MoveInfo;
import ru.nsu.vorona.server.model.PlayerSnake;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Проверяет столкновения змей
 */
public class CollisionDetector {
    private final int rows;
    private final int cols;

    public CollisionDetector(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    public Set<PlayerSnake> findDeadSnakes(Map<PlayerSnake, MoveInfo> moves) {
        Set<PlayerSnake> deadSnakes = new HashSet<>();

        for (Map.Entry<PlayerSnake, MoveInfo> entry : moves.entrySet()) {
            PlayerSnake snake = entry.getKey();
            MoveInfo move = entry.getValue();

            if (isOutOfBounds(move.newHead()) || isFutureBodyCollision(snake, move, moves)) {
                deadSnakes.add(snake);
            }
        }

        addHeadToHeadCollisions(moves, deadSnakes);
        return deadSnakes;
    }

    private void addHeadToHeadCollisions(
            Map<PlayerSnake, MoveInfo> moves,
            Set<PlayerSnake> deadSnakes
    ) {
        List<Map.Entry<PlayerSnake, MoveInfo>> entries = new ArrayList<>(moves.entrySet());

        for (int i = 0; i < entries.size(); i++) {
            for (int j = i + 1; j < entries.size(); j++) {
                PlayerSnake firstSnake = entries.get(i).getKey();
                PlayerSnake secondSnake = entries.get(j).getKey();

                Cell firstHead = entries.get(i).getValue().newHead();
                Cell secondHead = entries.get(j).getValue().newHead();

                if (firstHead.equals(secondHead)) {
                    deadSnakes.add(firstSnake);
                    deadSnakes.add(secondSnake);
                }

                if (firstHead.equals(secondSnake.getHead())
                        && secondHead.equals(firstSnake.getHead())) {
                    deadSnakes.add(firstSnake);
                    deadSnakes.add(secondSnake);
                }
            }
        }
    }

    private boolean isFutureBodyCollision(
            PlayerSnake currentSnake,
            MoveInfo currentMove,
            Map<PlayerSnake, MoveInfo> moves
    ) {
        for (Map.Entry<PlayerSnake, MoveInfo> entry : moves.entrySet()) {
            PlayerSnake otherSnake = entry.getKey();

            if (!otherSnake.isAlive()) {
                continue;
            }

            MoveInfo otherMove = entry.getValue();
            List<Cell> body = otherSnake.getBody();
            int lastIndex = body.size() - 1;

            for (int i = 0; i < body.size(); i++) {
                boolean isTail = i == lastIndex;
                boolean tailWillMove = !otherMove.grow();

                if (isTail && tailWillMove) {
                    continue;
                }

                if (currentSnake == otherSnake && i == 0) {
                    continue;
                }

                if (currentMove.newHead().equals(body.get(i))) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isOutOfBounds(Cell cell) {
        return cell.row() < 0
                || cell.row() >= rows
                || cell.col() < 0
                || cell.col() >= cols;
    }
}