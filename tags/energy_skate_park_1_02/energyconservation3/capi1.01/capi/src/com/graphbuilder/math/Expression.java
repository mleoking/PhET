package com.graphbuilder.math;

import com.graphbuilder.struc.Bag;

/**
The class from which all nodes of an expression tree are descendents.  Expressions can be evaluated
using the eval method.  Expressions that are or have FuncNodes or VarNodes as descendents must provide
a VarMap or FuncMap respectively.  Expressions that consist entirely of OpNodes and ValNodes do not
require a VarMap or FuncMap.  For Expressions that support children (OpNodes, FuncNodes), a child can
only be accepted provided it currently has no parent, a cyclic reference is not formed, and it is
non-null.
*/
public abstract class Expression {

	protected Expression parent = null;

	/**
	Returns the result of evaluating the expression tree rooted at this node.
	*/
	public abstract double eval(VarMap v, FuncMap f);

	/**
	Returns true if this node is a descendent of the specified node, false otherwise.  By this
	methods definition, a node is a descendent of itself.
	*/
	public boolean isDescendent(Expression x) {
		Expression y = this;

		while (y != null) {
			if (y == x)
				return true;
			y = y.parent;
		}

		return false;
	}

	/**
	Returns the parent of this node.  Returns null if this node is the root node of an expression-tree.
	*/
	public Expression getParent() {
		return parent;
	}

	/**
	Protected method used to verify that the specified expression can be included as a child
	expression of this node.  An expression cannot be included as a child if it is null, it
	currently has a parent, or a cyclic reference would be formed.

	@throws IllegalArgumentException If the specified expression is not accepted.
	*/
	protected void checkBeforeAccept(Expression x) {
		if (x == null)
			throw new IllegalArgumentException("expression cannot be null");

		if (x.parent != null)
			throw new IllegalArgumentException("expression must be removed parent");

		if (isDescendent(x))
			throw new IllegalArgumentException("cyclic reference");
	}

	/**
	Returns an array of exact length of the variable names contained in the expression tree rooted at this node.
	*/
	public String[] getVariableNames() {
		return getTermNames(true);
	}

	/**
	Returns an array of exact length of the function names contained in the expression tree rooted at this node.
	*/
	public String[] getFunctionNames() {
		return getTermNames(false);
	}

	private String[] getTermNames(boolean varNames) {
		Bag b = new Bag();
		getTermNames(this, b, varNames);
		String[] arr = new String[b.size()];
		for (int i = 0; i < arr.length; i++)
			arr[i] = (String) b.get(i);
		return arr;
	}

	private static void getTermNames(Expression x, Bag b, boolean varNames) {
		if (x instanceof OpNode) {
			OpNode o = (OpNode) x;
			getTermNames(o.leftChild, b, varNames);
			getTermNames(o.rightChild, b, varNames);
		}
		else if (x instanceof VarNode) {
			if (varNames) {
				VarNode v = (VarNode) x;
				if (!b.contains(v.name))
					b.add(v.name);
			}
		}
		else if (x instanceof FuncNode) {
			FuncNode f = (FuncNode) x;

			if (!varNames) {
				if (!b.contains(f.name))
					b.add(f.name);
			}

			for (int i = 0; i < f.numChildren(); i++)
				getTermNames(f.child(i), b, varNames);
		}
	}

	/**
	Returns a string that represents the expression tree rooted at this node.
	*/
	public String toString() {
		StringBuffer sb = new StringBuffer();
		toString(this, sb);
		return sb.toString();
	}

	private static void toString(Expression x, StringBuffer sb) {
		if (x instanceof OpNode) {
			OpNode o = (OpNode) x;
			sb.append("(");
			toString(o.leftChild, sb);
			sb.append(o.getSymbol());
			toString(o.rightChild, sb);
			sb.append(")");
		}
		else if (x instanceof TermNode) {
			TermNode t = (TermNode) x;

			if (t.getNegate()) {
				sb.append("(");
				sb.append("-");
			}

			sb.append(t.getName());

			if (t instanceof FuncNode) {
				FuncNode f = (FuncNode) t;

				sb.append("(");

				if (f.numChildren() > 0)
					toString(f.child(0), sb);

				for (int i = 1; i < f.numChildren(); i++) {
					sb.append(", ");
					toString(f.child(i), sb);
				}

				sb.append(")");
			}

			if (t.getNegate())
				sb.append(")");
		}
		else if (x instanceof ValNode) {
			sb.append(((ValNode) x).val);
		}
	}
}
