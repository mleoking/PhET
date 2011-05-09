/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.tree { 

/**
 * Defines the requirements for an object that can be used as a
 * tree node in a JTree.
 * 
 * @author iiley
 * @see org.aswing.tree.MutableTreeNode
 * @see org.aswing.tree.DefaultMutableTreeNode
 * @see org.aswing.JTree
 */
public interface TreeNode {
	
    /**
     * Returns the child <code>TreeNode</code> at index 
     * <code>childIndex</code>.
     */
    function getChildAt(childIndex:int):TreeNode;

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     */
    function getChildCount():int;

    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     */
    function getParent():TreeNode;

    /**
     * Returns the index of <code>node</code> in the receivers children.
     * If the receiver does not contain <code>node</code>, -1 will be
     * returned.
     */
    function getIndex(node:TreeNode):int;

    /**
     * Returns true if the receiver allows children.
     */
    function getAllowsChildren():Boolean;

    /**
     * Returns true if the receiver is a leaf.
     */
    function isLeaf():Boolean;

    /**
     * Returns the children of the receiver as an <code>Enumeration</code>.
     */
    function children():Array;
}
}