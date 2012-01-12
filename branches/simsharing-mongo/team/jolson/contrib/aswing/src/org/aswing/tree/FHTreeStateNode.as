/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.tree.DefaultMutableTreeNode;
import org.aswing.tree.FixedHeightLayoutCache;
import org.aswing.tree.MutableTreeNode;
import org.aswing.tree.SearchInfo;
import org.aswing.tree.TreeModel;
import org.aswing.tree.TreePath;

/**
 * FHTreeStateNode is used to track what has been expanded.
 * FHTreeStateNode differs from VariableHeightTreeState.TreeStateNode
 * in that it is highly model intensive. That is almost all queries to a
 * FHTreeStateNode result in the TreeModel being queried. And it
 * obviously does not support variable sized row heights.
 * @author iiley
 */
class org.aswing.tree.FHTreeStateNode extends DefaultMutableTreeNode {
	

	/** Is this node expanded? */
	private var expanded:Boolean;

	/** Index of this node from the model. */
	private var childIndex:Number;

  	/** Child count of the receiver. */
  	private var childCount:Number;
 	
	/** Row of the receiver. This is only valid if the row is expanded.
	 */
	private var row:Number;

	/** Path of this node. */
	private var path:TreePath;
	
	private var layoutCache:FixedHeightLayoutCache;

	public function FHTreeStateNode(layoutCache:FixedHeightLayoutCache, userObject:Object, childIndex:Number, row:Number) {
	    super(userObject);
	    this.childIndex = childIndex;
	    this.row = row;
	    this.layoutCache = layoutCache;
	    childCount = 0;
	    expanded = false;
	}
	
	public function setPath(p:TreePath):Void{
		path = p;
	}
	
	public function setRow(r:Number):Void{
		row = r;
	}
	
	//
	// Overriden DefaultMutableTreeNode methods
	//

	/**
	 * Messaged when this node is added somewhere, resets the path
	 * and adds a mapping from path to this node.
	 */
	public function setParent(parent:MutableTreeNode):Void {
	    super.setParent(parent);
	    if(parent != null) {
			path = (FHTreeStateNode(parent)).getTreePath().pathByAddingChild(getUserObject());
			layoutCache.addMapping(this);
	    }
	}

	/**
	 * Messaged when this node is removed from its parent, this messages
	 * <code>removedFromMapping</code> to remove all the children.
	 */
	public function removeAt(childIndex:Number):Void {
	    var node:FHTreeStateNode = FHTreeStateNode(getChildAt(childIndex));

	    node.removeFromMapping();
	    super.removeAt(childIndex);
	}

	/**
	 * Messaged to set the user object. This resets the path.
	 */
	public function setUserObject(o:Object):Void {
	    super.setUserObject(o);
	    if(path != null) {
			var parent:FHTreeStateNode = FHTreeStateNode(getParent());
	
			if(parent != null)
			    resetChildrenPaths(parent.getTreePath());
			else
		    	resetChildrenPaths(null);
	    }
	}

	//
	//

	/**
	 * Returns the index of the receiver in the model.
	 */
	public function getChildIndex():Number {
	    return childIndex;
	}

	/**
	 * Returns the <code>TreePath</code> of the receiver.
	 */
	public function getTreePath():TreePath {
	    return path;
	}

	/**
	 * Returns the child for the passed in model index, this will
	 * return <code>null</code> if the child for <code>index</code>
         * has not yet been created (expanded).
	 */
	public function getChildAtModelIndex(index:Number):FHTreeStateNode {
	    // PENDING: Make this a binary search!
	    for(var counter:Number = getChildCount() - 1; counter >= 0; counter--){
			if((FHTreeStateNode(getChildAt(counter))).childIndex == index)
			    return FHTreeStateNode(getChildAt(counter));
	    }
	    return null;
	}

	/**
	 * Returns true if this node is visible. This is determined by
	 * asking all the parents if they are expanded.
	 */
	public function isVisible():Boolean {
	    var parent:FHTreeStateNode = FHTreeStateNode(getParent());

	    if(parent == null){
			return true;
	    }
	    return (parent.isExpanded() && parent.isVisible());
	}

	/**
	 * Returns the row of the receiver.
	 */
	public function getRow():Number {
	    return row;
	}

	/**
	 * Returns the row of the child with a model index of
	 * <code>index</code>.
	 */
	public function getRowToModelIndex(index:Number):Number {
	    var child:FHTreeStateNode;
	    var lastRow:Number = getRow() + 1;
	    var retValue:Number = lastRow;

	    // This too could be a binary search!
	    var counter:Number = 0;
	    var maxCounter:Number = getChildCount();
	    for(; counter < maxCounter; counter++) {
			child = FHTreeStateNode(getChildAt(counter));
			if(child.childIndex >= index) {
			    if(child.childIndex == index)
					return child.row;
			    if(counter == 0)
					return getRow() + 1 + index;
			    return child.row - (child.childIndex - index);
			}
		}
	    // YECK!
	    return getRow() + 1 + getTotalChildCount() - (childCount - index);
	}

	/**
	 * Returns the number of children in the receiver by descending all
	 * expanded nodes and messaging them with getTotalChildCount.
	 */
	public function getTotalChildCount():Number {
	    if(isExpanded()) {
			var parent:FHTreeStateNode = FHTreeStateNode(getParent());
			var pIndex:Number;
	
			if(parent != null && (pIndex = parent.getIndex(this)) + 1 < parent.getChildCount()) {
			    // This node has a created sibling, to calc total
			    // child count directly from that!
			    var nextSibling:FHTreeStateNode = FHTreeStateNode(parent.getChildAt(pIndex + 1));
	
			    return nextSibling.row - row - (nextSibling.childIndex - childIndex);
			}else{
	 		    var retCount:Number = childCount;
	
			    for(var counter:Number = getChildCount() - 1; counter >= 0; counter--) {
					retCount += (FHTreeStateNode(getChildAt(counter))).getTotalChildCount();
			    }
			    return retCount;
			}
	    }
	    return 0;
	}

	/**
	 * Returns true if this node is expanded.
	 */
	public function isExpanded():Boolean {
	    return expanded;
	}

	/**
	 * The highest visible nodes have a depth of 0.
	 */
	public function getVisibleLevel():Number {
	    if (layoutCache.isRootVisible()) {
			return getLevel();
	    } else {
			return getLevel() - 1;
	    }
	}

	/**
	 * Recreates the receivers path, and all its childrens paths.
	 */
	private function resetChildrenPaths(parentPath:TreePath):Void {
	    layoutCache.removeMapping(this);
	    if(parentPath == null)
			path = new TreePath([getUserObject()]);
	    else
			path = parentPath.pathByAddingChild(getUserObject());
	    layoutCache.addMapping(this);
	    for(var counter:Number = getChildCount() - 1; counter >= 0; counter--){
			(FHTreeStateNode(getChildAt(counter))).resetChildrenPaths(path);
	    }
	}

	/**
	 * Removes the receiver, and all its children, from the mapping
	 * table.
	 */
	private function removeFromMapping():Void {
	    if(path != null) {
			layoutCache.removeMapping(this);
			for(var counter:Number = getChildCount() - 1; counter >= 0; counter--){
			    (FHTreeStateNode(getChildAt(counter))).removeFromMapping();
			}
	    }
	}

	/**
	 * Creates a new node to represent <code>userObject</code>.
	 * This does NOT check to ensure there isn't already a child node
	 * to manage <code>userObject</code>.
	 */
	public function createChildFor(userObject:Object):FHTreeStateNode {
	    var newChildIndex:Number = layoutCache.getModel().getIndexOfChild(getUserObject(), userObject);

	    if(newChildIndex < 0)
			return null;

	    var aNode:FHTreeStateNode;
	    var child:FHTreeStateNode = layoutCache.createNodeForValue(userObject, newChildIndex);
	    var childRow:Number;

	    if(isVisible()) {
			childRow = getRowToModelIndex(newChildIndex);
	    }else {
			childRow = -1;
	    }
	    child.row = childRow;
	    var counter:Number = 0;
	    var maxCounter:Number = getChildCount();
	    for(; counter < maxCounter; counter++) {
			aNode = FHTreeStateNode(getChildAt(counter));
			if(aNode.childIndex > newChildIndex) {
			    insert(child, counter);
			    return child;
			}
	    }
	    append(child);
	    return child;
	}

	/**
	 * adjustRowBy(amount:Number, startIndex:Number)<br>
	 * Adjusts this node, its child, and its parent starting at
	 * an index of <code>index</code> index is the index of the child
	 * to start adjusting from, which is not necessarily the model
	 * index.
	 * <p>
	 * adjustRowBy(amount:Number)<br>
	 * Adjusts the receiver, and all its children rows by
	 * <code>amount</code>.
	 */
	public function adjustRowBy(amount:Number, startIndex:Number):Void {
		if(startIndex == undefined){
		    row += amount;
		    if(expanded) {
				for(var counter:Number = getChildCount() - 1; counter >= 0; counter--){
				    (FHTreeStateNode(getChildAt(counter))).adjustRowBy(amount);
				}
		    }
		}else{
		    // Could check isVisible, but probably isn't worth it.
		    if(expanded) {
				// children following startIndex.
				for(var counter:Number = getChildCount() - 1; counter >= startIndex; counter--)
				    (FHTreeStateNode(getChildAt(counter))).adjustRowBy(amount);
		    }
		    // Parent
		    var parent:FHTreeStateNode = FHTreeStateNode(getParent());
	
		    if(parent != null) {
				parent.adjustRowBy(amount, parent.getIndex(this) + 1);
		    }
		}
	}

	/**
	 * Messaged when the node has expanded. This updates all of
	 * the receivers children rows, as well as the total row count.
	 */
	private function didExpand():Void {
	    var nextRow:Number = setRowAndChildren(row);
	    var parent:FHTreeStateNode = FHTreeStateNode(getParent());
	    var childRowCount:Number = nextRow - row - 1;

	    if(parent != null) {
			parent.adjustRowBy(childRowCount, parent.getIndex(this) + 1);
	    }
	    layoutCache.adjustRowCountBy(childRowCount);
	}

	/**
	 * Sets the receivers row to <code>nextRow</code> and recursively
	 * updates all the children of the receivers rows. The index the
	 * next row is to be placed as is returned.
	 */
	private function setRowAndChildren(nextRow:Number):Number {
	    row = nextRow;

	    if(!isExpanded())
			return row + 1;

	    var lastRow:Number = row + 1;
	    var lastModelIndex:Number = 0;
	    var child:FHTreeStateNode;
	    var maxCounter:Number = getChildCount();

	    for(var counter:Number = 0; counter < maxCounter; counter++) {
			child = FHTreeStateNode(getChildAt(counter));
			lastRow += (child.childIndex - lastModelIndex);
			lastModelIndex = child.childIndex + 1;
			if(child.expanded) {
			    lastRow = child.setRowAndChildren(lastRow);
			}else {
			    child.row = lastRow++;
			}
	    }
	    return lastRow + childCount - lastModelIndex;
	}

	/**
	 * Resets the receivers childrens rows. Starting with the child
	 * at <code>childIndex</code> (and <code>modelIndex</code>) to
	 * <code>newRow</code>. This uses <code>setRowAndChildren</code>
	 * to recursively descend children, and uses
	 * <code>resetRowSelection</code> to ascend parents.
	 */
	// This can be rather expensive, but is needed for the collapse
	// case this is resulting from a remove (although I could fix
	// that by having instances of FHTreeStateNode hold a ref to
	// the number of children). I prefer this though, making determing
	// the row of a particular node fast is very nice!
	public function resetChildrenRowsFrom(newRow:Number, childIndex:Number,
					    modelIndex:Number):Void {
	    var lastRow:Number = newRow;
	    var lastModelIndex:Number = modelIndex;
	    var node:FHTreeStateNode;
	    var maxCounter:Number = getChildCount();

	    for(var counter:Number = childIndex; counter < maxCounter; counter++) {
			node = FHTreeStateNode(getChildAt(counter));
			lastRow += (node.childIndex - lastModelIndex);
			lastModelIndex = node.childIndex + 1;
			if(node.expanded) {
			    lastRow = node.setRowAndChildren(lastRow);
			}
			else {
			    node.row = lastRow++;
			}
	    }
	    lastRow += childCount - lastModelIndex;
	    node = FHTreeStateNode(getParent());
	    if(node != null) {
			node.resetChildrenRowsFrom(lastRow, node.getIndex(this) + 1, this.childIndex + 1);
	    }else { // This is the root, reset total ROWCOUNT!
			layoutCache.setRowCount(lastRow);
	    }
	}

	/**
	 * Makes the receiver visible, but invoking
	 * <code>expandParentAndReceiver</code> on the superclass.
	 */
	public function makeVisible():Void {
	    var parent:FHTreeStateNode = FHTreeStateNode(getParent());

	    if(parent != null)
			parent.expandParentAndReceiver();
	}

	/**
	 * Invokes <code>expandParentAndReceiver</code> on the parent,
	 * and expands the receiver.
	 */
	private function expandParentAndReceiver():Void {
	    var parent:FHTreeStateNode = FHTreeStateNode(getParent());

	    if(parent != null)
			parent.expandParentAndReceiver();
	    expand();
	}

	/**
	 * Expands the receiver.
	 */
	public function expand():Void {
		//trace("try expand : " + getUserObject());
	    if(!expanded && !isLeaf()) {
			//trace("expanding ... ");
			var visible:Boolean = isVisible();
	
			expanded = true;
			childCount = layoutCache.getModel().getChildCount(getUserObject());
	
			if(visible) {
			    didExpand();
			}
	
			// Update the selection model.
			if(visible && layoutCache.getSelectionModel() != null) {
			    layoutCache.getSelectionModel().resetRowSelection();
			}
	    }
	}

	/**
	 * Collapses the receiver. If <code>adjustRows</code> is true,
	 * the rows of nodes after the receiver are adjusted.
	 */
	public function collapse(adjustRows:Boolean):Void {
	    if(expanded) {
			if(isVisible() && adjustRows) {
			    var childCount:Number = getTotalChildCount();
	
			    expanded = false;
			    layoutCache.adjustRowCountBy(-childCount);
			    // We can do this because adjustRowBy won't descend
			    // the children.
			    adjustRowBy(-childCount, 0);
			}else{
			    expanded = false;
			}
			if(adjustRows && isVisible() && layoutCache.getSelectionModel() != null){
			    layoutCache.getSelectionModel().resetRowSelection();
			}
	    }
	}

	/**
	 * Returns true if the receiver is a leaf.
	 */
	public function isLeaf():Boolean {
        var model:TreeModel = layoutCache.getModel();
        return (model != null) ? model.isLeaf(getUserObject()) : true;
	}

	/**
	 * Adds newChild to this nodes children at the appropriate location.
	 * The location is determined from the childIndex of newChild.
	 */
	private function addNode(newChild:FHTreeStateNode):Void {
	    var added:Boolean = false;
	    var childIndex:Number = newChild.getChildIndex();

	    var counter:Number = 0;
	    var maxCounter:Number = getChildCount();
	    for(; counter < maxCounter; counter++) {
			if((FHTreeStateNode(getChildAt(counter))).getChildIndex() > childIndex) {
			    added = true;
			    insert(newChild, counter);
			    counter = maxCounter;
			}
	    }
	    if(!added)
			append(newChild);
	}

	/**
	 * Removes the child at <code>modelIndex</code>.
	 * <code>isChildVisible</code> should be true if the receiver
	 * is visible and expanded.
	 */
	public function removeChildAtModelIndex(modelIndex:Number, isChildVisible:Boolean):Void {
	    var childNode:FHTreeStateNode = getChildAtModelIndex(modelIndex);

	    if(childNode != null) {
			var row:Number = childNode.getRow();
			var index:Number = getIndex(childNode);
	
			childNode.collapse(false);
			removeAt(index);
			adjustChildIndexs(index, -1);
			childCount--;
			if(isChildVisible) {
			    // Adjust the rows.
			    resetChildrenRowsFrom(row, index, modelIndex);
			}
	    }else {
			var maxCounter:Number = getChildCount();
			var aChild:FHTreeStateNode;
	
			for(var counter:Number = 0; counter < maxCounter; counter++) {
			    aChild = FHTreeStateNode(getChildAt(counter));
			    if(aChild.childIndex >= modelIndex) {
					if(isChildVisible) {
					    adjustRowBy(-1, counter);
					    layoutCache.adjustRowCountBy(-1);
					}
					// Since matched and children are always sorted by
					// index, no need to continue testing with the
					// above.
					for(; counter < maxCounter; counter++){
					    (FHTreeStateNode(getChildAt(counter))).childIndex--;
					}
					childCount--;
					return;
			    }
			}
			// No children to adjust, but it was a child, so we still need
			// to adjust nodes after this one.
			if(isChildVisible) {
			    adjustRowBy(-1, maxCounter);
			    layoutCache.adjustRowCountBy(-1);
			}
			childCount--;
	    }
	}

	/**
	 * Adjusts the child indexs of the receivers children by
	 * <code>amount</code>, starting at <code>index</code>.
	 */
	private function adjustChildIndexs(index:Number, amount:Number):Void {
		var counter:Number = index;
		var maxCounter:Number = getChildCount();
	    for(; counter < maxCounter; counter++) {
			(FHTreeStateNode(getChildAt(counter))).childIndex += amount;
	    }
	}

	/**
	 * Messaged when a child has been inserted at index. For all the
	 * children that have a childIndex >= index their index is incremented
	 * by one.
	 */
	public function childInsertedAtModelIndex(index:Number, isExpandedAndVisible:Boolean):Void {
	    var aChild:FHTreeStateNode;
	    var maxCounter:Number = getChildCount();

	    for(var counter:Number = 0; counter < maxCounter; counter++) {
			aChild = FHTreeStateNode(getChildAt(counter));
			if(aChild.childIndex >= index) {
			    if(isExpandedAndVisible) {
					adjustRowBy(1, counter);
					layoutCache.adjustRowCountBy(1);
			    }
			    /* Since matched and children are always sorted by
			       index, no need to continue testing with the above. */
			    for(; counter < maxCounter; counter++){
					(FHTreeStateNode(getChildAt(counter))).childIndex++;
			    }
			    childCount++;
			    return;
			}
	    }
	    // No children to adjust, but it was a child, so we still need
	    // to adjust nodes after this one.
	    if(isExpandedAndVisible) {
			adjustRowBy(1, maxCounter);
			layoutCache.adjustRowCountBy(1);
	    }
	    childCount++;
	}

	/**
	 * Returns true if there is a row for <code>row</code>.
	 * <code>nextRow</code> gives the bounds of the receiver.
	 * Information about the found row is returned in <code>info</code>.
	 * This should be invoked on root with <code>nextRow</code> set
	 * to <code>getRowCount</code>().
	 */
	public function getPathForRow(row:Number, nextRow:Number, info:SearchInfo):Boolean {
	    if(this.row == row) {
			info.node = this;
			info.isNodeParentNode = false;
			info.childIndex = childIndex;
			return true;
	    }

	    var child:FHTreeStateNode;
	    var lastChild:FHTreeStateNode = null;
		var counter:Number = 0;
		var maxCounter:Number = getChildCount();
	    for(;counter < maxCounter; counter++) {
			child = FHTreeStateNode(getChildAt(counter));
			if(child.row > row) {
			    if(counter == 0) {
					// No node exists for it, and is first.
					info.node = this;
					info.isNodeParentNode = true;
					info.childIndex = row - this.row - 1;
					return true;
			    }else {
					// May have been in last childs bounds.
					var  lastChildEndRow:Number = 1 + child.row - (child.childIndex - lastChild.childIndex);
		
					if(row < lastChildEndRow) {
					    return lastChild.getPathForRow(row, lastChildEndRow, info);
					}
					// Between last child and child, but not in last child
					info.node = this;
					info.isNodeParentNode = true;
					info.childIndex = row - lastChildEndRow +
					                        lastChild.childIndex + 1;
					return true;
			    }
			}
			lastChild = child;
	    }

	    // Not in children, but we should have it, offset from
	    // nextRow.
	    if(lastChild != null) {
			var lastChildEndRow:Number = nextRow - (childCount - lastChild.childIndex) + 1;
	
			if(row < lastChildEndRow) {
			    return lastChild.getPathForRow(row, lastChildEndRow, info);
			}
			// Between last child and child, but not in last child
			info.node = this;
			info.isNodeParentNode = true;
			info.childIndex = row - lastChildEndRow + lastChild.childIndex + 1;
			return true;
	    } else {
			// No children.
			var retChildIndex:Number = row - this.row - 1;
	
			if(retChildIndex >= childCount) {
			    return false;
			}
			info.node = this;
			info.isNodeParentNode = true;
			info.childIndex = retChildIndex;
			return true;
	    }
	}

	/**
	 * Asks all the children of the receiver for their totalChildCount
	 * and returns this value (plus stopIndex).
	 */
	private function getCountTo(stopIndex:Number):Number {
	    var aChild:FHTreeStateNode;
	    var retCount:Number = stopIndex + 1;
		var counter:Number = 0;
		var maxCounter:Number = getChildCount();
	    for(; counter < maxCounter; counter++) {
			aChild = FHTreeStateNode(getChildAt(counter));
			if(aChild.childIndex >= stopIndex)
			    counter = maxCounter;
			else
			    retCount += aChild.getTotalChildCount();
	    }
	    if(parent != null){
			return retCount + (FHTreeStateNode(getParent())).getCountTo(childIndex);
	    }
	    if(!layoutCache.isRootVisible()){
			return (retCount - 1);
	    }
	    return retCount;
	}

	/**
	 * Returns the number of children that are expanded to
	 * <code>stopIndex</code>. This does not include the number
	 * of children that the child at <code>stopIndex</code> might
	 * have.
	 */
	private function getNumExpandedChildrenTo(stopIndex:Number):Number {
	    var aChild:FHTreeStateNode;
	    var retCount:Number = stopIndex + 1;
		var counter:Number = 0;
		var maxCounter:Number = getChildCount();

	    for(; counter < maxCounter; counter++) {
			aChild = FHTreeStateNode(getChildAt(counter));
			if(aChild.childIndex >= stopIndex)
			    return retCount;
			else {
			    retCount += aChild.getTotalChildCount();
			}
	    }
	    return retCount;
	}

	/**
	 * Messaged when this node either expands or collapses.
	 */
	private function didAdjustTree():Void {
	}
}