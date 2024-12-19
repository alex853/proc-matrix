package org.procmatrix.core.ops.descriptor;

import org.procmatrix.core.PMatrixId;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class ResultDescriptor {
    private final UUID operationId;
    private final Type type;
    private final UUID matrixId;

    private ResultDescriptor(final UUID operationId, final Type type, final UUID matrixId) {
        this.operationId = operationId;
        this.type = type;
        this.matrixId = matrixId;
    }

    public static ResultDescriptor buildForMatrixId(final UUID operationId, final PMatrixId resultedMatrixId) {
        checkNotNull(operationId, "operationId should not be null");
        checkNotNull(resultedMatrixId, "resultedMatrixId should not be null");

        return new ResultDescriptor(operationId, Type.MatrixId, resultedMatrixId.getUuid());
    }

    public static ResultDescriptor buildForMatrixDataAttached(final UUID operationId) {
        checkNotNull(operationId, "operationId should not be null");

        return new ResultDescriptor(operationId, Type.MatrixDataAttached, null);
    }

    public static ResultDescriptor buildForError(final UUID operationId, final String message) {
        checkNotNull(operationId, "operationId should not be null");
        checkNotNull(message, "message should not be null");

        return new ResultDescriptor(operationId, Type.Error, null);
    }

    public UUID getOperationId() {
        return operationId;
    }

    public Type getType() {
        return type;
    }

    public PMatrixId getMatrixId() {
        checkState(type == Type.MatrixId);
        return PMatrixId.fromUuid(matrixId);
    }

    public enum Type {
        MatrixId,
        MatrixDataAttached,
        Error
    }
}
