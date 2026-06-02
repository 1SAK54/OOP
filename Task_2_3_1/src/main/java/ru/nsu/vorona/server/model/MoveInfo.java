package ru.nsu.vorona.server.model;

import ru.nsu.vorona.core.model.Cell;

/**
 * Данные будущего хода змейки
 *
 * @param newHead новая голова
 * @param grow будет ли рост
 */
public record MoveInfo(Cell newHead, boolean grow) {
}