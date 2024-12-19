package org.procmatrix.core.ops;

import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.PMatrixId;
import org.procmatrix.core.RotationAngle;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class Op {
    private final UUID operationId = UUID.randomUUID();
    private final Operation operation;
    private final List<Operand> operands;
    private final ResultAction resultAction;

    private Op(final Operation operation,
               final List<Operand> operands,
               final ResultAction resultAction) {
        this.operation = operation;
        this.operands = operands;
        this.resultAction = resultAction;
    }

    public UUID getOperationId() {
        return operationId;
    }

    public Operation getOperation() {
        return operation;
    }

    public List<Operand> getOperands() {
        return Collections.unmodifiableList(operands);
    }

    public ResultAction getResultAction() {
        return resultAction;
    }

    public static Op rotate(final PMatrixId matrixId, final RotationAngle angle) {
        checkNotNull(matrixId, "matrixId should not be null");
        checkNotNull(angle, "angle should not be null");

        return new Op(
                Operation.Rotate,
                list(Operand.of(matrixId), Operand.of(angle)),
                ResultAction.and_store);
    }

    public static Op rotate(final PMatrixData matrix, final RotationAngle angle) {
        checkNotNull(matrix, "matrix should not be null");
        checkNotNull(angle, "angle should not be null");

        return new Op(
                Operation.Rotate,
                list(Operand.of(matrix), Operand.of(angle)),
                ResultAction.and_return);
    }

    public static Op transpose(final PMatrixId matrixId) {
        checkNotNull(matrixId, "matrixId should not be null");

        return new Op(
                Operation.Transpose,
                list(Operand.of(matrixId)),
                ResultAction.and_store);
    }

    public static Op transpose(final PMatrixData matrix) {
        checkNotNull(matrix, "matrix should not be null");

        return new Op(
                Operation.Transpose,
                list(Operand.of(matrix)),
                ResultAction.and_return);
    }

    public static Op add(final PMatrixId matrixId1, final PMatrixId matrixId2) {
        checkNotNull(matrixId1, "matrixId1 should not be null");
        checkNotNull(matrixId2, "matrixId2 should not be null");

        return new Op(
                Operation.Add,
                list(Operand.of(matrixId1), Operand.of(matrixId2)),
                ResultAction.and_store);
    }

    public static Op add(final PMatrixId matrixId1, final PMatrixData matrix2) {
        checkNotNull(matrixId1, "matrixId1 should not be null");
        checkNotNull(matrix2, "matrix2 should not be null");

        return new Op(
                Operation.Add,
                list(Operand.of(matrixId1), Operand.of(matrix2)),
                ResultAction.and_store);
    }

    public static Op add(final PMatrixData matrix1, final PMatrixId matrixId2) {
        checkNotNull(matrix1, "matrix1 should not be null");
        checkNotNull(matrixId2, "matrixId2 should not be null");

        return new Op(
                Operation.Add,
                list(Operand.of(matrix1), Operand.of(matrixId2)),
                ResultAction.and_store);
    }

    public static Op add(final PMatrixData matrix1, final PMatrixData matrix2) {
        checkNotNull(matrix1, "matrix1 should not be null");
        checkNotNull(matrix2, "matrix2 should not be null");

        return new Op(
                Operation.Add,
                list(Operand.of(matrix1), Operand.of(matrix2)),
                ResultAction.and_return);
    }

    public Op andStore() {
        return new Op(operation, operands, ResultAction.and_store);
    }

    public Op andReturn() {
        return new Op(operation, operands, ResultAction.and_return);
    }

    private static List<Operand> list(final Operand op) {
        return Arrays.asList(op);
    }

    private static List<Operand> list(final Operand op1, final Operand op2) {
        return Arrays.asList(op1, op2);
    }
}
