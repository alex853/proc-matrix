package org.procmatrix.storage.client;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.PMatrixId;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

class StorageServiceImpl implements StorageService {
    private final StorageServiceApi storageServiceApi;

    StorageServiceImpl(StorageServiceApi storageServiceApi) {
        this.storageServiceApi = storageServiceApi;
    }

    @Override
    public PMatrixId save(final PMatrixData matrix) {
        checkNotNull(matrix, "matrix should not be null");

        try {
            final Call<String> call = storageServiceApi.save(
                    RequestBody.create(
                            MediaType.parse("application/octet-stream"),
                            matrix.asBytes()));
            final Response<String> response = call.execute();
            checkState(response.isSuccessful(), "save is not successful");
            return PMatrixId.fromString(response.body());
        } catch (final IOException e) {
            throw new RuntimeException("unable to save matrix", e);
        }
    }

    @Override
    public PMatrixData load(final PMatrixId matrixId) {
        checkNotNull(matrixId, "matrixId should not be null");

        try {
            final Call<ResponseBody> call = storageServiceApi.load(matrixId.toString());
            final Response<ResponseBody> response = call.execute();
            checkState(response.isSuccessful(), "load is not successful");
            try (final ResponseBody body = response.body()) {
                checkNotNull(body, "body should not be null");
                final byte[] bytes = body.bytes();
                return PMatrixData.fromBytes(bytes);
            }
        } catch (final IOException e) {
            throw new RuntimeException("unable to load matrix", e);
        }
    }

    @Override
    public void delete(final PMatrixId matrixId) {
        checkNotNull(matrixId, "matrixId should not be null");

        try {
            final Call<Void> call = storageServiceApi.delete(matrixId.toString());
            final Response<Void> response = call.execute();
            checkState(response.isSuccessful(), "load is not successful");
        } catch (final IOException e) {
            throw new RuntimeException("unable to delete matrix", e);
        }
    }
}
