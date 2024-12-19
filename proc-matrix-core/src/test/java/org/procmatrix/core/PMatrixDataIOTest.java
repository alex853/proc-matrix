package org.procmatrix.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PMatrixDataIOTest {
    @Test
    public void test_asBytes_then_fromBytes() {
        final PMatrixData origin = PredefinedMatrices.randomSmallMatrix();
        final byte[] bytes = origin.asBytes();

        final PMatrixData another = PMatrixData.fromBytes(bytes);
        assertEquals(origin, another);
    }

    @Test
    public void test__asBytes__then__fromBytes__then__asBytes__again__arrays_should_be_same() {
        final PMatrixData origin = PredefinedMatrices.randomSmallMatrix();
        final byte[] originBytes = origin.asBytes();

        final PMatrixData another = PMatrixData.fromBytes(originBytes);
        final byte[] anotherBytes = another.asBytes();

        assertArrayEquals(originBytes, anotherBytes);
    }

    @Test
    public void test__asBytes_over_intBuffer__should_not_change_matrix() {
        final PMatrixData origin = PredefinedMatrices.randomSmallMatrix();
        final byte[] originBytes = origin.asBytes();

        final PMatrixData another = PMatrixData.fromBytes(originBytes);
        assertEquals(origin, another);

        final byte[] firstBytes = another.asBytes();
        assertEquals(origin, another);

        final byte[] secondBytes = another.asBytes();
        assertEquals(origin, another);

        assertArrayEquals(firstBytes, secondBytes);
    }
}
