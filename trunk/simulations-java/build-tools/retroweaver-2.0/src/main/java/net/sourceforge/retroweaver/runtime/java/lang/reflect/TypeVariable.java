package net.sourceforge.retroweaver.runtime.java.lang.reflect;

import net.sourceforge.retroweaver.runtime.java.lang.TypeNotPresentException;

public interface TypeVariable<D extends GenericDeclaration> extends Type {

	Type[] getBounds() throws TypeNotPresentException, MalformedParameterizedTypeException;

	D getGenericDeclaration();

	String getName() ;

}
