package org.procmatrix.core;

import static com.google.common.base.Preconditions.checkNotNull;

public class Computations {
    public static boolean isAdditionPossible(final PMatrixData m1, final PMatrixData m2) {
        checkNotNull(m1, "m1 should not be null");
        checkNotNull(m2, "m2 should not be null");

        return (m1.getWidth() == m2.getWidth()) && (m1.getHeight() == m2.getHeight());
    }

    public static PMatrixData add(final PMatrixData m1, final PMatrixData m2) {
        checkNotNull(m1, "m1 should not be null");
        checkNotNull(m2, "m2 should not be null");

        if (!isAdditionPossible(m1, m2)) {
            throw new MatricesOfDifferentSizesException();
        }

        final PMatrixData.DataAccess data1 = m1.getData();
        final PMatrixData.DataAccess data2 = m2.getData();

        final int[] data = new int[data1.length()];
        for (int i = 0; i < data1.length(); i++) {
            data[i] = data1.getByIndex(i) + data2.getByIndex(i);
        }

        return PMatrixData.of(m1.getWidth(), m1.getHeight(), data);
    }

    public static PMatrixData transpose(final PMatrixData m) {
        checkNotNull(m, "m should not be null");

        final int srcW = m.getWidth();
        final int srcH = m.getHeight();
        final PMatrixData.DataAccess src = m.getData();

        final int dstW = srcH;
        final int dstH = srcW;
        final int[] dst = new int[src.length()];

        for (int i = 0; i < src.length(); i++) {
            final int dstRow = i / dstW;
            final int dstCol = i % dstW;
            final int srcRow = dstCol;
            final int srcCol = dstRow;
            dst[i] = src.getByIndex(srcRow * srcW + srcCol);
        }

        return PMatrixData.of(dstW, dstH, dst);
    }

    public static PMatrixData rotate(final PMatrixData m, final RotationAngle angle) {
        checkNotNull(m, "m should not be null");

        final int srcW = m.getWidth();
        final int srcH = m.getHeight();
        final PMatrixData.DataAccess src = m.getData();

        final int dstW;
        final int dstH;

        switch (angle) {
            case cw90:
            case ccw270:
                dstW = srcH;
                dstH = srcW;
                break;
            case cw180:
            case ccw180:
                dstW = srcW;
                dstH = srcH;
                break;
            case cw270:
            case ccw90:
                dstW = srcH;
                dstH = srcW;
                break;
            default:
                throw new IllegalArgumentException("unknown angle " + angle);
        }

        final int[] dst = new int[src.length()];

        for (int i = 0; i < src.length(); i++) {
            final int dstRow = i / dstW;
            final int dstCol = i % dstW;

            final int srcRow;
            final int srcCol;

            switch (angle) {
                case cw90:
                case ccw270:
                    srcRow = (srcH - 1) - dstCol;
                    srcCol = dstRow;
                    break;
                case cw180:
                case ccw180:
                    srcRow = (srcH - 1) - dstRow;
                    srcCol = (srcW - 1) - dstCol;
                    break;
                case cw270:
                case ccw90:
                    srcRow = dstCol;
                    srcCol = (srcW - 1) - dstRow;
                    break;
                default:
                    throw new IllegalArgumentException("unknown angle " + angle);
            }

            dst[i] = src.getByIndex(srcRow * srcW + srcCol);
        }

        return PMatrixData.of(dstW, dstH, dst);
    }

}
