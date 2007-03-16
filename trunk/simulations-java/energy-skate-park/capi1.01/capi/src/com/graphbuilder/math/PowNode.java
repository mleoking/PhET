package com.graphbuilder.math;

/**
A node of an expression tree, represented by the symbol "^".
*/
public class PowNode extends OpNode {

	public PowNode(Expression leftChild, Expression rightChild) {
		super(leftChild, rightChild);
	}

	/**
	Raises the evaluation of the left side to the power of the evaluation of the right side and returns the result.
	*/
	public double eval(VarMap v, FuncMap f) {
		double a = leftChild.eval(v, f);
		double b = rightChild.eval(v, f);
		return Math.pow(a, b);
	}

	public String getSymbol() {
		return "^";
	}
}
