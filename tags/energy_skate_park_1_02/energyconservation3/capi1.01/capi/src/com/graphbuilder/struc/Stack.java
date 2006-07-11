package com.graphbuilder.struc;

public class Stack extends LinkedList {

	public Stack() {}

	public Object peek() {
		if (head == null)
			return null;

		return head.userObject;
	}

	public Object pop() {
		return removeHead();
	}

	public void push(Object o) {
		addToHead(o);
	}
}