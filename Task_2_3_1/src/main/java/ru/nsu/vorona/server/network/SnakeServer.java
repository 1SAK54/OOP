package ru.nsu.vorona.server.network;

import ru.nsu.vorona.server.service.MultiplayerGame;
import ru.nsu.vorona.server.config.ServerConfig;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервер змейки
 */
public class SnakeServer {
    private static final int MAX_PLAYERS = 8;

    private final int port;
    private final MultiplayerGame game;
    private final List<ClientHandler> clients = new ArrayList<>();

    /**
     * Создаёт сервер
     *
     * @param port порт
     */
    public SnakeServer(int port) {
        this.port = port;
        this.game = new MultiplayerGame(
                ServerConfig.ROWS,
                ServerConfig.COLS,
                ServerConfig.FOOD_COUNT,
                ServerConfig.TICK_MS
        );
    }

    /**
     * Запускает сервер
     *
     * @throws IOException если порт недоступен
     */
    public void start() throws IOException {
        new Thread(this::gameLoop, "GameLoop").start();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();

                if (game.getPlayerCount() >= ServerConfig.MAX_PLAYERS) {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("ERROR|Server is full");
                    socket.close();
                    continue;
                }

                ClientHandler handler = new ClientHandler(socket, this, game);
                handler.start();
            }
        }
    }

    /**
     * Регистрирует клиента
     *
     * @param client клиент
     */
    public void registerClient(ClientHandler client) {
        synchronized (clients) {
            clients.add(client);
        }
    }

    /**
     * Удаляет клиента
     *
     * @param client клиент
     */
    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    /**
     * Выполняет игровой цикл
     */
    private void gameLoop() {
        while (true) {
            try {
                Thread.sleep(game.getTickMs());
                game.update();
                broadcastState();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    /**
     * Рассылает состояние игры
     */
    public void broadcastState() {
        List<ClientHandler> copy;

        synchronized (clients) {
            copy = new ArrayList<>(clients);
        }

        for (ClientHandler client : copy) {
            client.send(game.toStateLine(client.getPlayerId()));
        }
    }
}