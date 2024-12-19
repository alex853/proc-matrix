package org.procmatrix.storage.impl;

import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.PMatrixId;
import org.procmatrix.storage.Storage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.google.common.base.Preconditions.checkNotNull;

public class LocalStorage implements Storage {
    private static final Path root = Paths.get("./file-system-matrix-storage");

    @Override
    public PMatrixId save(final PMatrixData matrix) {
        checkNotNull(matrix, "matrix should not be null");

        final PMatrixId id = PMatrixId.random();
        final byte[] bytes = matrix.asBytes();
        final ByteArrayInputStream in = new ByteArrayInputStream(bytes);

        final Path dataPath = root.resolve(id.getUuid() + ".data");

        try {
            Files.createDirectories(root);
            Files.copy(in, dataPath, StandardCopyOption.REPLACE_EXISTING);
            return id;
        } catch (IOException e) {
            throw new RuntimeException("unable to save matrix", e);
        }
    }

    @Override
    public PMatrixData load(final PMatrixId matrixId) {
        checkNotNull(matrixId, "id should not be null");

        final Path dataPath = root.resolve(matrixId.getUuid() + ".data");

        byte[] bytes;
        try {
            bytes = Files.readAllBytes(dataPath);
        } catch (IOException e) {
            throw new RuntimeException("unable to load matrix", e);
        }

        return PMatrixData.fromBytes(bytes);
    }

    @Override
    public void delete(final PMatrixId matrixId) {
        checkNotNull(matrixId, "id should not be null");

        final Path dataPath = root.resolve(matrixId.getUuid() + ".data");

        try {
            Files.delete(dataPath);
        } catch (IOException e) {
            throw new RuntimeException("unable to delete matrix", e);
        }
    }
}
