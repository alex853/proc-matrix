package org.procmatrix.core;

import java.util.Objects;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class PMatrixId {
    private final UUID uuid;

    private PMatrixId(final UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PMatrixId pMatrixId = (PMatrixId) o;
        return uuid.equals(pMatrixId.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return uuid.toString();
    }

    public static PMatrixId fromString(final String uuid) {
        checkNotNull(uuid, "uuid should not be null");
        return new PMatrixId(UUID.fromString(uuid));
    }

    public static PMatrixId fromUuid(final UUID uuid) {
        checkNotNull(uuid, "uuid should not be null");
        return new PMatrixId(uuid);
    }

    public static PMatrixId random() {
        return new PMatrixId(UUID.randomUUID());
    }
}
