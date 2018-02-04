package java.lang.ref;

/**
 * A compilation stub only - <b>must not</b> be included in the
 * binary distribution!
 */
public abstract class Reference<T> {
    /**
     * @since 9
     */
    public static void reachabilityFence(Object ref) {
        throw new UnsupportedOperationException();
    }
}
