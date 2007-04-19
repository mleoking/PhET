package net.sourceforge.retroweaver.runtime.java.lang.reflect;

public interface GenericDeclaration {

	public TypeVariable<?>[] getTypeParameters() throws GenericSignatureFormatError;

}
