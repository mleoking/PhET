package com.graphbuilder.math;

/**
A node of an expression tree that has exactly 2 children, a left child and a right child.  After the
children are evaluated, a mathematical operation is applied and the result is returned.
*/
public abstract class OpNode extends Expression {

	protected Expression leftChild = null;
	protected Expression rightChild = null;

	public OpNode(Expression leftChild, Expression rightChild) {
		setLeftChild(leftChild);
		setRightChild(rightChild);
	}

	public void setLeftChild(Expression x) {
		checkBeforeAccept(x);
		if (leftChild != null)
			leftChild.parent = null;
		x.parent = this;
		leftChild = x;
	}

	public void setRightChild(Expression x) {
		checkBeforeAccept(x);
		if (rightChild != null)
			rightChild.parent = null;
		x.parent = this;
		rightChild = x;
	}

	public Expression getLeftChild() {
		return leftChild;
	}

	public Expression getRightChild() {
		return rightChild;
	}

	/**
	Returns the text symbol that represents the operation.
	*/
	public abstract String getSymbol();
}
