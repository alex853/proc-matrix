package org.procmatrix.helloworld;

import org.procmatrix.client.PMatrixClient;
import org.procmatrix.client.PMatrixClientFactory;
import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.ops.Op;

public class HelloWorld {
    public static void main(String[] args) {
        PMatrixClient client = PMatrixClientFactory.client();

        PMatrixData matrix1 = PMatrixData.of(3, 3, new int[] {
                1, 2, 3,
                4, 5, 6,
                7, 8, 9});
        PMatrixData matrix2 = PMatrixData.of(3, 3, new int[] {
                11, 12, 13,
                14, 15, 16,
                17, 18, 19});

        PMatrixData resultedMatrix = client.compute(Op.add(matrix1, matrix2).andReturn())[0].getMatrixData();

        System.out.println(resultedMatrix.toPrintedString());
    }
}
