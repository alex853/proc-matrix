package org.procmatrix.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComputationsTest {

    @Test
    void test_check_addition_possible() {
        final PMatrixData m1 = PredefinedMatrices.randomSmallMatrix();
        final PMatrixData m2 = PredefinedMatrices.randomSmallMatrix();

        assertTrue(Computations.isAdditionPossible(m1, m2));
    }

    @Test
    void test_check_addition_impossible() {
        final PMatrixData m1 = PredefinedMatrices.randomSmallMatrix();
        final PMatrixData m2 = PredefinedMatrices.randomBigMatrix();

        assertFalse(Computations.isAdditionPossible(m1, m2));
    }

    @Test
    void test_add() {
        final PMatrixData m1 = PMatrixData.of(3, 3, new int[]{
                1, 2, 3,
                4, 5, 6,
                7, 8, 9});
        final PMatrixData m2 = PMatrixData.of(3, 3, new int[]{
                11, 12, 13,
                14, 15, 16,
                17, 18, 19});

        assertTrue(Computations.isAdditionPossible(m1, m2));
        assertEquals(PMatrixData.of(3, 3, new int[]{
                        12, 14, 16,
                        18, 20, 22,
                        24, 26, 28}),
                Computations.add(m1, m2));
    }

    @Test
    void test_add_when_matrices_can_not_be_added() {
        final PMatrixData m1 = PMatrixData.of(3, 3, new int[]{
                1, 2, 3,
                4, 5, 6,
                7, 8, 9});
        final PMatrixData m2 = PMatrixData.of(4, 3, new int[]{
                11, 12, 13, 14,
                15, 16, 17, 18,
                19, 20, 21, 22});

        assertFalse(Computations.isAdditionPossible(m1, m2));
        assertThrows(MatricesOfDifferentSizesException.class, () -> Computations.add(m1, m2));
    }

    @Test
    void test_transpose_squared_matrix() {
        final PMatrixData m1 = PMatrixData.of(3, 3, new int[]{
                1, 2, 3,
                4, 5, 6,
                7, 8, 9});

        assertEquals(PMatrixData.of(3, 3, new int[]{
                        1, 4, 7,
                        2, 5, 8,
                        3, 6, 9}),
                Computations.transpose(m1));
    }

    @Test
    void test_transpose_rectangular_matrix() {
        final PMatrixData m1 = PMatrixData.of(5, 2, new int[]{
                1, 2, 3, 4, 5,
                6, 7, 8, 9, 0});

        assertEquals(PMatrixData.of(2, 5, new int[]{
                        1, 6,
                        2, 7,
                        3, 8,
                        4, 9,
                        5, 0}),
                Computations.transpose(m1));
    }

    @Test
    void test_rotate_cw90_ccw270_rectangular_matrix() {
        final PMatrixData m1 = PMatrixData.of(5, 2, new int[]{
                1, 2, 3, 4, 5,
                6, 7, 8, 9, 0});

        assertEquals(PMatrixData.of(2, 5, new int[]{
                        6, 1,
                        7, 2,
                        8, 3,
                        9, 4,
                        0, 5}),
                Computations.rotate(m1, RotationAngle.cw90));
        assertEquals(Computations.rotate(m1, RotationAngle.ccw270),
                Computations.rotate(m1, RotationAngle.cw90));
    }

    @Test
    void test_rotate_cw180_ccw180_rectangular_matrix() {
        final PMatrixData m1 = PMatrixData.of(5, 2, new int[]{
                1, 2, 3, 4, 5,
                6, 7, 8, 9, 0});

        assertEquals(PMatrixData.of(5, 2, new int[]{
                        0, 9, 8, 7, 6,
                        5, 4, 3, 2, 1}),
                Computations.rotate(m1, RotationAngle.cw180));
        assertEquals(Computations.rotate(m1, RotationAngle.ccw180),
                Computations.rotate(m1, RotationAngle.cw180));
    }

    @Test
    void test_rotate_cw270_ccw90_rectangular_matrix() {
        final PMatrixData m1 = PMatrixData.of(5, 2, new int[]{
                1, 2, 3, 4, 5,
                6, 7, 8, 9, 0});

        assertEquals(PMatrixData.of(2, 5, new int[]{
                        5, 0,
                        4, 9,
                        3, 8,
                        2, 7,
                        1, 6}),
                Computations.rotate(m1, RotationAngle.cw270));
        assertEquals(Computations.rotate(m1, RotationAngle.ccw90),
                Computations.rotate(m1, RotationAngle.cw270));
    }
}
