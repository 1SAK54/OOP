package ru.nsu.vorona;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadedPrimeCheckerTest {

    private final int[] hasNonPrime = {6, 8, 7, 13, 5, 9, 4};
    private final int[] allPrime = {
            20319251, 6997901, 6997927, 6997937,
            17858849, 6997967, 6998009, 6998029,
            6998039, 20165149, 6998051, 6998053
    };

    @Test
    void oneThreadBehavesLikeSequential() {
        PrimeChecker threaded = new ThreadedPrimeChecker(1);
        PrimeChecker sequential = new SequentialPrimeChecker();

        assertEquals(sequential.hasNonPrime(hasNonPrime),
                threaded.hasNonPrime(hasNonPrime));

        assertEquals(sequential.hasNonPrime(allPrime),
                threaded.hasNonPrime(allPrime));
    }

    @Test
    void multiThreadBehavesCorrectly() {
        PrimeChecker checker = new ThreadedPrimeChecker(4);
        assertTrue(checker.hasNonPrime(hasNonPrime));
        assertFalse(checker.hasNonPrime(allPrime));
    }

    @Test
    void manyThreadsMoreThanArrayLength() {
        int[] shortArray = {2, 3, 5, 7, 11};
        PrimeChecker checker = new ThreadedPrimeChecker(16);
        assertFalse(checker.hasNonPrime(shortArray));
    }

    @Test
    void throwsOnZeroThreads() {
        assertThrows(IllegalArgumentException.class,
                () -> new ThreadedPrimeChecker(0));
    }

    @Test
    void emptyArrayOkInMultiThread() {
        PrimeChecker checker = new ThreadedPrimeChecker(4);
        assertFalse(checker.hasNonPrime(new int[]{}));
    }
}
