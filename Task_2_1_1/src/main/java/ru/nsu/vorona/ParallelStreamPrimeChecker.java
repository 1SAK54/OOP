package ru.nsu.vorona;

import java.util.Arrays;

public class ParallelStreamPrimeChecker extends PrimeChecker {

    /**
     * Проверяет массив с помощью parallelStream.
     *
     * @param numbers массив чисел
     * @return true, если найдено непростое число, иначе false
     */
    @Override
    public boolean hasNonPrime(int[] numbers) {
        return Arrays.stream(numbers)
                .parallel()
                .anyMatch(n -> !isPrime(n));
    }
}