package ru.nsu.vorona.client.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import ru.nsu.vorona.core.model.Cell;
import ru.nsu.vorona.client.model.GameState;
import ru.nsu.vorona.client.model.PlayerState;

/**
 * Отрисовка игры
 */
public class GameView {
    private final Canvas canvas;

    /**
     * Создаёт view
     *
     * @param canvas canvas
     */
    public GameView(Canvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Рисует игру
     *
     * @param state состояние игры
     */
    public void render(GameState state) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double cellWidth = canvas.getWidth() / state.getCols();
        double cellHeight = canvas.getHeight() / state.getRows();

        drawBackground(gc);
        drawFood(gc, state, cellWidth, cellHeight);
        drawPlayers(gc, state, cellWidth, cellHeight);
    }

    /**
     * Рисует фон
     *
     * @param gc графический контекст
     */
    private void drawBackground(GraphicsContext gc) {
        gc.setEffect(null);
        gc.setFill(Color.rgb(7, 12, 26));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawHexGrid(gc);
    }

    /**
     * Рисует шестиугольную сетку
     *
     * @param gc графический контекст
     */
    private void drawHexGrid(GraphicsContext gc) {
        double radius = 18;
        double height = Math.sqrt(3) * radius;
        double xStep = radius * 1.5;
        double yStep = height;

        gc.setStroke(Color.rgb(28, 43, 75, 0.45));
        gc.setLineWidth(1);

        for (double x = -radius; x < canvas.getWidth() + radius; x += xStep) {
            int column = (int) Math.round(x / xStep);
            double offsetY = column % 2 == 0 ? 0 : height / 2;

            for (double y = -height; y < canvas.getHeight() + height; y += yStep) {
                drawHexagon(gc, x, y + offsetY, radius);
            }
        }
    }

    /**
     * Рисует один шестиугольник
     *
     * @param gc графический контекст
     * @param centerX центр по X
     * @param centerY центр по Y
     * @param radius радиус
     */
    private void drawHexagon(GraphicsContext gc, double centerX, double centerY, double radius) {
        double[] xs = new double[6];
        double[] ys = new double[6];

        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            xs[i] = centerX + radius * Math.cos(angle);
            ys[i] = centerY + radius * Math.sin(angle);
        }

        gc.strokePolygon(xs, ys, 6);
    }

    /**
     * Рисует еду
     */
    private void drawFood(GraphicsContext gc, GameState state, double cellWidth, double cellHeight) {
        Color foodColor = Color.rgb(255, 220, 90);

        for (Cell food : state.getFoods()) {
            double x = food.col() * cellWidth + cellWidth / 2;
            double y = food.row() * cellHeight + cellHeight / 2;
            double r = Math.min(cellWidth, cellHeight) * 0.32;

            gc.setEffect(new DropShadow(16, foodColor));
            gc.setFill(foodColor);
            gc.fillOval(x - r, y - r, r * 2, r * 2);
            gc.setEffect(null);
        }
    }

    /**
     * Рисует игроков
     */
    private void drawPlayers(GraphicsContext gc, GameState state, double cellWidth, double cellHeight) {
        for (PlayerState player : state.getPlayers()) {
            if (player.getBody().isEmpty()) {
                continue;
            }

            Color mainColor = player.isAlive()
                    ? colorForPlayer(player.getId(), player.getId() == state.getViewerId())
                    : Color.rgb(90, 90, 100);

            Color secondColor = player.isAlive()
                    ? mainColor.darker()
                    : Color.rgb(60, 60, 70);

            boolean head = true;
            int segmentIndex = 0;

            for (Cell cell : player.getBody()) {
                Color color = segmentIndex % 2 == 0 ? mainColor : secondColor;

                drawSnakeSegment(gc, cell, cellWidth, cellHeight, color, head);

                if (head) {
                    drawEyes(gc, cell, cellWidth, cellHeight);
                    drawNickname(gc, player, cell, cellWidth, cellHeight);
                    head = false;
                }

                segmentIndex++;
            }
        }
    }

    /**
     * Рисует звено змейки
     */
    private void drawSnakeSegment(
            GraphicsContext gc,
            Cell cell,
            double cellWidth,
            double cellHeight,
            Color color,
            boolean head
    ) {
        double x = cell.col() * cellWidth + cellWidth / 2;
        double y = cell.row() * cellHeight + cellHeight / 2;
        double r = Math.min(cellWidth, cellHeight) * (head ? 0.52 : 0.46);

        gc.setEffect(new DropShadow(10, color));
        gc.setFill(color);
        gc.fillOval(x - r, y - r, r * 2, r * 2);
        gc.setEffect(null);
    }

    /**
     * Рисует глаза
     */
    private void drawEyes(GraphicsContext gc, Cell cell, double cellWidth, double cellHeight) {
        double x = cell.col() * cellWidth + cellWidth / 2;
        double y = cell.row() * cellHeight + cellHeight / 2;
        double eyeRadius = Math.min(cellWidth, cellHeight) * 0.12;
        double pupilRadius = eyeRadius * 0.45;

        gc.setFill(Color.WHITE);
        gc.fillOval(x - eyeRadius * 2.1, y - eyeRadius * 1.2, eyeRadius * 2, eyeRadius * 2);
        gc.fillOval(x + eyeRadius * 0.1, y - eyeRadius * 1.2, eyeRadius * 2, eyeRadius * 2);

        gc.setFill(Color.BLACK);
        gc.fillOval(x - eyeRadius * 1.45, y - eyeRadius * 0.55, pupilRadius * 2, pupilRadius * 2);
        gc.fillOval(x + eyeRadius * 0.75, y - eyeRadius * 0.55, pupilRadius * 2, pupilRadius * 2);
    }

    /**
     * Рисует ник над головой
     */
    private void drawNickname(
            GraphicsContext gc,
            PlayerState player,
            Cell head,
            double cellWidth,
            double cellHeight
    ) {
        double x = head.col() * cellWidth + cellWidth / 2;
        double y = head.row() * cellHeight - cellHeight * 0.25;

        gc.setEffect(new DropShadow(6, Color.BLACK));
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 13));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(player.getNickname(), x, y);
        gc.setEffect(null);
    }

    /**
     * Возвращает цвет игрока
     */
    private Color colorForPlayer(int id, boolean currentPlayer) {
        Color[] colors = {
                Color.rgb(255, 196, 64),
                Color.rgb(64, 210, 255),
                Color.rgb(255, 80, 120),
                Color.rgb(160, 90, 255),
                Color.rgb(80, 255, 160),
                Color.rgb(255, 120, 50)
        };

        Color color = colors[(id - 1) % colors.length];
        return currentPlayer ? color.brighter() : color;
    }
}