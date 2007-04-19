package net.sourceforge.retroweaver.runtime.java.lang;

public class Thread_ {

	private static final StackTraceElement[] EMPTY_STACK = new StackTraceElement[0];

	public static StackTraceElement[] getStackTrace(Thread t) {
		if (Thread.currentThread() == t) {
			return new RuntimeException().getStackTrace();
		} else {
			return EMPTY_STACK;
		}
	}

}
