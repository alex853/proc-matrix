package org.procmatrix.core;

public class PredefinedMatrices {

    public static PMatrixData randomTinyMatrix() {
        return PMatrixData.random(10, 10);
    }

    public static PMatrixData randomSmallMatrix() {
        return PMatrixData.random(123, 321);
    }

    public static PMatrixData randomBigMatrix() {
        return PMatrixData.random(1234, 4321);
    }

    public static PMatrixData randomHugeMatrix() {
        return PMatrixData.random(12345, 13579);
    }

    public static PMatrixData random1gbMatrix() {
        final int requiredSize = 1024*1024*1024;
        final int bytesPerInt = 4;
        final int requiredValues = requiredSize / bytesPerInt;
        final int side = (int) Math.sqrt(requiredValues);
        return PMatrixData.random(side, side);
    }
}
