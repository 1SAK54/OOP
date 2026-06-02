package ru.nsu.vorona.client.network;

import org.junit.jupiter.api.Test;
import ru.nsu.vorona.core.model.Direction;
import ru.nsu.vorona.client.model.GameState;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты сетевого клиента
 */
class NetworkClientTest {

    /**
     * Проверяет подключение, отправку JOIN, получение STATE и отправку направления
     *
     * @throws Exception если произошла ошибка сети
     */
    @Test
    void shouldConnectReadStateAndSendDirection() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();

            CountDownLatch stateLatch = new CountDownLatch(1);
            AtomicReference<GameState> receivedState = new AtomicReference<>();
            AtomicReference<String> joinLine = new AtomicReference<>();
            AtomicReference<String> directionLine = new AtomicReference<>();

            Thread serverThread = new Thread(() -> {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true)) {

                    joinLine.set(in.readLine());

                    out.println("STATE|1|40|60|5,5|1,Alice,1,true,0,10,10|1,Alice,1");

                    directionLine.set(in.readLine());
                } catch (Exception ignored) {
                }
            });

            serverThread.start();

            NetworkClient client = new NetworkClient();
            client.connect(
                    "localhost",
                    port,
                    "Alice",
                    state -> {
                        receivedState.set(state);
                        stateLatch.countDown();
                    },
                    message -> fail(message)
            );

            assertTrue(stateLatch.await(2, TimeUnit.SECONDS));

            client.sendDirection(Direction.RIGHT);
            serverThread.join(2000);

            assertEquals("JOIN|Alice", joinLine.get());
            assertEquals("DIR|RIGHT", directionLine.get());
            assertNotNull(receivedState.get());
            assertEquals(1, receivedState.get().getViewerId());
        }
    }
}