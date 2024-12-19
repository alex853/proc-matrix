package org.procmatrix.integrationtests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.procmatrix.client.PMatrixClient;
import org.procmatrix.client.PMatrixClientFactory;
import org.procmatrix.core.*;
import org.procmatrix.core.ops.Op;
import org.procmatrix.core.ops.Result;

import java.util.*;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.procmatrix.core.RotationAngle.*;

public class IntegrationTests {
    @ParameterizedTest
    @ValueSource(strings = {"tiny", "small", "big"})
    public void test__add__two_attached_matrices__and_return(String size) {
        PMatrixClient client = PMatrixClientFactory.client();

        PMatrixData matrix1 = Utils.randomMatrixBySize(size);
        PMatrixData matrix2 = Utils.randomMatrixBySize(size);

        PMatrixData resultedMatrix = client.compute(Op.add(matrix1, matrix2).andReturn())[0].getMatrixData();

        assertEquals(Computations.add(matrix1, matrix2), resultedMatrix);
    }

    @ParameterizedTest
    @ValueSource(strings = {"tiny", "small", "big"})
    public void test__add__two_stored_matrices__and_store(String size) {
        PMatrixClient client = PMatrixClientFactory.client();

        PMatrixData matrix1 = Utils.randomMatrixBySize(size);
        PMatrixData matrix2 = Utils.randomMatrixBySize(size);

        PMatrixId matrix1Id = client.save(matrix1);
        PMatrixId matrix2Id = client.save(matrix2);

        PMatrixId resultedMatrixId = client.compute(Op.add(matrix1Id, matrix2Id).andStore())[0].getMatrixId();

        PMatrixData resultedMatrix = client.load(resultedMatrixId);

        assertEquals(Computations.add(matrix1, matrix2), resultedMatrix);
    }

    @ParameterizedTest
    @ValueSource(strings = {"tiny", "small", "big"})
    public void test__add__stored_and_attached_matrices__and_return(String size) {
        PMatrixClient client = PMatrixClientFactory.client();

        PMatrixData matrix1 = Utils.randomMatrixBySize(size);
        PMatrixData matrix2 = Utils.randomMatrixBySize(size);

        PMatrixId matrix1Id = client.save(matrix1);

        PMatrixData resultedMatrix = client.compute(Op.add(matrix1Id, matrix2).andReturn())[0].getMatrixData();

        assertEquals(Computations.add(matrix1, matrix2), resultedMatrix);
    }

    @ParameterizedTest
    @ValueSource(strings = {"tiny", "small", "big"})
    public void test__add__attached_and_stored_matrices__and_return(String size) {
        PMatrixClient client = PMatrixClientFactory.client();

        PMatrixData matrix1 = Utils.randomMatrixBySize(size);
        PMatrixData matrix2 = Utils.randomMatrixBySize(size);

        PMatrixId matrix2Id = client.save(matrix2);

        PMatrixData resultedMatrix = client.compute(Op.add(matrix1, matrix2Id).andReturn())[0].getMatrixData();

        assertEquals(Computations.add(matrix1, matrix2), resultedMatrix);
    }

    @ParameterizedTest
    @ValueSource(strings = {"tiny", "small", "big"})
    public void test__rotate__cw90__attached_matrix__and_store(String size) {
        PMatrixClient client = PMatrixClientFactory.client();

        PMatrixData matrix = Utils.randomMatrixBySize(size);

        PMatrixData resultedMatrix = client.compute(Op.rotate(matrix, cw90).andReturn())[0].getMatrixData();

        assertEquals(Computations.rotate(matrix, cw90), resultedMatrix);
    }

    @Test
    public void test__rotate__attached_matrix__all_angles() {
        PMatrixClient client = PMatrixClientFactory.client();

        PMatrixData matrix = PredefinedMatrices.randomSmallMatrix();

        for (RotationAngle angle : RotationAngle.values()) {
            PMatrixData resultedMatrix = client.compute(Op.rotate(matrix, angle).andReturn())[0].getMatrixData();

            assertEquals(Computations.rotate(matrix, angle), resultedMatrix);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"tiny", "small", "big"})
    public void test__transpose__attached_matrix__and_store(String size) {
        PMatrixClient client = PMatrixClientFactory.client();

        PMatrixData matrix = Utils.randomMatrixBySize(size);

        PMatrixId resultedMatrixId = client.compute(Op.transpose(matrix).andStore())[0].getMatrixId();

        PMatrixData resultedMatrix = client.load(resultedMatrixId);

        assertEquals(Computations.transpose(matrix), resultedMatrix);
    }

    @ParameterizedTest
    @ValueSource(strings = {"tiny", "small", "big"})
    public void test__transpose_twice__attached_matrix__and_return(String size) {
        PMatrixClient client = PMatrixClientFactory.client();

        PMatrixData matrix = Utils.randomMatrixBySize(size);

        PMatrixData transposedMatrix = client.compute(Op.transpose(matrix).andReturn())[0].getMatrixData();
        PMatrixData resultedMatrix = client.compute(Op.transpose(transposedMatrix).andReturn())[0].getMatrixData();

        assertEquals(matrix, resultedMatrix);
    }

    @Test
    public void test__batch__3_adds_of_different_sizes() {
        PMatrixClient client = PMatrixClientFactory.client();

        PMatrixData m1t = PredefinedMatrices.randomTinyMatrix();
        PMatrixData m2t = PredefinedMatrices.randomTinyMatrix();

        PMatrixData m1s = PredefinedMatrices.randomSmallMatrix();
        PMatrixData m2s = PredefinedMatrices.randomSmallMatrix();

        PMatrixData m1b = PredefinedMatrices.randomBigMatrix();
        PMatrixData m2b = PredefinedMatrices.randomBigMatrix();

        Op addOpT = Op.add(m1t, m2t).andReturn();
        Op addOpS = Op.add(m1s, m2s).andReturn();
        Op addOpB = Op.add(m1b, m2b).andReturn();

        Result[] results = client.compute(addOpT, addOpS, addOpB);
        Map<UUID, PMatrixData> resultsById = Arrays.stream(results).collect(toMap(Result::getOperationId, Result::getMatrixData));

        assertEquals(Computations.add(m1t, m2t), resultsById.get(addOpT.getOperationId()));
        assertEquals(Computations.add(m1s, m2s), resultsById.get(addOpS.getOperationId()));
        assertEquals(Computations.add(m1b, m2b), resultsById.get(addOpB.getOperationId()));
    }

    @Test
    public void test__batch__50_adds() {
        int opsCount = 50;

        PMatrixClient client = PMatrixClientFactory.client();

        int matrixCount = 10;
        PMatrixData[] matrices = new PMatrixData[matrixCount];
        for (int i = 0; i < matrixCount; i++) {
            matrices[i] = PredefinedMatrices.randomSmallMatrix();
        }

        List<Op> ops = new ArrayList<>();
        Map<UUID, PMatrixData> expectedResults = new HashMap<>();
        for (int i = 0; i < opsCount; i++) {
            int matrix1index = i % matrixCount;
            int matrix2index = (int)Math.round((i+1)*1.23) % matrixCount;

            PMatrixData matrix1 = matrices[matrix1index];
            PMatrixData matrix2 = matrices[matrix2index];

            Op op = Op.add(matrix1, matrix2).andReturn();
            expectedResults.put(op.getOperationId(), Computations.add(matrix1, matrix2));

            ops.add(op);
        }

        Result[] results = client.compute(ops.toArray(new Op[0]));

        Map<UUID, PMatrixData> resultsById = Arrays.stream(results).collect(toMap(Result::getOperationId, Result::getMatrixData));

        for (Op op : ops) {
            assertEquals(expectedResults.get(op.getOperationId()), resultsById.get(op.getOperationId()));
        }
    }

    @Test
    public void test__batch__mix_of_adds_rotates_transposes() {
        int opsCount = 50;

        PMatrixClient client = PMatrixClientFactory.client();

        int matrixCount = 10;
        PMatrixData[] matrices = new PMatrixData[matrixCount];
        for (int i = 0; i < matrixCount; i++) {
            matrices[i] = PredefinedMatrices.randomSmallMatrix();
        }

        List<Op> ops = new ArrayList<>();
        Map<UUID, PMatrixData> expectedResults = new HashMap<>();
        for (int i = 0; i < opsCount; i++) {
            int matrix1index = i % matrixCount;
            int matrix2index = (int)Math.round((i+1)*1.23) % matrixCount;

            PMatrixData matrix1 = matrices[matrix1index];
            PMatrixData matrix2 = matrices[matrix2index];

            Op op;
            PMatrixData expectedResult;

            double rnd = Math.random();
            if (rnd < 0.2) {
                op = Op.add(matrix1, matrix2).andReturn();
                expectedResult = Computations.add(matrix1, matrix2);
            } else if (rnd < 0.6) {
                RotationAngle angle = RotationAngle.values()[(int) (Math.random() * RotationAngle.values().length)];
                op = Op.rotate(matrix1, angle).andReturn();
                expectedResult = Computations.rotate(matrix1, angle);
            } else {
                op = Op.transpose(matrix1).andReturn();
                expectedResult = Computations.transpose(matrix1);
            }

            ops.add(op);
            expectedResults.put(op.getOperationId(), expectedResult);
        }

        Result[] results = client.compute(ops.toArray(new Op[0]));

        Map<UUID, PMatrixData> resultsById = Arrays.stream(results).collect(toMap(Result::getOperationId, Result::getMatrixData));

        for (Op op : ops) {
            assertEquals(expectedResults.get(op.getOperationId()), resultsById.get(op.getOperationId()));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"tiny", "small", "big", "huge", "1gb"})
    public void test__batch___several_heavy_matrices__needs_12gb_of_heap(String size) {
        PMatrixClient client = PMatrixClientFactory.client();

        PMatrixData m1 = Utils.randomMatrixBySize(size);
        PMatrixData m2 = Utils.randomMatrixBySize(size);
        PMatrixData m3 = Utils.randomMatrixBySize(size);

        Op addOp1 = Op.add(m1, m2).andReturn();
        Op addOp2 = Op.add(m2, m3).andReturn();
        Op rotateOp = Op.rotate(m2, ccw90).andReturn();
        Op transposeOp = Op.transpose(m3).andReturn();

        Result[] results = client.compute(transposeOp, addOp1, rotateOp, addOp2);

        Map<UUID, PMatrixData> resultsById = Arrays.stream(results).collect(toMap(Result::getOperationId, Result::getMatrixData));

        assertEquals(Computations.add(m1, m2), resultsById.get(addOp1.getOperationId()));
        assertEquals(Computations.add(m2, m3), resultsById.get(addOp2.getOperationId()));
        assertEquals(Computations.rotate(m2, ccw90), resultsById.get(rotateOp.getOperationId()));
        assertEquals(Computations.transpose(m3), resultsById.get(transposeOp.getOperationId()));
    }

    @Test
    public void test__add__wrong_matrix_sizes() {
        PMatrixClient client = PMatrixClientFactory.client();

        PMatrixData matrix1 = PredefinedMatrices.randomTinyMatrix();
        PMatrixData matrix2 = PredefinedMatrices.randomSmallMatrix();

        Result result = client.compute(Op.add(matrix1, matrix2).andReturn())[0];
        assertEquals(Result.Type.Error, result.getType());
    }

    @Test
    public void test__add__one_op_failing_while_several_are_fine() {
        PMatrixClient client = PMatrixClientFactory.client();

        PMatrixData matrix1tiny = PredefinedMatrices.randomTinyMatrix();

        PMatrixData matrix2small = PredefinedMatrices.randomSmallMatrix();
        PMatrixData matrix3small = PredefinedMatrices.randomSmallMatrix();
        PMatrixData matrix4small = PredefinedMatrices.randomSmallMatrix();

        Op fineOp1 = Op.add(matrix2small, matrix3small).andReturn();
        Op fineOp2 = Op.transpose(matrix4small).andReturn();
        Op failingOp = Op.add(matrix4small, matrix1tiny).andReturn();
        Op fineOp3 = Op.add(matrix4small, matrix2small).andReturn();

        Result[] results = client.compute(fineOp1, fineOp2, failingOp, fineOp3);
        Map<UUID, Result> resultsById = Arrays.stream(results).collect(toMap(Result::getOperationId, r -> r));

        assertNotEquals(Result.Type.Error, resultsById.get(fineOp1.getOperationId()).getType());
        assertNotEquals(Result.Type.Error, resultsById.get(fineOp2.getOperationId()).getType());
        assertEquals(Result.Type.Error, resultsById.get(failingOp.getOperationId()).getType());
        assertNotEquals(Result.Type.Error, resultsById.get(fineOp3.getOperationId()).getType());
    }
}