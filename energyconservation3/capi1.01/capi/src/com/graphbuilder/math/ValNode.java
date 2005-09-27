package com.graphbuilder.math;

/**
A node of an expression tree that represents a value.  A ValNode cannot have any children.
*/
public class ValNode extends Expression {

	protected double val = 0.0;

	public ValNode(double d) {
		val = d;
	}

	/**
	Returns the value.
	*/
	public double eval(VarMap v, FuncMap f) {
		return val;
	}

	public double getValue() {
		return val;
	}

	public void setValue(double d) {
		val = d;
	}
}
