package net.sourceforge.retroweaver.runtime.java.lang;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

/**
 * Replacements for methods added to java.lang.Iterable in Java 1.5, used
 * for targets of the "foreach" statement.
 */
public final class Iterable_ {

	private Iterable_() {
		// private constructor
	}

	/**
	 * Returns an iterator for <code>iterable</code>.
	 * 
	 * @param iterable  the object to get the Iterator from
	 * @return an Iterator.
	 * @throws UnsupportedOperationException if an iterator method can not be found.
	 * @throws NullPointerException if <code>iterable</code> is null.
	 */
	public static Iterator iterator(final Object iterable) {
		if (iterable == null) {
			throw new NullPointerException(); // NOPMD by xlv
		}

		if (iterable instanceof Collection) {
			// core jdk classes implementing Iterable: they are not weaved but,
			// at least in 1.5, they all implement Collection and as its iterator
			// method exits in pre 1.5 jdks, a valid Iterator can be returned.
			return ((Collection) iterable).iterator();
		}

		if (iterable instanceof net.sourceforge.retroweaver.runtime.java.lang.Iterable) {
			// weaved classes inheriting from Iterable
			return ((net.sourceforge.retroweaver.runtime.java.lang.Iterable) iterable).iterator();
		}

		// for future jdk Iterable classes not inheriting from Collection
		// use reflection to try to get the iterator if it was present pre 1.5
		try {
			final Method method = iterable.getClass().getMethod("iterator", (Class[]) null);
			if (method != null) {
				return (Iterator) method.invoke(iterable, (Object[]) null);
			}
		} catch (Exception ignored) { // NOPMD by xlv
		}

		throw new UnsupportedOperationException("iterator call on " + iterable.getClass());
	}

}
