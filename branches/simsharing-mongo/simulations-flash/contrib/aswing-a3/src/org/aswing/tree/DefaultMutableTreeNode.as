/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.tree { 

import org.aswing.tree.MutableTreeNode;
import org.aswing.tree.TreeNode;
import org.aswing.util.ArrayList;

/**
 * A <code>DefaultMutableTreeNode</code> is a general-purpose node in a tree data
 * structure. 
 * <p>
 * A tree node may have at most one parent and 0 or more children.
 * <code>DefaultMutableTreeNode</code> provides operations for examining and modifying a
 * node's parent and children and also operations for examining the tree that
 * the node is a part of.  A node's tree is the set of all nodes that can be
 * reached by starting at the node and following all the possible links to
 * parents and children.  A node with no parent is the root of its tree; a
 * node with no children is a leaf.  A tree may consist of many subtrees,
 * each node acting as the root for its own subtree.
 * <p>
 * This class provides enumerations for efficiently traversing a tree or
 * subtree in various orders or for following the path between two nodes.
 * A <code>DefaultMutableTreeNode</code> may also hold a reference to a user object, the
 * use of which is left to the user.  Asking a <code>DefaultMutableTreeNode</code> for its
 * string representation with <code>toString()</code> returns the string
 * representation of its user object.
 * <p>
 * While DefaultMutableTreeNode implements the MutableTreeNode interface and
 * will allow you to add in any implementation of MutableTreeNode not all
 * of the methods in DefaultMutableTreeNode will be applicable to all
 * MutableTreeNodes implementations. Especially with some of the enumerations
 * that are provided, using some of these methods assumes the
 * DefaultMutableTreeNode contains only DefaultMutableNode instances. All
 * of the TreeNode/MutableTreeNode methods will behave as defined no
 * matter what implementations are added.
 * 
 * @author iiley
 */
public class DefaultMutableTreeNode implements MutableTreeNode{

    /** this node's parent, or null if this node has no parent */
    protected var parent:MutableTreeNode;

    /** array of children, may be null if this node has no children */
    private var _children:ArrayList;

    /** optional user object */
    private var userObject:*;

    /** true if the node is able to have children */
    private var allowsChildren:Boolean;

    /**
     * Creates a tree node with no parent, no children, initialized with
     * the specified user object, and that allows children only if
     * specified.
     * 
     * @param userObject an Object provided by the user that constitutes
     *        the node's data
     * @param allowsChildren (optional)if true, the node is allowed to have child
     *        nodes -- otherwise, it is always a leaf node. Default is true.
     */
    public function DefaultMutableTreeNode(userObject:Object, allowsChildren:Boolean=true) {
		parent = null;
		this.allowsChildren = allowsChildren;
		this.userObject = userObject;
    }


    //
    //  Primitives
    //

    /**
     * Removes <code>newChild</code> from its present parent (if it has a
     * parent), sets the child's parent to this node, and then adds the child
     * to this node's child array at index <code>childIndex</code>.
     * <code>newChild</code> must not be null and must not be an ancestor of
     * this node.
     *
     * @param	newChild	the MutableTreeNode to insert under this node
     * @param	childIndex	the index in this node's child array
     *				where this node is to be inserted
     * @exception	Error	if
     *				<code>newChild</code> is null or is an ancestor of this node, 
     *				or if <code>childIndex</code> is out of bounds, or if this 
     *				node does not allow children
     * @see	#isNodeDescendant
     */
    public function insert(newChild:MutableTreeNode, childIndex:int):void {
		if (!allowsChildren) {
			trace("Error : node does not allow children");
		    throw new Error("node does not allow children");
		} else if (newChild == null) {
			trace("Error : new child is null");
		    throw new Error("new child is null");
		} else if (isNodeAncestor(newChild)) {
			trace("Error : new child is an ancestor");
		    throw new Error("new child is an ancestor");
		}

	    var oldParent:MutableTreeNode = MutableTreeNode(newChild.getParent());

	    if (oldParent != null) {
			oldParent.remove(newChild);
	    }
	    newChild.setParent(this);
	    if (_children == null) {
			_children = new ArrayList();
	    }
	    _children.append(newChild, childIndex);
    }

    /**
     * Removes the child at the specified index from this node's children
     * and sets that node's parent to null. The child node to remove
     * must be a <code>MutableTreeNode</code>.
     *
     * @param	childIndex	the index in this node's child array
     *				of the child to remove, nothing happen if
     *				<code>childIndex</code> is out of bounds
     */
    public function removeAt(childIndex:int):void {
		var child:MutableTreeNode = MutableTreeNode(getChildAt(childIndex));
		if(child != null){
			_children.removeAt(childIndex);
			child.setParent(null);
		}
    }

    /**
     * Sets this node's parent to <code>newParent</code> but does not 
     * change the parent's child array.  This method is called from
     * <code>insert()</code> and <code>remove()</code> to
     * reassign a child's parent, it should not be messaged from anywhere
     * else.
     *
     * @param	newParent	this node's new parent
     */
    public function setParent(newParent:MutableTreeNode):void {
		parent = newParent;
    }

    /**
     * Returns this node's parent or null if this node has no parent.
     *
     * @return	this node's parent TreeNode, or null if this node has no parent
     */
    public function getParent():TreeNode {
		return parent;
    }

    /**
     * Returns the child at the specified index in this node's child array.
     *
     * @param	index	an index into this node's child array
     * @exception	ArrayIndexOutOfBoundsException	if <code>index</code>
     *						is out of bounds
     * @return	the TreeNode in this node's child array at  the specified index
     */
    public function getChildAt(index:int):TreeNode {
		if (_children == null) {
			trace("Error : node has no children");
		    throw new Error("node has no children");
		}
		return TreeNode(_children.get(index));
    }

    /**
     * Returns the number of children of this node.
     *
     * @return	an int giving the number of children of this node
     */
    public function getChildCount():int {
		if (_children == null) {
		    return 0;
		} else {
		    return _children.size();
		}
    }

    /**
     * Returns the index of the specified child in this node's child array.
     * If the specified node is not a child of this node, returns
     * <code>-1</code>.  This method performs a linear search and is O(n)
     * where n is the number of children.
     *
     * @param	aChild	the TreeNode to search for among this node's children
     * @return	an int giving the index of the node in this node's child 
     *          array, or <code>-1</code> if the specified node is a not
     *          a child of this node
     */
    public function getIndex(aChild:TreeNode):int {
		if (!isNodeChild(aChild)) {
		    return -1;
		}
		return _children.indexOf(aChild);	// linear search
	}

    /**
     * Creates and returns a forward-order enumeration of this node's
     * children.  Modifying this node's child array invalidates any child
     * enumerations created before the modification.
     *
     * @return	an Enumeration of this node's children
     */
    public function children():Array {
		if (_children == null) {
		    return [];
		} else {
		    return _children.toArray();
		}
    }

    /**
     * Determines whether or not this node is allowed to have children. 
     * If <code>allows</code> is false, all of this node's children are
     * removed.
     * <p>
     * Note: By default, a node allows children.
     *
     * @param	allows	true if this node is allowed to have children
     */
    public function setAllowsChildren(allows:Boolean):void {
		if (allows != allowsChildren) {
		    allowsChildren = allows;
		    if (!allowsChildren) {
				removeAllChildren();
		    }
		}
    }

    /**
     * Returns true if this node is allowed to have children.
     *
     * @return	true if this node allows children, else false
     */
    public function getAllowsChildren():Boolean {
		return allowsChildren;
    }

    /**
     * Sets the user object for this node to <code>userObject</code>.
     *
     * @param	userObject	the Object that constitutes this node's 
     *                          user-specified data
     * @see	#getUserObject
     * @see	#toString
     */
    public function setUserObject(userObject:*):void {
		this.userObject = userObject;
    }

    /**
     * Returns this node's user object.
     *
     * @return	the Object stored at this node by the user
     * @see	#setUserObject
     * @see	#toString
     */
    public function getUserObject():* {
		return userObject;
    }


    //
    //  Derived methods
    //

    /**
     * Removes the subtree rooted at this node from the tree, giving this
     * node a null parent.  Does nothing if this node is the root of its
     * tree.
     */
    public function removeFromParent():void {
		var parent:MutableTreeNode = MutableTreeNode(getParent());
		if (parent != null) {
		    parent.remove(this);
		}
    }

    /**
     * Removes <code>aChild</code> from this node's child array, giving it a
     * null parent.
     *
     * @param	aChild	a child of this node to remove
     */
    public function remove(aChild:MutableTreeNode):void {
		if (!isNodeChild(aChild)) {
		    trace("argument is not a child");
		    return;
		}
		removeAt(getIndex(aChild));	// linear search
    }

    /**
     * Removes all of this node's children, setting their parents to null.
     * If this node has no children, this method does nothing.
     */
    public function removeAllChildren():void {
		for (var i:int = getChildCount()-1; i >= 0; i--) {
		    removeAt(i);
		}
    }

    /**
     * Removes <code>newChild</code> from its parent and makes it a child of
     * this node by adding it to the end of this node's child array.
     *
     * @see		#insert()
     * @param	newChild	node to add as a child of this node
     */
    public function append(newChild:MutableTreeNode):void {
		if(newChild != null && newChild.getParent() == this){
		    insert(newChild, getChildCount() - 1);
		}else{
		    insert(newChild, getChildCount());
		}
    }



    //
    //  Tree Queries
    //

    /**
     * Returns true if <code>anotherNode</code> is an ancestor of this node
     * -- if it is this node, this node's parent, or an ancestor of this
     * node's parent.  (Note that a node is considered an ancestor of itself.)
     * If <code>anotherNode</code> is null, this method returns false.  This
     * operation is at worst O(h) where h is the distance from the root to
     * this node.
     *
     * @see		#isNodeDescendant()
     * @see		#getSharedAncestor()
     * @param	anotherNode	node to test as an ancestor of this node
     * @return	true if this node is a descendant of <code>anotherNode</code>
     */
    public function isNodeAncestor(anotherNode:TreeNode):Boolean {
		if (anotherNode == null) {
		    return false;
		}
	
		var ancestor:TreeNode = this;
	
		do {
		    if (ancestor == anotherNode) {
				return true;
		    }
		} while((ancestor = ancestor.getParent()) != null);
	
		return false;
    }

    /**
     * Returns true if <code>anotherNode</code> is a descendant of this node
     * -- if it is this node, one of this node's children, or a descendant of
     * one of this node's children.  Note that a node is considered a
     * descendant of itself.  If <code>anotherNode</code> is null, returns
     * false.  This operation is at worst O(h) where h is the distance from the
     * root to <code>anotherNode</code>.
     *
     * @see	#isNodeAncestor
     * @see	#getSharedAncestor
     * @param	anotherNode	node to test as descendant of this node
     * @return	true if this node is an ancestor of <code>anotherNode</code>
     */
    public function isNodeDescendant(anotherNode:DefaultMutableTreeNode):Boolean {
		if (anotherNode == null)
		    return false;
	
		return anotherNode.isNodeAncestor(this);
    }

    /**
     * Returns the nearest common ancestor to this node and <code>aNode</code>.
     * Returns null, if no such ancestor exists -- if this node and
     * <code>aNode</code> are in different trees or if <code>aNode</code> is
     * null.  A node is considered an ancestor of itself.
     *
     * @see	#isNodeAncestor
     * @see	#isNodeDescendant
     * @param	aNode	node to find common ancestor with
     * @return	nearest ancestor common to this node and <code>aNode</code>,
     *		or null if none
     */
    public function getSharedAncestor(aNode:DefaultMutableTreeNode):TreeNode {
		if (aNode == this) {
		    return this;
		} else if (aNode == null) {
		    return null;
		}
	
		var	level1:int, level2:int, diff:int;
		var	node1:TreeNode, node2:TreeNode;
		
		level1 = getLevel();
		level2 = aNode.getLevel();
		
		if (level2 > level1) {
		    diff = level2 - level1;
		    node1 = aNode;
		    node2 = this;
		} else {
		    diff = level1 - level2;
		    node1 = this;
		    node2 = aNode;
		}
	
		// Go up the tree until the nodes are at the same level
		while (diff > 0) {
		    node1 = node1.getParent();
		    diff--;
		}
		
		// Move up the tree until we find a common ancestor.  Since we know
		// that both nodes are at the same level, we won't cross paths
		// unknowingly (if there is a common ancestor, both nodes hit it in
		// the same iteration).
		
		do {
		    if (node1 == node2) {
				return node1;
		    }
		    node1 = node1.getParent();
		    node2 = node2.getParent();
		} while (node1 != null);// only need to check one -- they're at the
		// same level so if one is null, the other is
		
		if (node1 != null || node2 != null) {
			trace("Error : nodes should be null");
		    throw new Error ("nodes should be null");
		}
		
		return null;
    }


    /**
     * Returns true if and only if <code>aNode</code> is in the same tree
     * as this node.  Returns false if <code>aNode</code> is null.
     *
     * @see	#getSharedAncestor
     * @see	#getRoot
     * @return	true if <code>aNode</code> is in the same tree as this node;
     *		false if <code>aNode</code> is null
     */
    public function isNodeRelated(aNode:DefaultMutableTreeNode):Boolean {
		return (aNode != null) && (getRoot() == aNode.getRoot());
    }


    /**
     * Returns the depth of the tree rooted at this node -- the longest
     * distance from this node to a leaf.  If this node has no children,
     * returns 0.  This operation is much more expensive than
     * <code>getLevel()</code> because it must effectively traverse the entire
     * tree rooted at this node.
     *
     * @see	#getLevel
     * @return	the depth of the tree whose root is this node
     */
    public function getDepth():int {
		var	last:Object = null;
		var	enum_:Array = breadthFirstEnumeration();
		
		last = enum_[enum_.length - 1];
		
		if (last == null) {
			trace("Error : nodes should be null");
		    throw new Error ("nodes should be null");
		}
		
		return (DefaultMutableTreeNode(last)).getLevel() - getLevel();
    }



    /**
     * Returns the number of levels above this node -- the distance from
     * the root to this node.  If this node is the root, returns 0.
     *
     * @see	#getDepth
     * @return	the number of levels above this node
     */
    public function getLevel():int {
		var ancestor:TreeNode;
		var levels:int = 0;
	
		ancestor = this;
		while((ancestor = ancestor.getParent()) != null){
		    levels++;
		}
	
		return levels;
    }


    /**
      * Returns the path from the root, to get to this node.  The last
      * element in the path is this node.
      *
      * @return an array of <code>TreeNode</code> objects giving the path, where the
      *         first element in the path is the root and the last
      *         element is this node.
      */
    public function getPath():Array {
		return getPathToRoot(this, 0);
    }

    /**
     * Builds the parents of node up to and including the root node,
     * where the original node is the last element in the returned array.
     * The length of the returned array gives the node's depth in the
     * tree.
     * 
     * @param aNode  the TreeNode to get the path for
     * @param depth  an int giving the number of steps already taken towards
     *        the root (on recursive calls), used to size the returned array
     * @return an array of TreeNodes giving the path from the root to the
     *         specified node 
     */
    private function getPathToRoot(aNode:TreeNode, depth:int):Array {
		var retNodes:Array;
	
		/* Check for null, in case someone passed in a null node, or
		   they passed in an element that isn't rooted at root. */
		if(aNode == null) {
		    if(depth == 0)
				return null;
		    else
				retNodes = new Array(depth);
		}else {
		    depth++;
		    retNodes = getPathToRoot(aNode.getParent(), depth);
		    retNodes[retNodes.length - depth] = aNode;
		}
		return retNodes;
    }

    /**
      * Returns the user object path, from the root, to get to this node.
      * If some of the TreeNodes in the path have null user objects, the
      * returned path will contain nulls.
      */
    public function getUserObjectPath():Array {
		var realPath:Array = getPath();
		var retPath:Array = new Array(realPath.length);
	
		for(var counter:int = 0; counter < realPath.length; counter++){
		    retPath[counter] = (DefaultMutableTreeNode(realPath[counter])).getUserObject();
		}
		return retPath;
    }

    /**
     * Returns the root of the tree that contains this node.  The root is
     * the ancestor with a null parent.
     *
     * @see	#isNodeAncestor
     * @return	the root of the tree that contains this node
     */
    public function getRoot():TreeNode {
		var ancestor:TreeNode = this;
		var previous:TreeNode;
	
		do {
		    previous = ancestor;
		    ancestor = ancestor.getParent();
		} while (ancestor != null);
	
		return previous;
    }


    /**
     * Returns true if this node is the root of the tree.  The root is
     * the only node in the tree with a null parent; every tree has exactly
     * one root.
     *
     * @return	true if this node is the root of its tree
     */
    public function isRoot():Boolean {
		return getParent() == null;
    }


    /**
     * Returns the node that follows this node in a preorder traversal of this
     * node's tree.  Returns null if this node is the last node of the
     * traversal.  This is an inefficient way to traverse the entire tree; use
     * an enumeration, instead.
     *
     * @see	#preorderEnumeration
     * @return	the node that follows this node in a preorder traversal, or
     *		null if this node is last
     */
    public function getNextNode():DefaultMutableTreeNode {
		if (getChildCount() == 0) {
		    // No children, so look for nextSibling
		    var nextSibling:DefaultMutableTreeNode = getNextSibling();
	
		    if (nextSibling == null) {
				var aNode:DefaultMutableTreeNode = DefaultMutableTreeNode(getParent());
				do {
				    if (aNode == null) {
						return null;
				    }
				    nextSibling = aNode.getNextSibling();
				    if (nextSibling != null) {
						return nextSibling;
				    }
				    aNode = DefaultMutableTreeNode(aNode.getParent());
				} while(true);
				return null;//just tell ide i'll return a value
		    } else {
				return nextSibling;
		    }
		} else {
		    return DefaultMutableTreeNode(getChildAt(0));
		}
    }


    /**
     * Returns the node that precedes this node in a preorder traversal of
     * this node's tree.  Returns <code>null</code> if this node is the
     * first node of the traversal -- the root of the tree. 
     * This is an inefficient way to
     * traverse the entire tree; use an enumeration, instead.
     *
     * @see	#preorderEnumeration
     * @return	the node that precedes this node in a preorder traversal, or
     *		null if this node is the first
     */
    public function getPreviousNode():DefaultMutableTreeNode {
		var previousSibling:DefaultMutableTreeNode;
		var myParent:DefaultMutableTreeNode = DefaultMutableTreeNode(getParent());
	
		if (myParent == null) {
		    return null;
		}
	
		previousSibling = getPreviousSibling();
	
		if (previousSibling != null) {
		    if (previousSibling.getChildCount() == 0)
				return previousSibling;
		    else
				return previousSibling.getLastLeaf();
		} else {
		    return myParent;
		}
    }

    /**
     * Creates and returns an enumeration that traverses the subtree rooted at
     * this node in preorder.  The first node returned by the enumeration's
     * <code>nextElement()</code> method is this node.<P>
     *
     * Modifying the tree by inserting, removing, or moving a node invalidates
     * any enumerations created before the modification.
     *
     * @see	#postorderEnumeration()
     * @return	an enumeration for traversing the tree in preorder
     */
    public function preorderEnumeration():Array {
		var arr:Array = new Array();
		fillPreorder(this, arr);
		return arr;
    }
    
    private function fillPreorder(node:TreeNode, arr:Array):void{
    	arr.push(node);
    	var cd:Array = node.children();
    	if(cd != null && cd.length > 0){
    		for(var i:int=0; i<cd.length; i++){
    			fillPreorder(cd[i], arr);
    		}
    	}
    }

    /**
     * Creates and returns an enumeration that traverses the subtree rooted at
     * this node in postorder.  The first node returned by the enumeration's
     * <code>nextElement()</code> method is the leftmost leaf.  This is the
     * same as a depth-first traversal.<P>
     *
     * Modifying the tree by inserting, removing, or moving a node invalidates
     * any enumerations created before the modification.
     *
     * @see	#depthFirstEnumeration()
     * @see	#preorderEnumeration()
     * @return	an enumeration for traversing the tree in postorder
     */
    public function postorderEnumeration():Array {
		var arr:Array = new Array();
		fillPostorder(this, arr);
		return arr;
    }
    
    private function fillPostorder(node:TreeNode, arr:Array):void{
    	var cd:Array = node.children();
    	if(cd != null && cd.length > 0){
    		for(var i:int=0; i<cd.length; i++){
    			fillPostorder(cd[i], arr);
    		}
    	}else{
    		arr.push(node);
    	}
    }

    /**
     * Creates and returns an enumeration that traverses the subtree rooted at
     * this node in breadth-first order.  The first node returned by the
     * enumeration's <code>nextElement()</code> method is this node.<P>
     *
     * Modifying the tree by inserting, removing, or moving a node invalidates
     * any enumerations created before the modification.
     *
     * @see	#depthFirstEnumeration()
     * @return	an enumeration for traversing the tree in breadth-first order
     */
    public function breadthFirstEnumeration():Array {
		var arr:Array = new Array();
		var queue:Array = new Array();
		queue.push(this);
		while(queue.length > 0){
			var node:TreeNode = TreeNode(queue.shift());
			arr.push(node);
	    	var cd:Array = node.children();
	    	if(cd != null && cd.length > 0){
	    		for(var i:int=0; i<cd.length; i++){
	    			queue.push(cd[i]);
	    		}
	    	}
		}
		return arr;
    }

    /**
     * Creates and returns an enumeration that traverses the subtree rooted at
     * this node in depth-first order.  The first node returned by the
     * enumeration's <code>nextElement()</code> method is the leftmost leaf.
     * This is the same as a postorder traversal.<P>
     *
     * Modifying the tree by inserting, removing, or moving a node invalidates
     * any enumerations created before the modification.
     *
     * @see	#breadthFirstEnumeration()
     * @see	#postorderEnumeration()
     * @return	an enumeration for traversing the tree in depth-first order
     */
    public function depthFirstEnumeration():Array {
		return postorderEnumeration();
    }

    /**
     * Creates and returns an enumeration that follows the path from
     * <code>ancestor</code> to this node.  The enumeration's
     * <code>nextElement()</code> method first returns <code>ancestor</code>,
     * then the child of <code>ancestor</code> that is an ancestor of this
     * node, and so on, and finally returns this node.  Creation of the
     * enumeration is O(m) where m is the number of nodes between this node
     * and <code>ancestor</code>, inclusive.  Each <code>nextElement()</code>
     * message is O(1).<P>
     *
     * Modifying the tree by inserting, removing, or moving a node invalidates
     * any enumerations created before the modification.
     *
     * @see		#isNodeAncestor()
     * @see		#isNodeDescendant()
     * @exception	IllegalArgumentException if <code>ancestor</code> is
     *						not an ancestor of this node
     * @return	an enumeration for following the path from an ancestor of
     *		this node to this one
     */
    public function pathFromAncestorEnumeration(ancestor:TreeNode):Array {
    	var descendant:TreeNode = this;
	    if (ancestor == null || descendant == null) {
	    	trace("Error : argument is null");
			throw new Error("argument is null");
	    }

	    var current:TreeNode;

	    var stack:Array = new Array();
	    stack.push(descendant);

	    current = descendant;
	    while (current != ancestor) {
			current = current.getParent();
			if (current == null && descendant != ancestor) {
				trace("Error : " + "node " + ancestor + " is not an ancestor of " + descendant);
		    	throw new Error("node " + ancestor + " is not an ancestor of " + descendant);
			}
			stack.push(current);
	    }
	    stack.reverse();
	    return stack;
    }


    //
    //  Child Queries
    //

    /**
     * Returns true if <code>aNode</code> is a child of this node.  If
     * <code>aNode</code> is null, this method returns false.
     *
     * @return	true if <code>aNode</code> is a child of this node; false if 
     *  		<code>aNode</code> is null
     */
    public function isNodeChild(aNode:TreeNode):Boolean {
		var retval:Boolean;
		if (aNode == null) {
		    retval = false;
		} else {
		    if (getChildCount() == 0) {
				retval = false;
		    } else {
				retval = (aNode.getParent() == this);
		    }
		}
		return retval;
    }


    /**
     * Returns this node's first child.
     *
     * @return	the first child of this node, null if this node has no children
     */
    public function getFirstChild():TreeNode {
		if (getChildCount() == 0) {
			return null;
		}
		return getChildAt(0);
    }


    /**
     * Returns this node's last child.
     *
     * @return	the last child of this node, null if this node has no children
     */
    public function getLastChild():TreeNode {
		if (getChildCount() == 0) {
			return null;
		}
		return getChildAt(getChildCount()-1);
    }


    /**
     * Returns the child in this node's child array that immediately
     * follows <code>aChild</code>, which must be a child of this node.  If
     * <code>aChild</code> is the last child, returns null.  This method
     * performs a linear search of this node's children for
     * <code>aChild</code> and is O(n) where n is the number of children; to
     * traverse the entire array of children, use an enumeration instead.
     *
     * @see		#children()
     * @exception	Error if <code>aChild</code> is
     *					null or is not a child of this node
     * @return	the child of this node that immediately follows
     *		<code>aChild</code>
     */
    public function getChildAfter(aChild:TreeNode):TreeNode {
		if (aChild == null) {
			trace("Error : argument is null");
		    throw new Error("argument is null");
		}
	
		var index:int = getIndex(aChild);		// linear search
	
		if (index == -1) {
			trace("Error : node is not a child");
		    throw new Error("node is not a child");
		}
	
		if (index < getChildCount() - 1) {
		    return getChildAt(index + 1);
		} else {
		    return null;
		}
    }


    /**
     * Returns the child in this node's child array that immediately
     * precedes <code>aChild</code>, which must be a child of this node.  If
     * <code>aChild</code> is the first child, returns null.  This method
     * performs a linear search of this node's children for <code>aChild</code>
     * and is O(n) where n is the number of children.
     *
     * @exception	IllegalArgumentException if <code>aChild</code> is null
     *						or is not a child of this node
     * @return	the child of this node that immediately precedes
     *		<code>aChild</code>
     */
    public function getChildBefore(aChild:TreeNode):TreeNode {
		if (aChild == null) {
			trace("Error : argument is null");
		    throw new Error("argument is null");
		}
	
		var index:int = getIndex(aChild);		// linear search
	
		if (index == -1) {
			trace("Error : node is not a child");
		    throw new Error("node is not a child");
		}
	
		if (index > 0) {
		    return getChildAt(index - 1);
		} else {
		    return null;
		}
    }


    //
    //  Sibling Queries
    //


    /**
     * Returns true if <code>anotherNode</code> is a sibling of (has the
     * same parent as) this node.  A node is its own sibling.  If
     * <code>anotherNode</code> is null, returns false.
     *
     * @param	anotherNode	node to test as sibling of this node
     * @return	true if <code>anotherNode</code> is a sibling of this node
     */
    public function isNodeSibling(anotherNode:TreeNode):Boolean {
		var retval:Boolean = false;
	
		if (anotherNode == null) {
		    retval = false;
		} else if (anotherNode == this) {
		    retval = true;
		} else {
		    var  myParent:TreeNode = getParent();
		    retval = (myParent != null && myParent == anotherNode.getParent());
			
			var mp:DefaultMutableTreeNode = DefaultMutableTreeNode(getParent());
		    if (retval && !(mp.isNodeChild(anotherNode))) {
		    	trace("Error : sibling has different parent");
				throw new Error("sibling has different parent");
		    }
		}
	
		return retval;
    }


    /**
     * Returns the number of siblings of this node.  A node is its own sibling
     * (if it has no parent or no siblings, this method returns
     * <code>1</code>).
     *
     * @return	the number of siblings of this node
     */
    public function getSiblingCount():int {
		var myParent:TreeNode = getParent();
	
		if (myParent == null) {
		    return 1;
		} else {
		    return myParent.getChildCount();
		}
    }


    /**
     * Returns the next sibling of this node in the parent's children array.
     * Returns null if this node has no parent or is the parent's last child.
     * This method performs a linear search that is O(n) where n is the number
     * of children; to traverse the entire array, use the parent's child
     * enumeration instead.
     *
     * @see	#children
     * @return	the sibling of this node that immediately follows this node
     */
    public function getNextSibling():DefaultMutableTreeNode {
		var retval:DefaultMutableTreeNode;
	
		var myParent:DefaultMutableTreeNode = DefaultMutableTreeNode(getParent());
	
		if (myParent == null) {
		    retval = null;
		} else {
		    retval = DefaultMutableTreeNode(myParent.getChildAfter(this));	// linear search
		}
	
		if (retval != null && !isNodeSibling(retval)) {
			trace("Error : child of parent is not a sibling");
		    throw new Error("child of parent is not a sibling");
		}
	
		return retval;
    }


    /**
     * Returns the previous sibling of this node in the parent's children
     * array.  Returns null if this node has no parent or is the parent's
     * first child.  This method performs a linear search that is O(n) where n
     * is the number of children.
     *
     * @return	the sibling of this node that immediately precedes this node
     */
    public function getPreviousSibling():DefaultMutableTreeNode {
		var retval:DefaultMutableTreeNode;
	
		var myParent:DefaultMutableTreeNode = DefaultMutableTreeNode(getParent());
	
		if (myParent == null) {
		    retval = null;
		} else {
		    retval = DefaultMutableTreeNode(myParent.getChildBefore(this));	// linear search
		}
	
		if (retval != null && !isNodeSibling(retval)) {
			trace("Error : child of parent is not a sibling");
		    throw new Error("child of parent is not a sibling");
		}
	
		return retval;
    }



    //
    //  Leaf Queries
    //

    /**
     * Returns true if this node has no children.  To distinguish between
     * nodes that have no children and nodes that <i>cannot</i> have
     * children (e.g. to distinguish files from empty directories), use this
     * method in conjunction with <code>getAllowsChildren</code>
     *
     * @see	#getAllowsChildren()
     * @return	true if this node has no children
     */
    public function isLeaf():Boolean {
		return (getChildCount() == 0);
    }


    /**
     * Finds and returns the first leaf that is a descendant of this node --
     * either this node or its first child's first leaf.
     * Returns this node if it is a leaf.
     *
     * @see	#isLeaf()
     * @see	#isNodeDescendant()
     * @return	the first leaf in the subtree rooted at this node
     */
    public function getFirstLeaf():DefaultMutableTreeNode {
		var node:DefaultMutableTreeNode = this;
	
		while (!node.isLeaf()) {
		    node = DefaultMutableTreeNode(node.getFirstChild());
		}
	
		return node;
    }


    /**
     * Finds and returns the last leaf that is a descendant of this node --
     * either this node or its last child's last leaf. 
     * Returns this node if it is a leaf.
     *
     * @see	#isLeaf()
     * @see	#isNodeDescendant()
     * @return	the last leaf in the subtree rooted at this node
     */
    public function getLastLeaf():DefaultMutableTreeNode {
		var node:DefaultMutableTreeNode = this;
	
		while (!node.isLeaf()) {
		    node = DefaultMutableTreeNode(node.getLastChild());
		}
	
		return node;
    }


    /**
     * Returns the leaf after this node or null if this node is the
     * last leaf in the tree.
     * <p>
     * In this implementation of the <code>MutableNode</code> interface,
     * this operation is very inefficient. In order to determine the
     * next node, this method first performs a linear search in the 
     * parent's child-list in order to find the current node. 
     * <p>
     * That implementation makes the operation suitable for short
     * traversals from a known position. But to traverse all of the 
     * leaves in the tree, you should use <code>depthFirstEnumeration</code>
     * to enumerate the nodes in the tree and use <code>isLeaf</code>
     * on each node to determine which are leaves.
     *
     * @see	#depthFirstEnumeration()
     * @see	#isLeaf()
     * @return	returns the next leaf past this node
     */
    public function getNextLeaf():DefaultMutableTreeNode {
		var nextSibling:DefaultMutableTreeNode;
		var myParent:DefaultMutableTreeNode = DefaultMutableTreeNode(getParent());
	
		if (myParent == null)
		    return null;
	
		nextSibling = getNextSibling();	// linear search
	
		if (nextSibling != null)
		    return nextSibling.getFirstLeaf();
	
		return myParent.getNextLeaf();	// tail recursion
    }


    /**
     * Returns the leaf before this node or null if this node is the
     * first leaf in the tree.
     * <p>
     * In this implementation of the <code>MutableNode</code> interface,
     * this operation is very inefficient. In order to determine the
     * previous node, this method first performs a linear search in the 
     * parent's child-list in order to find the current node. 
     * <p>
     * That implementation makes the operation suitable for short
     * traversals from a known position. But to traverse all of the 
     * leaves in the tree, you should use <code>depthFirstEnumeration</code>
     * to enumerate the nodes in the tree and use <code>isLeaf</code>
     * on each node to determine which are leaves.
     *
     * @see		#depthFirstEnumeration()
     * @see		#isLeaf()
     * @return	returns the leaf before this node
     */
    public function getPreviousLeaf():DefaultMutableTreeNode {
		var previousSibling:DefaultMutableTreeNode;
		var myParent:DefaultMutableTreeNode = DefaultMutableTreeNode(getParent());
	
		if (myParent == null)
		    return null;
	
		previousSibling = getPreviousSibling();	// linear search
	
		if (previousSibling != null)
		    return previousSibling.getLastLeaf();
	
		return myParent.getPreviousLeaf();		// tail recursion
    }


    /**
     * Returns the total number of leaves that are descendants of this node.
     * If this node is a leaf, returns <code>1</code>.  This method is O(n)
     * where n is the number of descendants of this node.
     *
     * @see	#isNodeAncestor()
     * @return	the number of leaves beneath this node
     */
    public function getLeafCount():int {
		var count:int = 0;
	
		var node:TreeNode;
		var enum_:Array = breadthFirstEnumeration(); // order matters not
	
		for (var i:int=0; i<enum_.length; i++) {
		    node = TreeNode(enum_[i]);
		    if (node.isLeaf()) {
				count++;
		    }
		}
	
		if (count < 1) {
		    throw new Error("tree has zero leaves");
		}
	
		return count;
    }
    //
    //  Overrides
    //

    /**
     * Returns the result of sending <code>toString()</code> to this node's
     * user object, or null if this node has no user object.
     *
     * @see	#getUserObject()
     */
    public function toString():String {
		if (userObject == null) {
		    return null;
		} else {
		    return userObject.toString();
		}
    }
}
}