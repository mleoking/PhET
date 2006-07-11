package com.graphbuilder.math;

/**
A node of an expression tree, represented by the symbol "+".
*/
public class AddNode extends OpNode {

	public AddNode(Expression leftChild, Expression rightChild) {
		super(leftChild, rightChild);
	}

	/**
	Adds the evaluation of the left side to the evaluation of the right side and returns the result.
	*/
	public double eval(VarMap v, FuncMap f) {
		double a = leftChild.eval(v, f);
		double b = rightChild.eval(v, f);
		return a + b;
	}

	public String getSymbol() {
		return "+";
	}
}
