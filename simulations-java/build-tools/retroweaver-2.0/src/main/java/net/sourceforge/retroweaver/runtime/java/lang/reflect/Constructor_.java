package net.sourceforge.retroweaver.runtime.java.lang.reflect;

import java.lang.reflect.Constructor;

import net.sourceforge.retroweaver.runtime.java.lang.annotation.AIB;
import net.sourceforge.retroweaver.runtime.java.lang.annotation.Annotation;

/**
 * A mirror of java.lang.reflect.Constructor. Unfortunately, although this class
 * is almost a direct copy of Method_, there's not an easy way to share the
 * implementation between the two, because Method and Constructor don't have any
 * sort of typed relationship.
 * 
 * @author Toby Reyelts Date: Feb 21, 2005 Time: 2:40:01 AM
 */
public class Constructor_ {

	private Constructor_() {
		// private constructor
	}

	// Returns this element's annotation for the specified type if such an
	// annotation is present, else null.
	public static <T extends Annotation> T getAnnotation(final Constructor cons, final Class<T> annotationType) {
		final Class c = cons.getDeclaringClass();
		return AIB.getAib(c).getMethodAnnotation(cons.getName(), cons.getParameterTypes(), Void.class, annotationType);
	}

	// Returns all annotations present on this element.
	//
	// We have to search superclasses and interfaces, looking for annotations
	// which are meta-annotated with the @Inherited Annotation.
	// (Yuk)
	//
	public static Annotation[] getAnnotations(final Constructor cons) {
		return getDeclaredAnnotations(cons);
	}

	// Returns all annotations that are directly present on this element.
	public static Annotation[] getDeclaredAnnotations(final Constructor cons) {
		final Class c = cons.getDeclaringClass();
		return AIB.getAib(c).getMethodAnnotations(cons.getName(), cons.getParameterTypes(), Void.class);
	}

	// Returns true if an annotation for the specified type is present on this
	// element, else false.
	public static boolean isAnnotationPresent(final Constructor cons, final Class<? extends Annotation> annotationType) {
		return getAnnotation(cons, annotationType) != null;
	}

	public static Annotation[][] getParameterAnnotations(final Constructor cons) {
		final Class c = cons.getDeclaringClass();
		return AIB.getAib(c).getMethodParameterAnnotations(cons.getName(), cons.getParameterTypes(), Void.class);
	}

}
