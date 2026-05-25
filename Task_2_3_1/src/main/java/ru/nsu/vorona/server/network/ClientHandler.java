package ru.nsu.vorona.server.network;

import ru.nsu.vorona.server.service.MultiplayerGame;
import ru.nsu.vorona.server.model.Direction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Обработчик клиента
 */
public class ClientHandler extends Thread {
    private final Socket socket;
    private final SnakeServer server;
    private final MultiplayerGame game;

    private PrintWriter out;
    private int playerId;

    /**
     * Создаёт обработчик клиента
     *
     * @param socket сокет клиента
     * @param server сервер
     * @param game модель игры
     */
    public ClientHandler(Socket socket, SnakeServer server, MultiplayerGame game) {
        this.socket = socket;
        this.server = server;
        this.game = game;
    }

    /**
     * Запускает чтение команд
     */
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out = new PrintWriter(socket.getOutputStream(), true);

            String firstLine = in.readLine();
            if (firstLine == null || !firstLine.startsWith("JOIN|")) {
                close();
                return;
            }

            String nickname = firstLine.substring(5);
            playerId = game.addPlayer(nickname);
            setName("ClientHandler-" + playerId);

            send("INIT|" + playerId);
            server.registerClient(this);
            server.broadcastState();

            String line;
            while ((line = in.readLine()) != null) {
                handle(line);
            }
        } catch (IOException e) {
            System.err.println("Client disconnected: " + playerId);
        } finally {
            if (playerId > 0) {
                game.removePlayer(playerId);
            }

            server.removeClient(this);
            server.broadcastState();
            close();
        }
    }

    /**
     * Обрабатывает команду клиента
     *
     * @param line строка команды
     */
    private void handle(String line) {
        if (!line.startsWith("DIR|")) {
            return;
        }

        try {
            Direction direction = Direction.valueOf(line.substring(4));
            game.changeDirection(playerId, direction);
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * Отправляет сообщение клиенту
     *
     * @param line строка сообщения
     */
    public void send(String line) {
        if (out != null) {
            out.println(line);
        }
    }

    /**
     * Возвращает id игрока
     *
     * @return id игрока
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Закрывает сокет
     */
    private void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}