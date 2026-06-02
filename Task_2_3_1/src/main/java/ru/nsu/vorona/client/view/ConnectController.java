package ru.nsu.vorona.client.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Контроллер экрана подключения
 */
public class ConnectController {
    @FXML
    private TextField nicknameField;

    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;

    @FXML
    private Label errorLabel;

    /**
     * Подключается к игре
     */
    @FXML
    private void onConnect() {
        String nickname = nicknameField.getText().trim();
        String host = hostField.getText().trim();

        if (nickname.isBlank()) {
            errorLabel.setText("Enter nickname");
            return;
        }

        try {
            int port = Integer.parseInt(portField.getText().trim());
            ClientApp.showGameScreen(host, port, nickname);
        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid port");
        } catch (IOException e) {
            errorLabel.setText("Cannot open game screen");
        }
    }
}