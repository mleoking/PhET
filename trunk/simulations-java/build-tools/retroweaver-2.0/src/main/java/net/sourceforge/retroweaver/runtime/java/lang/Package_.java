package net.sourceforge.retroweaver.runtime.java.lang;

import java.lang.annotation.Annotation;

/**
 * A mirror of java.lang.Package
 * 
 * @author Toby Reyelts Date: Feb 20, 2005 Time: 11:50:47 PM
 */
public class Package_ {

	private Package_() {
		// private constructor
	}

	/**
	 * Implementation notes:
	 * ---------------------
	 * <p/>
	 * Package annotations are a little different from other annotations. The java compiler writes
	 * them into a synthetic interface named, <package-name>.package-info. Here, we implicitly load that
	 * class and pass it onto Class_ when asked for annotation information on a package.
	 * <p/>
	 *
	 */
	private static Class getPackageAnnotationClass(final Package p) {
		try {
			return Class.forName(p.getName() + ".package-info");
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	// Provide AnnotatedElement methods

	// Returns this element's annotation for the specified type if such an
	// annotation is present, else null.
	public static <T extends Annotation> T getAnnotation(final Package p, final Class<T> annotationType) {
		final Class c = getPackageAnnotationClass(p);
		if (c == null) {
			return null;
		} else {
			return (T) c.getAnnotation(annotationType);
		}
	}

	private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[] {};

	// Returns all annotations present on this element.
	public static Annotation[] getAnnotations(final Package p) {
		final Class c = getPackageAnnotationClass(p);
		if (c == null) {
			return EMPTY_ANNOTATION_ARRAY; // NOPMD by xlv
		} else {
			return c.getAnnotations();
		}
	}

	// Returns all annotations that are directly present on this element.
	public static Annotation[] getDeclaredAnnotations(final Package p) {
		final Class c = getPackageAnnotationClass(p);
		if (c == null) {
			return EMPTY_ANNOTATION_ARRAY; // NOPMD by xlv
		} else {
			return c.getDeclaredAnnotations();
		}
	}

	// Returns true if an annotation for the specified type is present on this
	// element, else false.
	public static boolean isAnnotationPresent(final Package p, final Class<? extends Annotation> annotationType) {
		final Class c = getPackageAnnotationClass(p);
		if (c == null) {
			return false;
		} else {
			return c.isAnnotationPresent(annotationType);
		}
	}

}
