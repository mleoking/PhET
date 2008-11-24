/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.util.List;
import org.aswing.util.ListNode;

/**
 * Linked list implementation of the List interface.
 * @author Tomato
 * @author iiley
 */
class org.aswing.util.LinkedList implements List{
	
	/**
	 * the head node in this list	 */
	private var head:ListNode;
	/**
	 * the tail node in this list	 */
	private var tail:ListNode;
	/**
	 * the current node in this list	 */
	private var current:ListNode;
	
	/**
	 * the number of nodes in this list	 */
	private var count:Number;
	 
	 
	//constructor
	public function LinkedList(){
	 	count = 0;
	 	head = null;
	 	tail = null;
	}
	
	
	public function size():Number{
		return count;
	}
	
	public function getHead():ListNode{
		return head;
	}
	
	public function getTail():ListNode{
		return tail;	
	}
	
	/**
	 * @throws Error when index out of bounds
	 */
	public function append(data:Object, index:Number):Void{
		if(index == undefined) index = size();
		if(size() == 0 && index == 0){
			setInitalFirstNode(data);
			return;
		}
		
		if(index < 0 || index > size()){
			trace("/e/Error index out of range : " + index + ", size:" + size());
			throw new Error("Error index out of range : " + index + ", size:" + size());
		}
		
		if(index == size()){
			var newNode:ListNode = new ListNode(data , tail , null);
			tail.setNextNode(newNode);
			tail = newNode;
		}else if(index == 0){
			var newNode:ListNode = new ListNode(data , null , head);
			head.setPrevNode(newNode);
			head = newNode;
		}else{
			var preNode:ListNode = getNodeAt(index-1);
			var nexNode:ListNode = preNode.getNextNode();
			var newNode:ListNode = new ListNode(data , preNode , nexNode);
			preNode.setNextNode(newNode);
			nexNode.setPrevNode(newNode);
		}
		count += 1;
	}
	
	private function setInitalFirstNode(data:Object):Void{
		var newNode:ListNode = new ListNode(data , null , null);
		head = newNode;
		tail = newNode;
		count = 1;
	}
	
	public function getNodeAt(index:Number):ListNode{
		if(index < 0 || index >= size()){
			return null;
		}
		if(index < size()/2){
			var n:Number = index;
			var node:ListNode = getHead();
			for(var i:Number=0; i<n; i++){
				node = node.getNextNode();
			}
			return node;
		}else{
			var n:Number = size() - index - 1;
			var node:ListNode = getTail();
			for(var i:Number=0; i<n; i++){
				node = node.getPrevNode();
			}
			return node;
		}
	}
	
	
	public function get(index : Number) {
		return this.getNodeAt(index).getData();
	}

	/**
	 * @throws Error when index out of bounds
	 */
	public function appendAll(arr : Array, index : Number) : Void {
		if(index == undefined) index = size();
		if(arr == 0 || arr.length == 0){
			return;
		}
		if(index < 0 || index > size()){
			trace("/e/Error index out of range : " + index + ", size:" + size());
			throw new Error("Error index out of range : " + index + ", size:" + size());
		}
		
		var tempList:LinkedList = new LinkedList();
		for(var i:Number=0; i<arr.length; i++){
			tempList.append(arr[i]);
		}
		
		if(size() == 0){
			head = tempList.getHead();
			tail = tempList.getTail();
		}else if(index == size()){
			tail.setNextNode(tempList.getHead());
			tempList.getHead().setPrevNode(tail);
			tail = tempList.getTail();
		}else if(index == 0){
			head.setPrevNode(tempList.getTail());
			tempList.getTail().setNextNode(head);
			head = tempList.getHead();
		}else{
			var preNode:ListNode = getNodeAt(index-1);
			var nexNode:ListNode = preNode.getNextNode();
			preNode.setNextNode(tempList.getHead());
			tempList.getHead().setPrevNode(preNode);
			tempList.getTail().setNextNode(nexNode);
			nexNode.setPrevNode(tempList.getTail());
		}
		count += tempList.size();
	}

	public function appendList(list : List, index : Number) : Void {
		appendAll(list.toArray(), index);
	}

	public function replaceAt(index : Number, element) {
		var node:ListNode = getNodeAt(index);
		var oldData = node.getData();
		node.setData(element);
		return oldData;
	}

	public function removeAt(index : Number) {
		if(index < 0 || index >= size()){
			return undefined;
		}
		var element;
		if(index == 0){
			element = head.getData();
			head = head.getNextNode();
			head.setPrevNode(null);
		}else if(index == size() - 1){
			element = tail.getData();
			tail = tail.getPrevNode();
			tail.setNextNode(null);
		}else{
			var preNode:ListNode = getNodeAt(index-1);
			var nexNode:ListNode = preNode.getNextNode().getNextNode();
			element = preNode.getNextNode().getData();
			preNode.setNextNode(nexNode);
			nexNode.setPrevNode(preNode);
		}
		count --;
		return element;
	}

	public function remove(element) {
		for(var node:ListNode = head; node!=null; node=node.getNextNode()){
			if(node.getData() == element){
				if(node == head){
					head = head.getNextNode();
					head.setPrevNode(null);
				}else if(node == tail){
					tail = tail.getPrevNode();
					tail.setNextNode(null);
				}else{
					var preNode:ListNode = node.getPrevNode();
					var nexNode:ListNode = node.getNextNode();
					preNode.setNextNode(nexNode);
					nexNode.setPrevNode(preNode);
				}
				count --;
				return node.getData();
			}
		}
		return undefined;
	}
	
	/**
	 * Returns undefined if out of bounds
	 */
	public function removeRange(fromIndex : Number, toIndex : Number) : Array {
		if(fromIndex > toIndex){
			var temp:Number = fromIndex;
			fromIndex = toIndex;
			toIndex = temp; 
		}
		if(fromIndex < 0 || fromIndex >= size()){
			return undefined;
		}
		if(toIndex < 0 || toIndex >= size()){
			return undefined;
		}
		var preNode:ListNode = getNodeAt(fromIndex - 1);
		var nexNode:ListNode = getNodeAt(toIndex + 1);
		if(fromIndex == 0 && toIndex == size() - 1){
			var arr:Array = toArray();
			clear();
			return arr;
		}
		
		var startNode:ListNode = preNode.getNextNode();
		var endNode:ListNode = nexNode.getPrevNode();
		if(preNode == null){
			startNode = head;
		}
		if(nexNode == null){
			endNode = tail;
		}
		var al:Number = toIndex-fromIndex+1;
		var arr:Array = new Array(al);
		for(var i:Number=0; i<al; i++){
			arr[i] = startNode;
			startNode = startNode.getNextNode();
		}
		
		if(fromIndex == 0){
			head = nexNode;
			head.setPrevNode(null);
		}else if(toIndex == size() - 1){
			tail = preNode;
			tail.setNextNode(null);
		}else{
			preNode.setNextNode(nexNode);
			nexNode.setPrevNode(preNode);
		}
		
		count -= al;
		return arr;
	}

	public function indexOf(element) : Number {
		var index:Number = 0;
		for(var node:ListNode = head; node!=null; node=node.getNextNode()){
			if(node.getData() == element){
				return index;
			}
			index++;
		}
		return -1;
	}

	public function contains(element) : Boolean {
		return (indexOf(element) != -1);
	}

	public function first() {
		return head.getData();
	}

	public function last() {
		return tail.getData();
	}

	public function pop() {
		return removeAt(size() - 1);
	}
	
	public function shift() {
		return removeAt(0);
	}

	public function clear() : Void {
		head = tail = null;
		count = 0;
	}

	public function isEmpty() : Boolean {
		return count > 0;
	}

	public function toArray() : Array {
		var arr:Array = new Array(size());
		var index:Number = 0;
		for(var node:ListNode = head; node!=null; node=node.getNextNode()){
			arr[index] = node.getData();
			index++;
		}
		return arr;
	}
	
	public function toString():String{
		return "LinkedList[" + toArray() + "]";
	}
}