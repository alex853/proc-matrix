package org.procmatrix.storage.service;

import org.procmatrix.core.PMatrixData;
import org.procmatrix.core.PMatrixId;
import org.procmatrix.storage.impl.LocalStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.google.common.base.Preconditions.checkNotNull;

@RestController
@RequestMapping("storage/v1")
@CrossOrigin
public class StorageController {
    private static final Logger log = LoggerFactory.getLogger(StorageController.class);

    @Autowired
    private LocalStorage localStorage;

    @PutMapping("save")
    public ResponseEntity<String> saveMatrix(final @RequestBody byte[] bytes) {
        checkNotNull(bytes, "body should not be null");

        log.info("saving matrix of {} bytes", bytes.length);

        final PMatrixData matrix = PMatrixData.fromBytes(bytes);
        final PMatrixId matrixId = localStorage.save(matrix);

        return ResponseEntity.ok(matrixId.toString());
    }

    @GetMapping(value = "load")
    public ResponseEntity<byte[]> loadMatrix(final @RequestParam("id") String id) {
        checkNotNull(id, "id should not be null");

        final PMatrixId matrixId = PMatrixId.fromString(id);

        log.info("loading matrix by id {}", matrixId.getUuid());

        final PMatrixData data = localStorage.load(matrixId);

        return ResponseEntity.ok(data.asBytes());
    }

    @DeleteMapping(value = "delete")
    public ResponseEntity<Void> deleteMatrix(final @RequestParam("id") String id) {
        checkNotNull(id, "id should not be null");

        final PMatrixId matrixId = PMatrixId.fromString(id);

        log.info("deleting matrix by id {}", matrixId.getUuid());

        localStorage.delete(matrixId);

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler
    public ResponseEntity<Void> handleMyCustomException(final Exception e) {
        log.error("exception during processing", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
