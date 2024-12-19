package org.procmatrix.computations.service;

import com.google.gson.Gson;
import org.procmatrix.core.Computations;
import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.PMatrixId;
import org.procmatrix.core.ops.ResultAction;
import org.procmatrix.core.ops.descriptor.OpDescriptor;
import org.procmatrix.core.ops.descriptor.OperandDescriptor;
import org.procmatrix.core.ops.descriptor.ResultDescriptor;
import org.procmatrix.storage.client.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@RestController
@RequestMapping("computations/v1")
@CrossOrigin
public class ComputationsController {
    private static final Logger log = LoggerFactory.getLogger(ComputationsController.class);

    private static final Gson gson = new Gson();

    @Autowired
    private StorageService storageService;

    private final Map<String, ResultInfo> cachedResults = new ConcurrentHashMap<>(); // todo ak1 this should be cleaned up using some TTL setting

    @PostMapping(value = "/batch/submit",
            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<String> submitBatch(final InputStream in) throws IOException {
        checkNotNull(in, "in should not be null");

        int counter = 0;
        while (true) {
            final OpDescriptor op = readOpDescriptor(in);
            if (op == null) {
                log.info("no more operations found");
                break;
            }

            try {
                final int attachedMatricesCount = getAttachedMatricesCount(op);
                final List<PMatrixData> attachedMatrices = new ArrayList<>(attachedMatricesCount);
                for (int i = 0; i < attachedMatricesCount; i++) {
                    attachedMatrices.add(readMatrix(in));
                }

                final List<PMatrixData> matrices = getOperandMatrices(op, attachedMatrices);
                final PMatrixData resultedMatrix = computeOp(op, matrices);

                if (op.getResultAction() == ResultAction.and_store) {
                    final PMatrixId resultedMatrixId = saveMatrix(resultedMatrix);

                    final ResultDescriptor resultDescriptor = ResultDescriptor.buildForMatrixId(op.getOperationId(), resultedMatrixId);
                    cachedResults.put(op.getOperationId().toString(), new ResultInfo(resultDescriptor, null));
                } else {
                    final ResultDescriptor resultDescriptor = ResultDescriptor.buildForMatrixDataAttached(op.getOperationId());
                    cachedResults.put(op.getOperationId().toString(), new ResultInfo(resultDescriptor, resultedMatrix));
                }
            } catch (final Exception e) {
                log.error("error on processing operation", e);

                final ResultDescriptor resultDescriptor = ResultDescriptor.buildForError(op.getOperationId(), e.getClass().getName());
                cachedResults.put(op.getOperationId().toString(), new ResultInfo(resultDescriptor, null));
            }

            counter++;
        }

        return ResponseEntity.ok("Loaded " + counter + " operations");
    }

    @PostMapping(value = "/batch/retrieve",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> retrieveResults(final @RequestBody List<String> operationIds) {
        checkNotNull(operationIds, "in should not be null");
        checkArgument(operationIds.size() > 0, "at least one id should be specified");

        final List<ResultInfo> foundResults = operationIds.stream()
                .map(cachedResults::remove)
                .filter(Objects::nonNull)
                .toList();

        return new ResponseEntity<>(out -> {
            for (final ResultInfo result : foundResults) {
                final ResultDescriptor resultDescriptor = result.resultDescriptor();

                writeResultDescriptor(out, resultDescriptor);

                if (resultDescriptor.getType() == ResultDescriptor.Type.MatrixDataAttached) {
                    writeMatrix(out, result.matrixDataToBeAttached());
                }
            }
        }, HttpStatus.OK);
    }

    private static OpDescriptor readOpDescriptor(final InputStream in) throws IOException {
        final int length = readLength(in);
        if (length == -1) {
            return null;
        }

        log.info("reading op descriptor of {} bytes", length);
        final byte[] bytes = in.readNBytes(length);
        return gson.fromJson(new String(bytes), OpDescriptor.class);
    }

    private static int getAttachedMatricesCount(final OpDescriptor op) {
        return (int) op.getOperands().stream()
                .filter(o -> o.getType() == OperandDescriptor.Type.MatrixDataAttached)
                .count();
    }

    private static PMatrixData readMatrix(final InputStream in) throws IOException {
        final int length = readLength(in);
        if (length == -1) {
            throw new IOException("unexpected EOF reached");
        }

        log.info("reading matrix of {} bytes", length);
        final byte[] bytes = in.readNBytes(length);
        return PMatrixData.fromBytes(bytes);
    }

    private List<PMatrixData> getOperandMatrices(OpDescriptor op, List<PMatrixData> attachedMatrices) {
        final Queue<PMatrixData> queue = new LinkedList<>(attachedMatrices);
        return op.getOperands().stream()
                .map(o -> switch (o.getType()) {
                    case MatrixId -> loadMatrix(o.getMatrixId());
                    case MatrixDataAttached -> queue.poll();
                    case RotationAngle -> null;
                    default -> throw new IllegalArgumentException("can't understand what todo with that operand");
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private static PMatrixData computeOp(final OpDescriptor op, final List<PMatrixData> matrices) {
        log.info("computing the math");
        return switch (op.getOperation()) {
            case Add -> Computations.add(matrices.get(0), matrices.get(1));
            case Rotate -> Computations.rotate(matrices.get(0), op.getOperands().get(1).getRotationAngle());
            case Transpose -> Computations.transpose(matrices.get(0));
        };
    }

    private static void writeResultDescriptor(final OutputStream out, final ResultDescriptor resultDescriptor) throws IOException {
        log.info("writing result descriptor to out");
        final byte[] bytes = gson.toJson(resultDescriptor).getBytes();
        writeLength(out, bytes.length);
        out.write(bytes);
    }

    private static void writeMatrix(final OutputStream out, final PMatrixData matrixData) throws IOException {
        log.info("writing matrix {} x {} to out", matrixData.getWidth(), matrixData.getHeight());
        final byte[] bytes = matrixData.asBytes();
        writeLength(out, bytes.length);
        out.write(bytes);
    }

    private static int readLength(final InputStream in) throws IOException {
        final byte[] bytes = in.readNBytes(4);
        if (bytes.length != 4) {
            return -1;
        }
        return ByteBuffer.wrap(bytes).getInt();
    }

    private static void writeLength(final OutputStream out, int length) throws IOException {
        final byte[] bytes = ByteBuffer.allocate(4).putInt(length).array();
        out.write(bytes);
    }

    // todo ak1 orchestrator support
    private PMatrixData loadMatrix(PMatrixId matrixId) {
        log.info("loading matrix by id {}", matrixId);
        return storageService.load(matrixId);
    }

    // todo ak1 orchestrator support
    private PMatrixId saveMatrix(PMatrixData result) {
        log.info("saving matrix {} x {}", result.getWidth(), result.getHeight());
        return storageService.save(result);
    }

    @ExceptionHandler
    public ResponseEntity<Void> handleMyCustomException(final Exception e) {
        log.error("exception during processing", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private record ResultInfo(ResultDescriptor resultDescriptor,
                              PMatrixData matrixDataToBeAttached) {

    }
}
