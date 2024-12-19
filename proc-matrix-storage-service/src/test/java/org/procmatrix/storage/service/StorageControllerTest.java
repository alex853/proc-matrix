package org.procmatrix.storage.service;

import org.junit.jupiter.api.Test;
import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.PMatrixId;
import org.procmatrix.core.PredefinedMatrices;
import org.procmatrix.storage.impl.LocalStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE) // default logging requires a lot of memory on huge matrices
public class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LocalStorage localStorage;

    @Test
    void test_save_and_load_small_matrix() throws Exception {
        final PMatrixData data = PredefinedMatrices.randomSmallMatrix();

        PMatrixId id = null;

        try {
            id = callSaveMatrix(data);

            final PMatrixData loadedData = callLoadMatrix(id);

            assertEquals(data, loadedData);
        } finally {
            deleteQuietly(id);
        }
    }

    @Test
    void test_save_and_load_big_matrix() throws Exception {
        final PMatrixData data = PredefinedMatrices.randomBigMatrix();

        PMatrixId id = null;

        try {
            id = callSaveMatrix(data);

            final PMatrixData loadedData = callLoadMatrix(id);

            assertEquals(data, loadedData);
        } finally {
            deleteQuietly(id);
        }
    }

    @Test
    void test_save_and_load_huge_matrix() throws Exception {
        final PMatrixData data = PredefinedMatrices.randomHugeMatrix();

        PMatrixId id = null;

        try {
            id = callSaveMatrix(data);

            final PMatrixData loadedData = callLoadMatrix(id);

            assertEquals(data, loadedData);
        } finally {
            deleteQuietly(id);
        }
    }

    @Test
    void test_save_and_load_1gb_matrix() throws Exception {
        final PMatrixData data = PredefinedMatrices.random1gbMatrix();

        PMatrixId id = null;

        try {
            id = callSaveMatrix(data);

            final PMatrixData loadedData = callLoadMatrix(id);

            assertEquals(data, loadedData);
        } finally {
            deleteQuietly(id);
        }
    }

    @Test
    void test_delete_matrix() throws Exception {
        final PMatrixData data = PredefinedMatrices.randomSmallMatrix();

        PMatrixId id = null;

        try {
            id = callSaveMatrix(data);
            callDeleteMatrix(id);
        } finally {
            deleteQuietly(id);
        }

        this.mockMvc
                .perform(delete("/storage/v1/delete")
                        .param("id", id.getUuid().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    private PMatrixData callLoadMatrix(PMatrixId id) throws Exception {
        final byte[] byteArray = this.mockMvc
                .perform(get("/storage/v1/load")
                        .param("id", id.getUuid().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsByteArray();
        return PMatrixData.fromBytes(byteArray);
    }

    private PMatrixId callSaveMatrix(PMatrixData data) throws Exception {
        final String content = this.mockMvc
                .perform(put("/storage/v1/save")
                        .content(data.asBytes())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return PMatrixId.fromString(content);
    }

    private void callDeleteMatrix(PMatrixId id) throws Exception {
        this.mockMvc
                .perform(delete("/storage/v1/delete")
                        .param("id", id.getUuid().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());;
    }

    private void deleteQuietly(final PMatrixId id) {
        if (id == null) {
            return;
        }

        try {
            localStorage.delete(id);
        } catch (final Throwable t) {
            // noop
        }
    }
}
