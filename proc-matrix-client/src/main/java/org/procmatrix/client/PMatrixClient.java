package org.procmatrix.client;

import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.PMatrixId;
import org.procmatrix.core.ops.Op;
import org.procmatrix.core.ops.Result;

public interface PMatrixClient {

    PMatrixId save(PMatrixData matrix);

    PMatrixData load(PMatrixId matrixId);

    void delete(PMatrixId matrixId);

    Result[] compute(Op... ops);

}
