package org.procmatrix.computations.client;

import com.google.gson.Gson;
import okhttp3.*;
import okio.BufferedSink;
import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.ops.Op;
import org.procmatrix.core.ops.Operand;
import org.procmatrix.core.ops.Result;
import org.procmatrix.core.ops.descriptor.OpDescriptor;
import org.procmatrix.core.ops.descriptor.ResultDescriptor;
import org.slf4j.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ComputationsServiceImpl implements ComputationsService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ComputationsServiceImpl.class);
    private static final Gson gson = new Gson();

    private final ComputationsServiceApi computationsServiceApi;

    ComputationsServiceImpl(final ComputationsServiceApi computationsServiceApi) {
        this.computationsServiceApi = computationsServiceApi;
    }

    // todo ak1 kind of async result fetching to invoking code?
    @Override
    public Result[] compute(final Op... ops) {
        checkNotNull(ops, "ops should be specified");
        checkArgument(ops.length >= 1, "at least one op should be specified");

        try {
            final AtomicBoolean submittedSuccessfully = submitOps(ops);

            final Set<String> awaitingForIds = Arrays.stream(ops)
                    .map(op -> op.getOperationId().toString())
                    .collect(Collectors.toSet());
            final List<Result> results = new ArrayList<>();

            while (!awaitingForIds.isEmpty() && submittedSuccessfully.get()) {
                final List<Result> eachResults = retrieveResults(awaitingForIds.stream().toList());

                results.addAll(eachResults);
                //noinspection SlowAbstractSetRemoveAll
                awaitingForIds.removeAll(eachResults.stream().map(r -> r.getOperationId().toString()).toList());
            }

            return results.toArray(new Result[0]);
        } catch (final IOException e) {
            throw new RuntimeException("error on compute", e);
        }
    }

    private AtomicBoolean submitOps(final Op... ops) {
        final AtomicBoolean submittedSuccessfully = new AtomicBoolean(true);
        final Call<String> submitCall = computationsServiceApi.submitBatch(
                new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return MediaType.get("application/octet-stream");
                    }

                    @SuppressWarnings("NullableProblems")
                    @Override
                    public void writeTo(final BufferedSink sink) throws IOException {
                        for (final Op op : ops) {
                            writeOpDescriptor(sink, OpDescriptor.from(op));
                            sink.flush();

                            for (final Operand o : op.getOperands()) {
                                if (o.getType() == Operand.Type.MatrixData) {
                                    writeMatrix(sink, o.getMatrixData());
                                    sink.flush();
                                }
                            }
                        }
                    }
                });

        log.info(String.format("submitting batch containing %s ops", ops.length));
        submitCall.enqueue(new Callback<>() {
            @SuppressWarnings("NullableProblems")
            @Override
            public void onResponse(final Call<String> call, final Response<String> response) {
                log.info(String.format("submit call finished, response code %s, response body '%s'", response.code(), response.body()));
                submittedSuccessfully.set(response.isSuccessful());
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public void onFailure(final Call<String> call, final Throwable t) {
                log.error("exception on submitting", t);
                submittedSuccessfully.set(false);
            }
        });

        return submittedSuccessfully;
    }

    private List<Result> retrieveResults(final List<String> idsToRetrieve) throws IOException {
        final Call<ResponseBody> retrieveCall = computationsServiceApi.retrieveResults(idsToRetrieve);

        final Response<ResponseBody> response = retrieveCall.execute();

        try (final ResponseBody responseBody = response.body()) {
            if (responseBody == null) {
                return Collections.emptyList();
            }

            try (final InputStream in = responseBody.byteStream()) {
                final List<Result> results = new ArrayList<>();

                while (true) {
                    final ResultDescriptor resultDescriptor = readResultDescriptor(in);
                    if (resultDescriptor == null) {
                        break;
                    }

                    if (resultDescriptor.getType() == ResultDescriptor.Type.MatrixId) {
                        results.add(Result.of(resultDescriptor.getOperationId(), resultDescriptor.getMatrixId()));
                    } else if (resultDescriptor.getType() == ResultDescriptor.Type.MatrixDataAttached) {
                        final PMatrixData matrixData = readMatrix(in);
                        results.add(Result.of(resultDescriptor.getOperationId(), matrixData));
                    } else if (resultDescriptor.getType() == ResultDescriptor.Type.Error) {
                        results.add(Result.ofError(resultDescriptor.getOperationId()));
                    } else {
                        throw new IllegalStateException("don't know what to do with type " + resultDescriptor.getType());
                    }
                }

                return results;
            }
        }
    }

    private static void writeOpDescriptor(final BufferedSink out, final OpDescriptor opDescriptor) throws IOException {
        log.info("writing op descriptor to out");
        final byte[] bytes = gson.toJson(opDescriptor).getBytes();
        writeLength(out, bytes.length);
        out.write(bytes);
    }

    private static void writeMatrix(final BufferedSink out, final PMatrixData matrixData) throws IOException {
        log.info(String.format("writing matrix %s x %s to out", matrixData.getWidth(), matrixData.getHeight()));
        final byte[] bytes = matrixData.asBytes();
        writeLength(out, bytes.length);
        out.write(bytes);
    }

    private static ResultDescriptor readResultDescriptor(final InputStream in) throws IOException {
        final int length = readLength(in);
        if (length == -1) {
            return null;
        }

        log.info(String.format("reading result descriptor of %s bytes", length));
        final byte[] bytes = in.readNBytes(length);
        return gson.fromJson(new String(bytes), ResultDescriptor.class);
    }

    private static PMatrixData readMatrix(final InputStream in) throws IOException {
        final int length = readLength(in);
        if (length == -1) {
            throw new IOException("unexpected EOF reached");
        }

        log.info(String.format("reading matrix of %s bytes", length));
        final byte[] bytes = in.readNBytes(length);
        return PMatrixData.fromBytes(bytes);
    }

    private static int readLength(final InputStream in) throws IOException {
        final byte[] bytes = in.readNBytes(4);
        if (bytes.length != 4) {
            return -1;
        }
        return ByteBuffer.wrap(bytes).getInt();
    }

    private static void writeLength(final BufferedSink out, int length) throws IOException {
        final byte[] bytes = ByteBuffer.allocate(4).putInt(length).array();
        out.write(bytes);
    }
}
