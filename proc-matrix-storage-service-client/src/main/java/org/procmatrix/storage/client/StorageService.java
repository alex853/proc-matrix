package org.procmatrix.storage.client;

import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.PMatrixId;

public interface StorageService {

    PMatrixId save(PMatrixData matrix);

    PMatrixData load(PMatrixId matrixId);

    void delete(PMatrixId matrixId);

}
