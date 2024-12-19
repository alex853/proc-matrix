package org.procmatrix.computations.client;

import org.procmatrix.core.ops.Op;
import org.procmatrix.core.ops.Result;

public interface ComputationsService {

    Result[] compute(Op... ops);

}
