package ru.nsu.vorona.server.app;

import ru.nsu.vorona.server.config.ServerConfig;
import ru.nsu.vorona.server.network.SnakeServer;

import java.io.IOException;

/**
 * Точка вход   а сервера
 */
public class ServerMain {

    /**
     * Запускает сервер
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        int port = args.length > 0
                ? Integer.parseInt(args[0])
                : ServerConfig.DEFAULT_PORT;

        try {
            new SnakeServer(port).start();
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}