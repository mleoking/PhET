package com.graphbuilder.struc;

public class LinkedList {

	protected Node head = null;
	protected Node tail = null;
	protected int size = 0;

	public LinkedList() {}

	public static class Node {
		protected LinkedList list = null;
		protected Node next = null;
		protected Node prev = null;
		protected Object userObject = null;

		protected Node(LinkedList list, Object userObject) {
			this.list = list;
			this.userObject = userObject;
		}

		public LinkedList list() {
			return list;
		}

		public Node next() {
			return next;
		}

		public Node prev() {
			return prev;
		}

		public Object getUserObject() {
			return userObject;
		}

		public void setUserObject(Object userObject) {
			this.userObject = userObject;
		}

		public void insertBefore(Object o) {
			list.insertBefore(this, o);
		}

		public void insertAfter(Object o) {
			list.insertAfter(this, o);
		}

		public void remove() {
			list.removeNode(this);
		}
	}

	protected Node createNode(Object o) {
		return new Node(this, o);
	}

	protected void insertBefore(Node n, Object o) {
		Node p = createNode(o);

		if (size == 0) {
			head = p;
			tail = p;
		}
		else {
			if (n == head) {
				p.next = head;
				head.prev = p;
				head = p;
			}
			else {
				n.prev.next = p;
				p.prev = n.prev;
				n.prev = p;
				p.next = n;
			}
		}

		size++;
	}

	protected void insertAfter(Node n, Object o) {
		Node p = createNode(o);

		if (size == 0) {
			head = p;
			tail = p;
		}
		else {
			if (n == tail) {
				p.prev = tail;
				tail.next = p;
				tail = p;
			}
			else {
				n.next.prev = p;
				p.next = n.next;
				n.next = p;
				p.prev = n;
			}
		}

		size++;
	}

	protected Object removeNode(Node n) {
		if (size == 0)
			return null;

		Object o = n.userObject;

		if (n == head) {
			head = head.next;

			if (head == null)
				tail = null;
			else
				head.prev = null;
		}
		else if (n == tail) {
			tail = tail.prev;
			tail.next = null;
		}
		else {
			n.prev.next = n.next;
			n.next.prev = n.prev;
		}

		n.list = null;
		size--;
		return o;
	}

	public Node getHead() {
		return head;
	}

	public Node getTail() {
		return tail;
	}

	public void addToHead(Object o) {
		insertBefore(head, o);
	}

	public void addToTail(Object o) {
		insertAfter(tail, o);
	}

	public Object removeHead() {
		return removeNode(head);
	}

	public Object removeTail() {
		return removeNode(tail);
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(6 * size);
		sb.append("[");
		Node n = head;

		if (n != null) {
			sb.append(n.userObject);
			n = n.next;
		}

		while (n != null) {
			sb.append(", ");
			sb.append(n.userObject);
			n = n.next;
		}

		return sb.append("]").toString();
	}
}