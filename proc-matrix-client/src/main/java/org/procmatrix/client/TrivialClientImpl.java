package org.procmatrix.client;

import org.procmatrix.computations.client.ComputationsService;
import org.procmatrix.computations.client.ComputationsServiceClientFactory;
import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.PMatrixId;
import org.procmatrix.core.ops.Op;
import org.procmatrix.core.ops.Result;
import org.procmatrix.storage.client.StorageService;
import org.procmatrix.storage.client.StorageServiceClientFactory;

import static com.google.common.base.Preconditions.checkNotNull;

class TrivialClientImpl implements PMatrixClient {
    private static final String STORAGE_SERVICE_URL = "http://localhost:8081";
    private static final String COMPUTATIONS_SERVICE_URL = "http://localhost:8082";

    private final StorageService storageService;
    private final ComputationsService computationsService;

    TrivialClientImpl() {
        this.storageService = StorageServiceClientFactory.createService(STORAGE_SERVICE_URL);
        this.computationsService = ComputationsServiceClientFactory.createService(COMPUTATIONS_SERVICE_URL);
    }

    @Override
    public PMatrixId save(final PMatrixData matrix) {
        return storageService.save(matrix);
    }

    @Override
    public PMatrixData load(final PMatrixId matrixId) {
        return storageService.load(matrixId);
    }

    @Override
    public void delete(final PMatrixId matrixId) {
        storageService.delete(matrixId);
    }

    @Override
    public Result[] compute(final Op... ops) {
        checkNotNull(ops, "ops array should not be null");

        return computationsService.compute(ops);
    }
}
