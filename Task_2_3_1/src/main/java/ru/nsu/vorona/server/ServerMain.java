package ru.nsu.vorona.server;

import java.io.IOException;

/**
 * Точка входа сервера
 */
public class ServerMain {

    /**
     * Запускает сервер
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 5555;

        try {
            new SnakeServer(port).start();
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}