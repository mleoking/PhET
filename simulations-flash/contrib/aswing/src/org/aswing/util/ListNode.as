﻿/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * List Node.
 */
class org.aswing.util.ListNode{
	
	/**
	 * the data stored in this node
	 */
	private var data;
	/**
	 * the node directly behind this node in a list
	 */
	private var nextNode:ListNode;
	/**
	 * the node directly before this node in a list
	 */
	private var preNode:ListNode;
	
	public function ListNode(_data , _preNode:ListNode , _nextNode:ListNode){
		this.data = _data;
		this.nextNode = _nextNode;
		this.preNode = _preNode;
	}
	
	//setter and getter methiods
	public function setData(_data):Void{
		this.data = _data;
	}
	
	public function getData(){
		return this.data;
	}
	
	public function setPrevNode(_preNode:ListNode):Void{
		this.preNode = _preNode;
	}
	
	public function getPrevNode():ListNode{
		return this.preNode;
	}
	
	public function setNextNode(_nextNode:ListNode):Void{
		this.nextNode = _nextNode;
	}
	
	public function getNextNode():ListNode{
		return this.nextNode;
	}
	
	public function toString():String{
		return "ListNode[data:" + data + "]";
	}
		
}
	
