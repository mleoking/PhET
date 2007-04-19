package net.sourceforge.retroweaver.runtime.java.lang;

public class TypeNotPresentException extends RuntimeException {

	private String typeName;

	public TypeNotPresentException(String typeName, Throwable cause) {
		super("type: " + typeName, cause);
		
		this.typeName = typeName;
	}

	public String typeName() {
		return typeName;
	}

}
