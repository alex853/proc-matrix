package org.procmatrix.core.ops;

import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.PMatrixId;
import org.procmatrix.core.RotationAngle;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class Operand {
    private final Type type;
    private final PMatrixId matrixId;
    private final PMatrixData matrixData;
    private final RotationAngle rotationAngle;

    private Operand(Type type, final PMatrixId matrixId, final PMatrixData matrixData, final RotationAngle rotationAngle) {
        this.type = type;
        this.matrixId = matrixId;
        this.matrixData = matrixData;
        this.rotationAngle = rotationAngle;
    }

    public Type getType() {
        return type;
    }

    public PMatrixId getMatrixId() {
        checkState(type == Type.MatrixId);
        return matrixId;
    }

    public PMatrixData getMatrixData() {
        checkState(type == Type.MatrixData);
        return matrixData;
    }

    public RotationAngle getRotationAngle() {
        checkState(type == Type.RotationAngle);
        return rotationAngle;
    }

    public static Operand of(final PMatrixId id) {
        checkNotNull(id, "id should be specified");

        return new Operand(Type.MatrixId, id, null, null);
    }

    public static Operand of(final PMatrixData matrix) {
        checkNotNull(matrix, "matrix should be specified");

        return new Operand(Type.MatrixData, null, matrix, null);
    }

    public static Operand of(final RotationAngle angle) {
        checkNotNull(angle, "angle should be specified");

        return new Operand(Type.RotationAngle, null, null, angle);
    }

    public enum Type {
        MatrixId,
        MatrixData,
        RotationAngle
    }
}
