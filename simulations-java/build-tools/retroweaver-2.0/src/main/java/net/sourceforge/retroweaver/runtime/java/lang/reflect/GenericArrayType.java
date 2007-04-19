package net.sourceforge.retroweaver.runtime.java.lang.reflect;

import net.sourceforge.retroweaver.runtime.java.lang.TypeNotPresentException;

public interface GenericArrayType extends Type {

	Type getGenericComponentType() throws TypeNotPresentException, MalformedParameterizedTypeException;

}
