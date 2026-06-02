package ru.nsu.vorona.client.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * JavaFX-клиент
 */
public class ClientApp extends Application {
    private static Stage primaryStage;

    /**
     * Запускает окно подключения
     *
     * @param stage главное окно
     * @throws IOException если FXML не найден
     */
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showConnectScreen();
    }

    /**
     * Показывает экран подключения
     *
     * @throws IOException если FXML не найден
     */
    public static void showConnectScreen() throws IOException {
        URL fxmlUrl = ClientApp.class.getResource("/client/connect.fxml");

        if (fxmlUrl == null) {
            throw new IllegalStateException("FXML file not found: /client/connect.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), 520, 360);

        primaryStage.setTitle("Snake Online");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Показывает экран игры
     *
     * @param host адрес сервера
     * @param port порт
     * @param nickname ник игрока
     * @throws IOException если FXML не найден
     */
    public static void showGameScreen(String host, int port, String nickname) throws IOException {
        URL fxmlUrl = ClientApp.class.getResource("/client/game.fxml");

        if (fxmlUrl == null) {
            throw new IllegalStateException("FXML file not found: /client/game.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), 1000, 720);

        GameController controller = loader.getController();
        controller.init(scene, host, port, nickname);

        primaryStage.setTitle("Snake Online - " + nickname);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Запускает приложение
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        launch(args);
    }
}