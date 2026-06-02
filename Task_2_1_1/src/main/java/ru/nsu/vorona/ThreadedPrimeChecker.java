package ru.nsu.vorona;

import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadedPrimeChecker extends PrimeChecker {

    private final int threadCount;

    /**
     * Создаёт проверяющий объект с заданным числом потоков.
     *
     * @param threadCount количество потоков
     */
    public ThreadedPrimeChecker(int threadCount) {
        if (threadCount < 1) {
            throw new IllegalArgumentException("Количество потоков должно быть >= 1");
        }
        this.threadCount = threadCount;
    }

    /**
     * Проверяет массив в несколько потоков.
     *
     * @param numbers массив чисел
     * @return true, если найдено непростое число, иначе false
     */
    @Override
    public boolean hasNonPrime(int[] numbers) {
        if (numbers.length == 0) return false;

        AtomicBoolean foundNonPrime = new AtomicBoolean(false);
        Thread[] threads = new Thread[threadCount];
        int chunkSize = (numbers.length + threadCount - 1) / threadCount;

        for (int i = 0; i < threadCount; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, numbers.length);

            if (start >= numbers.length) break;

            threads[i] = new Thread(() -> {
                for (int j = start; j < end && !foundNonPrime.get(); j++) {
                    if (!isPrime(numbers[j])) {
                        foundNonPrime.set(true);
                        return;
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }

        return foundNonPrime.get();
    }
}