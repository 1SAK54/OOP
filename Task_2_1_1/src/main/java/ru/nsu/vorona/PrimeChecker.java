package ru.nsu.vorona;

public abstract class PrimeChecker {

    /**
     * Проверяет, является ли число простым.
     *
     * @param n проверяемое число
     * @return true, если число простое, иначе false
     */
    protected static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    /**
     * Проверяет, содержит ли массив хотя бы одно непростое число.
     *
     * @param numbers массив чисел
     * @return true, если найдено непростое число, иначе false
     */
    public abstract boolean hasNonPrime(int[] numbers);
}