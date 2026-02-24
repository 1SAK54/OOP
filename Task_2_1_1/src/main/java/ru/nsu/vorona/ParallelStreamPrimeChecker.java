package ru.nsu.vorona;

import java.util.Arrays;

public class ParallelStreamPrimeChecker extends PrimeChecker {
    @Override
    public boolean hasNonPrime(int[] numbers) {
        return Arrays.stream(numbers)
                .parallel()
                .anyMatch(n -> !isPrime(n));
    }
}
