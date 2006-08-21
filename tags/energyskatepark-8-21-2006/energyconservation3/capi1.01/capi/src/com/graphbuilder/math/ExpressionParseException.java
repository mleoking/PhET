package com.graphbuilder.math;

/**
Exception thrown if expression cannot be parsed correctly.

@see com.graphbuilder.math.ExpressionTree
*/
public class ExpressionParseException extends RuntimeException {

	private String descrip = null;
	private int index = 0;

	public ExpressionParseException(String descrip, int index) {
		this.descrip = descrip;
		this.index = index;
	}

	/**
	Returns the description that explains why the exception was thrown.
	*/
	public String getDescription() {
		return descrip;
	}

	/**
	Returns an index value into the expression string.  Note, the index value
	may be less than zero or greater then the length of the expression string.
	*/
	public int getIndex() {
		return index;
	}

	/**
	Returns a string formatted such as "(index) description".
	*/
	public String toString() {
		return "(" + index + ") " + descrip;
	}
}