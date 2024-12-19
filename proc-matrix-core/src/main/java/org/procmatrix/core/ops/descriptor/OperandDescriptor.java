package org.procmatrix.core.ops.descriptor;

import org.procmatrix.core.PMatrixId;
import org.procmatrix.core.RotationAngle;
import org.procmatrix.core.ops.Operand;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class OperandDescriptor {
    private final Type type;
    private final UUID matrixId;
    private final RotationAngle rotationAngle;

    private OperandDescriptor(final Type type, final UUID matrixId, final RotationAngle rotationAngle) {
        this.type = type;
        this.matrixId = matrixId;
        this.rotationAngle = rotationAngle;
    }

    public static OperandDescriptor from(final Operand operand) {
        checkNotNull(operand, "operand should not be null");

        return switch (operand.getType()) {
            case MatrixId -> new OperandDescriptor(
                    Type.MatrixId,
                    operand.getMatrixId().getUuid(),
                    null);
            case MatrixData -> new OperandDescriptor(
                    Type.MatrixDataAttached,
                    null,
                    null);
            case RotationAngle -> new OperandDescriptor(
                    Type.RotationAngle,
                    null,
                    operand.getRotationAngle());
            default -> throw new IllegalStateException("do not know what to do with operand type " + operand.getType());
        };
    }

    public Type getType() {
        return type;
    }


    public PMatrixId getMatrixId() {
        checkState(type == Type.MatrixId);
        return PMatrixId.fromUuid(matrixId);
    }

    public RotationAngle getRotationAngle() {
        checkState(type == Type.RotationAngle);
        return rotationAngle;
    }

    public enum Type {
        MatrixId,
        MatrixDataAttached,
        RotationAngle
    }
}
