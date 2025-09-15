package ru.nsu.vorona;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SortTest {

    @Test
    public void testEmptyArray() {
        int[] arr = {};
        Sort.heapSort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    public void testSingleElement() {
        int[] arr = {42};
        Sort.heapSort(arr);
        assertArrayEquals(new int[]{42}, arr);
    }

    @Test
    public void testSortedArray() {
        int[] arr = {1, 2, 3, 4, 5};
        Sort.heapSort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    public void testReverseSortedArray() {
        int[] arr = {5, 4, 3, 2, 1};
        Sort.heapSort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    public void testRandomArray() {
        int[] arr = {12, 11, 13, 5, 6, 7};
        Sort.heapSort(arr);
        assertArrayEquals(new int[]{5, 6, 7, 11, 12, 13}, arr);
    }
}
