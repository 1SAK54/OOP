package ru.nsu.vorona;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SequentialPrimeCheckerTest {

    private final PrimeChecker checker = new SequentialPrimeChecker();

    @Test
    void arrayWithNonPrimeReturnsTrue() {
        int[] input = {6, 8, 7, 13, 5, 9, 4};
        assertTrue(checker.hasNonPrime(input));
    }

    @Test
    void arrayWithAllPrimesReturnsFalse() {
        int[] input = {
                20319251, 6997901, 6997927, 6997937,
                17858849, 6997967, 6998009, 6998029,
                6998039, 20165149, 6998051, 6998053
        };
        assertFalse(checker.hasNonPrime(input));
    }

    @Test
    void emptyArrayHasNoNonPrime() {
        int[] input = {};
        assertFalse(checker.hasNonPrime(input));
    }

    @Test
    void singlePrimeElement() {
        assertFalse(checker.hasNonPrime(new int[]{2}));
        assertFalse(checker.hasNonPrime(new int[]{13}));
    }

    @Test
    void singleNonPrimeElement() {
        assertTrue(checker.hasNonPrime(new int[]{1}));
        assertTrue(checker.hasNonPrime(new int[]{4}));
        assertTrue(checker.hasNonPrime(new int[]{0}));
        assertTrue(checker.hasNonPrime(new int[]{-5}));
    }

    @Test
    void mixOfBigPrimesAndOneComposite() {
        int[] input = {
                20319251, 6997901, 6997927,
                100, // явно составное
                6997937, 17858849
        };
        assertTrue(checker.hasNonPrime(input));
    }
}
