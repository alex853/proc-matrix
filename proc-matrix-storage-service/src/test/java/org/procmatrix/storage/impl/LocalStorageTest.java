package org.procmatrix.storage.impl;

import org.junit.jupiter.api.Test;
import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.PMatrixId;
import org.procmatrix.core.PredefinedMatrices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocalStorageTest {
    private final LocalStorage localStorage = new LocalStorage();

    @Test
    public void test_save_then_load_small_matrix() {
        final PMatrixData data = PredefinedMatrices.randomSmallMatrix();

        PMatrixId id = null;
        try {
            id = localStorage.save(data);
            final PMatrixData loadedData = localStorage.load(id);

            assertEquals(data, loadedData);
        } finally {
            deleteQuietly(id);
        }
    }

    @Test
    public void test_save_then_load_huge_matrix() {
        final PMatrixData data = PredefinedMatrices.randomHugeMatrix();

        PMatrixId id = null;
        try {
            id = localStorage.save(data);
            final PMatrixData loadedData = localStorage.load(id);

            assertEquals(data, loadedData);
        } finally {
            deleteQuietly(id);
        }
    }

    @Test
    public void test_save_then_load_1gb_matrix() {
        final PMatrixData data = PredefinedMatrices.random1gbMatrix();

        PMatrixId id = null;
        try {
            id = localStorage.save(data);
            final PMatrixData loadedData = localStorage.load(id);

            assertEquals(data, loadedData);
        } finally {
            deleteQuietly(id);
        }
    }

    @Test
    public void test_delete() {
        final PMatrixData data = PredefinedMatrices.randomSmallMatrix();

        PMatrixId id = null;
        try {
            id = localStorage.save(data);
            localStorage.delete(id);
        } finally {
            deleteQuietly(id);
        }

        final PMatrixId idToDelete = id;
        assertThrows(RuntimeException.class,
                () -> localStorage.delete(idToDelete));
    }

    private void deleteQuietly(final PMatrixId id) {
        if (id == null) {
            return;
        }

        try {
            localStorage.delete(id);
        } catch (final Throwable e) {
            // noop
        }
    }
}
