package ru.nsu.vorona;

public class SequentialPrimeChecker extends PrimeChecker {

    /**
     * Последовательно проверяет все числа в массиве.
     *
     * @param numbers массив чисел
     * @return true, если найдено непростое число, иначе false
     */
    @Override
    public boolean hasNonPrime(int[] numbers) {
        for (int num : numbers) {
            if (!isPrime(num)) return true;
        }
        return false;
    }
}