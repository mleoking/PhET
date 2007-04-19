package net.sourceforge.retroweaver.runtime.java.lang.annotation;

import java.lang.reflect.Method;

/**
 * A mirror of java.lang.annotation.AnnotationTypeMismatchException
 * 
 * @author Toby Reyelts
 */
public class AnnotationTypeMismatchException extends RuntimeException {

	private final Method element_;

	private final String foundType_;

	public AnnotationTypeMismatchException(final Method element, final String foundType) {
		super("type error: " + foundType + " for " + element);
		this.element_ = element;
		this.foundType_ = foundType;
	}

	public Method element() {
		return element_;
	}

	public String foundType() {
		return foundType_;
	}

}
