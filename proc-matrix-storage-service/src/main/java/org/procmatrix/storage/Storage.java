package org.procmatrix.storage;

import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.PMatrixId;

public interface Storage {
    PMatrixId save(PMatrixData matrix);

    PMatrixData load(PMatrixId matrixId);

    void delete(PMatrixId matrixId);
}
