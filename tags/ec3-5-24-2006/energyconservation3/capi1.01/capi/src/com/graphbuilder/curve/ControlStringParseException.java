package com.graphbuilder.curve;

import com.graphbuilder.math.*;

/**
<p>Exception thrown if the parsing of a control-string fails.  A ControlStringParseException
contains a description, and may contain any of the following information:

<ul>
<li>An index or index range of the substring that caused the problem.</li>
<li>An ExpressionParseException that was thrown by the ExpressionTree.parse(String) method.</li>
</ul>

<p>Otherwise the index values will be -1 if they are unassigned and the ExpressionParseException
will be null.

@see GroupIterator
*/
public class ControlStringParseException extends RuntimeException {

	private String descrip = null;
	private int fromIndex = -1;
	private int toIndex = -1;
	private ExpressionParseException epe = null;

	/**
	Constructor with only a description.
	*/
	public ControlStringParseException(String descrip) {
		this.descrip = descrip;
	}

	/**
	Constructor with a description and index value.  The index value is assigned to both the
	fromIndex and the toIndex.
	*/
	public ControlStringParseException(String descrip, int index) {
		this.descrip = descrip;
		fromIndex = index;
		toIndex = index;
	}

	/**
	Constructor with a description and index range.
	*/
	public ControlStringParseException(String descrip, int fromIndex, int toIndex) {
		this.descrip = descrip;
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
	}

	/**
	Constructor with a description, index range and ExpressionParseException.
	*/
	public ControlStringParseException(String descrip, int fromIndex, int toIndex, ExpressionParseException epe) {
		this.descrip = descrip;
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
		this.epe = epe;
	}

	/**
	Returns the index location in the control-string that marks the start of the problem or -1 if
	not available.
	*/
	public int getFromIndex() {
		return fromIndex;
	}

	/**
	Returns the index location in the control-string that marks the end of the problem or -1 if not
	available.
	*/
	public int getToIndex() {
		return toIndex;
	}

	/**
	Returns the description of the problem.
	*/
	public String getDescription() {
		return descrip;
	}

	/**
	Returns an ExpressionParseException if an expression in the control-string could not be
	parsed correctly or null if this was not the problem.
	*/
	public ExpressionParseException getExpressionParseException() {
		return epe;
	}

	/**
	Returns a nicely formatted string of this exception.
	*/
	public String toString() {
		String e = "";
		if (epe != null)
			e = "\n" + epe.toString();

		if (fromIndex == -1 && toIndex == -1)
			return descrip + e;

		if (fromIndex == toIndex)
			return descrip + " : [" + toIndex + "]" + e;

		return descrip + " : [" + fromIndex + ", " + toIndex + "]" + e;
	}
}