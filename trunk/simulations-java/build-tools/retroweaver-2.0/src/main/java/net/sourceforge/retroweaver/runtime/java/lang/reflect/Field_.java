package net.sourceforge.retroweaver.runtime.java.lang.reflect;

import net.sourceforge.retroweaver.runtime.java.lang.annotation.AIB;
import net.sourceforge.retroweaver.runtime.java.lang.annotation.Annotation;

import java.lang.reflect.Field;

/**
 * A mirror of java.lang.reflect.Field.
 * 
 * @author Toby Reyelts Date: Feb 21, 2005 Time: 2:29:41 AM
 */
public class Field_ {

	private Field_() {
		// private constructor
	}

	// Returns this element's annotation for the specified type if such an
	// annotation is present, else null.
	public static <T extends Annotation> T getAnnotation(final Field f, final Class<T> annotationType) {
		final Class c = f.getDeclaringClass();
		return AIB.getAib(c).getFieldAnnotation(f.getName(), annotationType);
	}

	// Returns all annotations present on this element.
	//
	public static Annotation[] getAnnotations(final Field f) {
		return getDeclaredAnnotations(f);
	}

	// Returns all annotations that are directly present on this element.
	public static Annotation[] getDeclaredAnnotations(final Field field) {
		final Class c = field.getDeclaringClass();
		return AIB.getAib(c).getFieldAnnotations(field.getName());
	}

	// Returns true if an annotation for the specified type is present on this
	// element, else false.
	public static boolean isAnnotationPresent(final Field field, final Class<? extends Annotation> annotationType) {
		return getAnnotation(field, annotationType) != null;
	}

}
