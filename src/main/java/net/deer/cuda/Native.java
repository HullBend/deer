package net.deer.cuda;

final class Native {

    static native boolean initializeHandleCacheN();

    private Native() {
        throw new AssertionError();
    }
}
