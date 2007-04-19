package net.sourceforge.retroweaver.runtime.java.lang.reflect;

import net.sourceforge.retroweaver.runtime.java.lang.TypeNotPresentException;

public interface WildcardType extends Type {

	Type[] getLowerBounds() throws TypeNotPresentException, MalformedParameterizedTypeException;

	Type[] getUpperBounds() throws TypeNotPresentException, MalformedParameterizedTypeException;

}
