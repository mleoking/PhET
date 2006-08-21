package com.graphbuilder.math;

/**
A node of an expression tree, represented by the symbol "*".
*/
public class MultNode extends OpNode {

	public MultNode(Expression leftChild, Expression rightChild) {
		super(leftChild, rightChild);
	}

	/**
	Multiples the evaluation of the left side and the evaluation of the right side and returns the result.
	*/
	public double eval(VarMap v, FuncMap f) {
		double a = leftChild.eval(v, f);
		double b = rightChild.eval(v, f);
		return a * b;
	}

	public String getSymbol() {
		return "*";
	}
}
