package net.sourceforge.retroweaver.runtime.java.lang.reflect;

import net.sourceforge.retroweaver.runtime.java.lang.annotation.*;

public interface AnnotatedElement {

	// Returns this element's annotation for the specified type if such an
	// annotation is present, else null.
	<T extends Annotation> T getAnnotation(Class<T> annotationType);

	// Returns all annotations present on this element.
	Annotation[] getAnnotations();

	// Returns all annotations that are directly present on this element.
	Annotation[] getDeclaredAnnotations();

	// Returns true if an annotation for the specified type is present on this
	// element, else false.
	boolean isAnnotationPresent(Class<? extends Annotation> annotationType);

}
