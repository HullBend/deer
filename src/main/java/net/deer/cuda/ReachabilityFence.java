package net.deer.cuda;

import java.lang.ref.Reference;
import java.lang.reflect.Method;

/**
 * Poor man's substitute for the {@code reachabilityFence} introduced in Java 9.
 * <p>
 * See <a
 * href="https://docs.oracle.com/javase/9/docs/api/java/lang/ref/Reference.html#reachabilityFence-java.lang.Object-">Java
 * 9 reachabilityFence Javadoc</a>.
 */
final class ReachabilityFence {

	private static final boolean FENCE_METHOD_EXISTS = fenceMethodExists();
	private static final boolean USE_JNI = SystemProps.REACHABILITY_JNI;

	/**
	 * Ensures that the object referenced by the given reference remains
	 * <em>strongly reachable</em>, regardless of any prior actions of the
	 * program that might otherwise cause the object to become unreachable;
	 * thus, the referenced object is not reclaimable by garbage collection at
	 * least until after the invocation of this method.
	 * <p>
	 * This method is applicable only when reclamation may have visible effects,
	 * which is possible for objects with finalizers (See <a
	 * href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-12.html#jls-12.6">
	 * Section 12.6 17 of <cite>The Java&trade; Language
	 * Specification</cite></a>) that are implemented in ways that rely on
	 * ordering control for correctness.
	 * <p>
	 * The garbage collector may reclaim an object even if a field of that
	 * object is still in use, so long as the object has otherwise become
	 * unreachable. In the typical case, the field is a long integer
	 * representing a "native" C++ pointer. When the object is reclaimed the C++
	 * object referenced by the field is deleted in the object's
	 * <em>finalize()</em> method.
	 * <p>
	 * As an example, consider the code sequence
	 * {@code long p = o.pointer; foo(p);} where {@code foo()} is probably a
	 * static method. If obtaining the pointer field from {@code o} was the last
	 * reference to {@code o} then {@code o} may now be found to be unreachable
	 * and reclaimed after the initial assignment is executed. But the original
	 * state associated with {@code p} may still be needed by {@code foo()}. If
	 * the <em>finalize()</em> method deallocates native objects needed by
	 * native calls invoked by {@code foo()}, this can introduce (rare and hard
	 * to test for) native heap corruption.
	 * 
	 * @param ref
	 *            the reference. If {@code null}, this method has no effect.
	 */
	public static void protect(Object ref) {
		if (ref != null) {
			if (FENCE_METHOD_EXISTS) {
				Reference.reachabilityFence(ref);
			} else if (USE_JNI) {
				protectN(ref);
			} else {
				protectJ(ref);
			}
		}
	}

	// The approach is that if a referent is passed through to a JNI method,
	// then such referents must be kept alive for the entire time the native
	// method executes, because the native method may call methods or access
	// fields on such a referent.
	// Because there's no cross-native-boundary optimization going on currently,
	// JNI has to guarantee that.
	private static native void protectN(Object ref);

	// This should be good enough for most practical purposes, though strictly
	// speaking it doesn't provide full guarantees preventing GC
	private static void protectJ(Object ref) {
		if (ref != null && ref.getClass() == null) {
			throw new IllegalStateException();
		}
	}

	private static boolean fenceMethodExists() {
		if (JavaVersion.isAtLeastJava9()) {
			Method m = null;
			try {
				m = Reference.class.getDeclaredMethod("reachabilityFence", Object.class);
			} catch (Exception ignore) {
			}
			return m != null;
		}
		return false;
	}

	private ReachabilityFence() {
		throw new AssertionError();
	}
}
