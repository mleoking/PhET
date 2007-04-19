package net.sourceforge.retroweaver.runtime.java.lang.reflect;

import net.sourceforge.retroweaver.runtime.java.lang.TypeNotPresentException;

public interface ParameterizedType extends Type {

	Type[] getActualTypeArguments() throws TypeNotPresentException, MalformedParameterizedTypeException;

	Type getOwnerType();

	Type getRawType();

}
