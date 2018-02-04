package net.deer.cuda;

import java.security.AccessController;
import java.security.PrivilegedAction;

final class SystemProps {

	private static final String REACHABILITY_JNI_ENABLED_P = SystemProps.class.getName() + ".reachability.jni.enabled";

	static final boolean REACHABILITY_JNI = getBooleanPropVal(REACHABILITY_JNI_ENABLED_P, false);

	private static boolean getBooleanPropVal(final String prop, final boolean defVal) {
		return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
			@Override
			public Boolean run() {
				boolean val = defVal;
				try {
					String s = System.getProperty(prop, Boolean.toString(defVal));
					val = Boolean.parseBoolean(s.trim());
				} catch (IllegalArgumentException ignore) {
				} catch (NullPointerException ignore) {
				}
				return val;
			}
		});
	}

	private SystemProps() {
		throw new AssertionError();
	}
}
