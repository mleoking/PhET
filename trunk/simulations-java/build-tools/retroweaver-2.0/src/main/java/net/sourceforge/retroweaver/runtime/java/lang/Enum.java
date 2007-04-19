package net.sourceforge.retroweaver.runtime.java.lang;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A version of the 1.5 java.lang.Enum class for the 1.4 VM.
 */
public class Enum<E extends Enum<E>> implements Comparable<E>, Serializable {

	private static final long serialVersionUID = -1689726118486609581L;

	private final transient int ordinal_;

	private final String name_;

	private static final Map<Class, Object[]> enumValues = new HashMap<Class, Object[]>();

	protected Enum(final String name, final int ordinal) {
		this.name_ = name;
		this.ordinal_ = ordinal;
	}

	protected static final void setEnumValues(final Object[] values, final Class c) {
		synchronized(enumValues) {
			enumValues.put(c, values);
		}
	}

	protected static final <T> T[] getEnumValues(final Class<T> class_) {
		synchronized(enumValues) {
			final T[] values = (T[]) enumValues.get(class_);
			if (values != null) {
				return values;
			}
		}

		if (!class_.isEnum()) {
			return null;
		}

		// force initialization of class_ as
		// class loader may not have called static initializers yet
		try {
			Class.forName(class_.getName(), true, class_.getClassLoader());
		} catch (ClassNotFoundException e) {
			// can not happen: class_ has already been resolved.
		}
	
		synchronized(enumValues) {
			return (T[]) enumValues.get(class_);
		}
	}

	/**
	 * Implement serialization so we can get the singleton behavior we're
	 * looking for in enums.
	 */
	protected Object readResolve() throws ObjectStreamException {
		/*
		 * The implementation is based on "Java Object Serialization Specification",
		 * revision 1.5.0:
		 * 
		 * only the name_ is saved, serialVersionUID is 0L for all enum types
		 * InvalidObjectException is raised if valueOf() raises IllegalArgumentException.
		 * 
		 */

		final Class<E> clazz = getDeclaringClass();
		/*
		 * Note: getClass() would not work for enum inner classes
		 * such as CMYK.Cyan in the test suite.
		 */
		try {
			return valueOf(clazz, name_);
		} catch (IllegalArgumentException iae) {
			final InvalidObjectException ioe = new InvalidObjectException(name_ + " is not a valid enum for " + clazz.getName());
			try {
				ioe.initCause(iae);
			} catch (NoSuchMethodError nsm) {
				// cause should be set according to the spec but it's only available in 1.4
			}
				
			throw ioe;
		}
	}

	public static <T extends Enum<T>> T valueOf(final Class<T> enumType, final String name) {

		if (enumType == null) {
			throw new NullPointerException("enumType is null"); // NOPMD by xlv
		}

		if (name == null) {
			throw new NullPointerException("name_ is null"); // NOPMD by xlv
		}

		final T[] enums = getEnumValues(enumType);

		if (enums != null) {
			for (T enum_ : enums) {
				if (enum_.name_.equals(name)) {
					return enum_;
				}
			}
		}

		throw new IllegalArgumentException("No enum const " + enumType + "."
				+ name);
	}

	public final boolean equals(final Object other) {
		return other == this;
	}

	public final int hashCode() {
		return System.identityHashCode(this);
	}

	public String toString() {
		return name_;
	}

	public final int compareTo(final E e) {
		final Class c1 = getDeclaringClass();
		final Class c2 = e.getDeclaringClass();

		if (c1 == c2) { // NOPMD by xlv
			return ordinal_ - e.ordinal_;
		}

		throw new ClassCastException();
	}

	protected final Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public final String name() {
		return name_;
	}

	public final int ordinal() {
		return ordinal_;
	}

	public final Class<E> getDeclaringClass() {
		final Class clazz = getClass();
		final Class superClass = clazz.getSuperclass();
		if (superClass == Enum.class) {
			return clazz;
		} else {
			return superClass;
		}
	}

}
