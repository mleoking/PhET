package com.graphbuilder.math;

/**
A node of an expression tree, represented by the symbol "-".
*/
public class SubNode extends OpNode {

	public SubNode(Expression leftChild, Expression rightChild) {
		super(leftChild, rightChild);
	}

	/**
	Subtracts the evaluation of the right side from the evaluation of the left side and returns the result.
	*/
	public double eval(VarMap v, FuncMap f) {
		double a = leftChild.eval(v, f);
		double b = rightChild.eval(v, f);
		return a - b;
	}

	public String getSymbol() {
		return "-";
	}
}
