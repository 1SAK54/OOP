package ru.nsu.vorona;

public class SequentialPrimeChecker extends PrimeChecker {
    @Override
    public boolean hasNonPrime(int[] numbers) {
        for (int num : numbers) {
            if (!isPrime(num)) return true;
        }
        return false;
    }
}
