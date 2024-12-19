package org.procmatrix.core.ops.descriptor;

import org.procmatrix.core.ops.Op;
import org.procmatrix.core.ops.Operation;
import org.procmatrix.core.ops.ResultAction;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class OpDescriptor {
    private final UUID operationId;
    private final Operation operation;
    private final List<OperandDescriptor> operands;
    private final ResultAction resultAction;

    private OpDescriptor(final UUID operationId,
                         final Operation operation,
                         final List<OperandDescriptor> operands,
                         final ResultAction resultAction) {
        this.operationId = operationId;
        this.operation = operation;
        this.operands = operands;
        this.resultAction = resultAction;
    }

    public static OpDescriptor from(final Op op) {
        checkNotNull(op, "op should not be null");

        return new OpDescriptor(
                op.getOperationId(),
                op.getOperation(),
                op.getOperands().stream().map(OperandDescriptor::from).collect(Collectors.toList()),
                op.getResultAction());
    }

    public UUID getOperationId() {
        return operationId;
    }

    public Operation getOperation() {
        return operation;
    }

    public List<OperandDescriptor> getOperands() {
        return Collections.unmodifiableList(operands);
    }

    public ResultAction getResultAction() {
        return resultAction;
    }
}
