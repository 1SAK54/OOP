package ru.nsu.vorona.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Сетевой клиент
 */
public class NetworkClient {
    private Socket socket;
    private PrintWriter out;

    /**
     * Подключается к серверу
     *
     * @param host адрес сервера
     * @param port порт
     * @param nickname ник игрока
     * @param onState обработчик состояния
     * @param onError обработчик ошибки
     * @throws IOException если подключение не удалось
     */
    public void connect(
            String host,
            int port,
            String nickname,
            Consumer<GameState> onState,
            Consumer<String> onError
    ) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        out.println("JOIN|" + nickname);

        Thread reader = new Thread(() -> readLoop(onState, onError), "NetworkReader");
        reader.setDaemon(true);
        reader.start();
    }

    /**
     * Отправляет направление
     *
     * @param direction направление
     */
    public void sendDirection(Direction direction) {
        if (out != null) {
            out.println("DIR|" + direction.name());
        }
    }

    /**
     * Читает сообщения сервера
     *
     * @param onState обработчик состояния
     * @param onError обработчик ошибки
     */
    private void readLoop(Consumer<GameState> onState, Consumer<String> onError) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String line;

            while ((line = in.readLine()) != null) {
                if (line.startsWith("STATE|")) {
                    onState.accept(GameState.parse(line));
                } else if (line.startsWith("ERROR|")) {
                    onError.accept(line.substring(6));
                }
            }
        } catch (IOException e) {
            onError.accept("Disconnected");
        }
    }
}