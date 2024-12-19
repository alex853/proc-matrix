package org.procmatrix.core.ops;

import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.PMatrixId;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class Result {
    private final Type type;
    private final UUID operationId;
    private final PMatrixId matrixId;
    private final PMatrixData matrixData;

    private Result(final Type type,
                   final UUID operationId,
                   final PMatrixId matrixId,
                   final PMatrixData matrixData) {
        this.type = type;
        this.operationId = operationId;
        this.matrixId = matrixId;
        this.matrixData = matrixData;
    }

    public static Result of(final UUID operationId,
                            final PMatrixId matrixId) {
        checkNotNull(operationId, "operationId should not be null");
        checkNotNull(matrixId, "matrixId should not be null");

        return new Result(Type.MatrixId, operationId, matrixId, null);
    }

    public static Result of(final UUID operationId,
                            final PMatrixData matrixData) {
        checkNotNull(operationId, "operationId should not be null");
        checkNotNull(matrixData, "matrixData should not be null");

        return new Result(Type.MatrixData, operationId, null, matrixData);
    }

    public static Result ofError(final UUID operationId) {
        checkNotNull(operationId, "operationId should not be null");

        return new Result(Type.Error, operationId, null, null);
    }

    public Type getType() {
        return type;
    }

    public UUID getOperationId() {
        return operationId;
    }

    public PMatrixId getMatrixId() {
        checkState(type == Type.MatrixId);
        return matrixId;
    }

    public PMatrixData getMatrixData() {
        checkState(type == Type.MatrixData);
        return matrixData;
    }

    public enum Type {
        MatrixId,
        MatrixData,
        Error
    }
}
