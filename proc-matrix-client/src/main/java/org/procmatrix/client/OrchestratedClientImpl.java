package org.procmatrix.client;

import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.PMatrixId;
import org.procmatrix.core.ops.Op;
import org.procmatrix.core.ops.Result;
import org.procmatrix.storage.client.StorageService;
import org.procmatrix.storage.client.StorageServiceClientFactory;

import static com.google.common.base.Preconditions.checkNotNull;

class OrchestratedClientImpl implements PMatrixClient {
    private static final String ORCHESTRATOR_SERVICE_URL = "http://localhost:8761";

    OrchestratedClientImpl() {
    }

    @Override
    public PMatrixId save(final PMatrixData matrix) {
        return getRandomStorageService().save(matrix);
    }

    @Override
    public PMatrixData load(final PMatrixId matrixId) {
        return getRandomStorageService().load(matrixId);
    }

    @Override
    public void delete(final PMatrixId matrixId) {
        getRandomStorageService().delete(matrixId);
    }

    @Override
    public Result[] compute(final Op... ops) {
        checkNotNull(ops, "ops array should not be null");

        // todo ak1 send descriptor to orchestrator
        // todo ak1 orchestrator will answer with batch id and some instructions
        // todo ak1 some cycle while not all computed
        // todo ak1 send compute ops according to known instructions
        // todo ak1 poll orchestrator regularly
        // todo ak1 send compute ops according to his answers
        // todo ak1 build and return results once all ops will be computed
        throw new UnsupportedOperationException("PMatrixClientImpl.compute not implemented");
    }

    private StorageService getRandomStorageService() {
        final String storageServiceUrl = getRandomStorageServiceUrl();
        return StorageServiceClientFactory.createService(storageServiceUrl);
    }

    private String getRandomStorageServiceUrl() {
        // todo ak1 final DiscoveryClient
        throw new UnsupportedOperationException("OrchestratedClientImpl.getRandomStorageServiceUrl not implemented");
    }
}
