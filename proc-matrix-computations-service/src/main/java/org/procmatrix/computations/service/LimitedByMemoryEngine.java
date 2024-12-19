package org.procmatrix.computations.service;

import java.util.LinkedList;
import java.util.Queue;

public class LimitedByMemoryEngine {
    private final long maxAllowedMemory = 8_000_000_000L;

    private final Queue<Void> queue = new LinkedList<>();

    // client sends operations description
    // server sends booking id and instruction which matrices to upload
    // possible response - ...
}
