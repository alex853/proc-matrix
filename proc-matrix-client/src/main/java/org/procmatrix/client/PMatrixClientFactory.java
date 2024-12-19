package org.procmatrix.client;

public class PMatrixClientFactory {

    private static class TrivialClientSingleton {
        private static final PMatrixClient client = new TrivialClientImpl();
    }

    public static PMatrixClient client() {
        return trivialClient(); // todo ak1 - trivial or orchestrated - should be controlled by some property
    }

    private static PMatrixClient trivialClient() {
        return TrivialClientSingleton.client;
    }

    private static PMatrixClient backedByOrchestrator() {
        throw new UnsupportedOperationException("PMatrixClientFactory.backedByOrchestrator not implemented");
    }
}
