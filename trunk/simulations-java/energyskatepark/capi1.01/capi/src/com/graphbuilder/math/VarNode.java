package com.graphbuilder.math;

/**
A node of an expression tree that represents a variable.  A VarNode cannot have any children.
*/
public class VarNode extends TermNode {

	public VarNode(String name, boolean negate) {
		super(name, negate);
	}

	/**
	Returns the value associated with the variable name in the VarMap.
	*/
	public double eval(VarMap v, FuncMap f) {
		double val = v.getValue(name);

		if (negate) val = -val;

		return val;
	}
}
