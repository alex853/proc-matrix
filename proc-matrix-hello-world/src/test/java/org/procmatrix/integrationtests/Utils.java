package org.procmatrix.integrationtests;

import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.PredefinedMatrices;

public class Utils {
    public static PMatrixData randomMatrixBySize(final String size) {
        return switch (size) {
            case "tiny" -> PredefinedMatrices.randomTinyMatrix();
            case "small" -> PredefinedMatrices.randomSmallMatrix();
            case "big" -> PredefinedMatrices.randomBigMatrix();
            case "huge" -> PredefinedMatrices.randomHugeMatrix();
            case "1gb" -> PredefinedMatrices.random1gbMatrix();
            default -> throw new IllegalArgumentException("unknown size " + size);
        };
    }
}
