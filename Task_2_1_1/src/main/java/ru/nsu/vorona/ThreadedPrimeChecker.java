package ru.nsu.vorona;

import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadedPrimeChecker extends PrimeChecker {

    private final int threadCount;

    public ThreadedPrimeChecker(int threadCount) {
        if (threadCount < 1) {
            throw new IllegalArgumentException("Количество потоков должно быть >= 1");
        }
        this.threadCount = threadCount;
    }

    @Override
    public boolean hasNonPrime(int[] numbers) {
        if (numbers.length == 0) return false;

        // Thread-safe флаг для early-stop
        AtomicBoolean foundNonPrime = new AtomicBoolean(false);
        Thread[] threads = new Thread[threadCount];
        int chunkSize = (numbers.length + threadCount - 1) / threadCount;

        // Запускаем потоки
        for (int i = 0; i < threadCount; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, numbers.length);

            if (start >= numbers.length) break;

            threads[i] = new Thread(() -> {
                // Early-stop: не проверяем если уже найдено
                for (int j = start; j < end && !foundNonPrime.get(); j++) {
                    if (!isPrime(numbers[j])) {
                        foundNonPrime.set(true); // Найдено!
                        return; // Выход из потока
                    }
                }
            });
            threads[i].start();
        }

        // Ждём завершения всех потоков
        for (Thread thread : threads) {
            if (thread != null) {
                try {
                    thread.join();
                    // Если уже найдено — можно прервать остальных
                    if (foundNonPrime.get()) {
                        break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }

        return foundNonPrime.get();
    }
}
