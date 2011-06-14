/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.tree.TreeNode;

/**
 * Defines the requirements for a tree node object that can change --
 * by adding or removing child nodes, or by changing the contents
 * of a user object stored in the node.
 * 
 * @author iiley
 * @see org.aswing.tree.DefaultMutableTreeNode
 * @see org.aswing.overflow.JTree
 */
interface org.aswing.tree.MutableTreeNode extends TreeNode{
	
    /**
     * Adds <code>child</code> to the receiver at <code>index</code>.
     * <code>child</code> will be messaged with <code>setParent</code>.
     */
    public function insert(child:MutableTreeNode, index:Number):Void;

    /**
     * Removes the child at <code>index</code> from the receiver.
     */
    public function removeAt(index:Number):Void;

    /**
     * Removes <code>node</code> from the receiver. <code>setParent</code>
     * will be messaged on <code>node</code>.
     */
    public function remove(node:MutableTreeNode):Void;

    /**
     * Resets the user object of the receiver to <code>object</code>.
     */
    public function setUserObject(object:Object):Void;

    /**
     * Removes the receiver from its parent.
     */
    public function removeFromParent():Void;

    /**
     * Sets the parent of the receiver to <code>newParent</code>.
     */
    public function setParent(newParent:MutableTreeNode):Void;
}