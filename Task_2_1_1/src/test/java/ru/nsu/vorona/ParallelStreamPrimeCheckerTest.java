package ru.nsu.vorona;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParallelStreamPrimeCheckerTest {

    private final PrimeChecker parallel = new ParallelStreamPrimeChecker();
    private final PrimeChecker sequential = new SequentialPrimeChecker();

    @Test
    void behavesLikeSequentialOnGivenExamples() {
        int[] hasNonPrime = {6, 8, 7, 13, 5, 9, 4};
        int[] allPrime = {
                20319251, 6997901, 6997927, 6997937,
                17858849, 6997967, 6998009, 6998029,
                6998039, 20165149, 6998051, 6998053
        };

        assertEquals(sequential.hasNonPrime(hasNonPrime),
                parallel.hasNonPrime(hasNonPrime));

        assertEquals(sequential.hasNonPrime(allPrime),
                parallel.hasNonPrime(allPrime));
    }

    @Test
    void worksOnEmptyArray() {
        assertFalse(parallel.hasNonPrime(new int[]{}));
    }

    @Test
    void worksOnLargeRandomLikeArray() {
        int[] input = {
                2, 3, 5, 7, 11,
                4, 6, 8, 9, 10,
                7919, 7927, 7933, 7949
        };

        assertTrue(parallel.hasNonPrime(input));
    }
}
