package ru.nsu.vorona.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;

/**
 * Контроллер игры
 */
public class GameController {
    @FXML
    private Canvas canvas;

    @FXML
    private Label playerLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label respawnLabel;

    @FXML
    private Label leaderboardLabel;

    private GameView view;
    private NetworkClient networkClient;
    private GameState lastState;

    /**
     * Инициализирует контроллер
     */
    @FXML
    private void initialize() {
        view = new GameView(canvas);
    }

    /**
     * Инициализирует игру
     *
     * @param scene сцена
     * @param host адрес сервера
     * @param port порт
     * @param nickname ник игрока
     */
    public void init(Scene scene, String host, int port, String nickname) {
        scene.setOnKeyPressed(event -> handleKey(event.getCode()));

        try {
            networkClient = new NetworkClient();
            networkClient.connect(
                    host,
                    port,
                    nickname,
                    this::onState,
                    this::onError
            );
        } catch (Exception e) {
            respawnLabel.setText("Connection error");
        }
    }

    /**
     * Обрабатывает состояние сервера
     *
     * @param state состояние игры
     */
    private void onState(GameState state) {
        Platform.runLater(() -> {
            lastState = state;
            view.render(state);

            PlayerState viewer = state.getViewer();

            if (viewer != null) {
                playerLabel.setText(viewer.getNickname());
                scoreLabel.setText("Score: " + viewer.getScore());

                if (viewer.isAlive()) {
                    respawnLabel.setText("");
                } else {
                    respawnLabel.setText("Respawn in " + viewer.getRespawnSecondsLeft() + "...");
                }
            }

            leaderboardLabel.setText(buildLeaderboardText(state));
        });
    }

    /**
     * Обрабатывает сетевую ошибку
     *
     * @param message сообщение
     */
    private void onError(String message) {
        Platform.runLater(() -> respawnLabel.setText(message));
    }

    /**
     * Обрабатывает клавишу
     *
     * @param code код клавиши
     */
    private void handleKey(KeyCode code) {
        if (networkClient == null || lastState == null) {
            return;
        }

        if (code == KeyCode.UP || code == KeyCode.W) {
            networkClient.sendDirection(Direction.UP);
        } else if (code == KeyCode.DOWN || code == KeyCode.S) {
            networkClient.sendDirection(Direction.DOWN);
        } else if (code == KeyCode.LEFT || code == KeyCode.A) {
            networkClient.sendDirection(Direction.LEFT);
        } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
            networkClient.sendDirection(Direction.RIGHT);
        }
    }

    /**
     * Формирует текст лидерборда
     *
     * @param state состояние игры
     * @return текст лидерборда
     */
    private String buildLeaderboardText(GameState state) {
        StringBuilder sb = new StringBuilder("Leaderboard\n");

        int place = 1;
        for (LeaderboardItem item : state.getLeaderboard()) {
            sb.append(place)
                    .append(". ")
                    .append(item.getNickname())
                    .append(" - ")
                    .append(item.getScore())
                    .append("\n");
            place++;
        }

        return sb.toString();
    }
}