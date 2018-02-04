package net.deer.cuda;

final class JavaVersion {
    private static final double JAVA_CLASS_VERSION;

    static {
        try {
            JAVA_CLASS_VERSION = java.security.AccessController
                    .doPrivileged(new java.security.PrivilegedAction<Double>() {
                        public Double run() {
                            return Double.valueOf(System.getProperty("java.class.version"));
                        }
                    });
        } catch (Throwable t) {
            throw new Error(t);
        }
    }

    public static boolean isAtLeastJava7() {
        return JAVA_CLASS_VERSION >= 51.0;
    }

    public static boolean isAtLeastJava8() {
        return JAVA_CLASS_VERSION >= 52.0;
    }

    public static boolean isAtLeastJava9() {
        return JAVA_CLASS_VERSION >= 53.0;
    }

    public static boolean isAtLeastJava10() {
        return JAVA_CLASS_VERSION >= 54.0;
    }

    private JavaVersion() {
        throw new AssertionError();
    }
}
