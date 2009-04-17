/*
 CopyRight @ 2005 XLands.com INC. All rights reserved.
*/

import org.aswing.AbstractListModel;
import org.aswing.event.TreeModelEvent;
import org.aswing.event.TreeModelListener;
import org.aswing.ListModel;
import org.aswing.tree.TreeModel;
import org.aswing.tree.TreePath;
import org.aswing.tree.TreePathMap;
import org.aswing.util.Vector;

/**
 * The <code>TreeModel</code> to <code>ListModel</code> adapter and controller.
 * It store all the visible <code>TreePath</code> in the <code>ListModel</code>.
 * 
 * @see org.aswing.overflow.JListTree
 * @author iiley
 */
class org.aswing.tree.list.ListTreeModel extends AbstractListModel implements ListModel, TreeModelListener{
	
	private var vector:Vector;
	private var treeModel:TreeModel;
	private var rootVisible:Boolean;
	private var expandedPaths:TreePathMap;
	
	/**
	 * Create a ListTreeModel with treeModel.
	 * @param treeModel the treeModel
	 */
	public function ListTreeModel(treeModel:TreeModel){
		super();
		this.treeModel = treeModel;
		vector = new Vector();
		expandedPaths = new TreePathMap();
		rootVisible = false;
		treeModel.addTreeModelListener(this);
		reconstruct();
	}

	public function getElementAt(index : Number) : Object {
		return vector.get(index);
	}

	public function getSize() : Number {
		return vector.size();
	}
	
	//--
	
	/**
	 * Returns the tree model.
	 */
	public function getTreeModel():TreeModel{
		return treeModel;
	}
	
    /**
     * Determines whether or not the root node from
     * the <code>TreeModel</code> is visible.
     *
     * @param rootVisible true if the root node of the tree is to be displayed
     */	
	public function setRootVisible(b:Boolean):Void{
		if(b != rootVisible){
			rootVisible = b;
			reconstruct();
		}
	}
	
	/**
     * Returns true if the root node of the tree is displayed.
     *
     * @return true if the root node of the tree is displayed
     */
	public function isRootVisible():Boolean{
		return rootVisible;
	}
	
    /**
     * Returns true if the node identified by the path is currently expanded,
     * 
     * @param path  the <code>TreePath</code> specifying the node to check
     * @return false if any of the nodes in the node's path are collapsed, 
     *               true if all nodes in the path are expanded
     */	
	public function isExpanded(path:TreePath):Boolean{
		for(var node:TreePath=path; node!=null; node=node.getParentPath()){
			if(!getExpandedState(node)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns true if this path is in a expanded parent or it's root.
	 */	
	public function isPathVisible(path:TreePath):Boolean{
		if(path.getLastPathComponent() == treeModel.getRoot()){
			return true;
		}
		return isExpanded(path.getParentPath());
	}
	
	/**
	 * Sets the specified path expand or collapse.
	 * This method will not influence it's parent or children expanding or collapsing.
	 * @param path the path
	 * @param isExpand true to set to be expanded, false to be collapsed.
	 */
	public function setExpandState(path:TreePath, isExpand:Boolean):Void{
		if(path == null){
			trace("/e/Error path is null!");
			return;
		}
		if(treeModel.isLeaf(path.getLastPathComponent())){
			return;
		}
		if(isExpand){
			if(!expandedPaths.containsKey(path)){
				expandedPaths.put(path, true);
				if(path.getPathCount() <= 1 || isExpanded(path.getParentPath())){
					doExpandPath(path);
				}
			}
		}else{
			var removed:Boolean = (expandedPaths.remove(path) != null);
			if(removed && (path.getPathCount() <= 1 || isExpanded(path.getParentPath()))){
				doCollapsePath(path);
			}
		}
	}
	
	/**
	 * Returns whether specified path is set expanded.
	 * @return whether specified path is set expanded.
	 * @see #setExpandState()
	 */
	public function getExpandedState(path:TreePath):Boolean{
		if(treeModel.isLeaf(path.getLastPathComponent())){
			return false;
		}else if(path.getPathCount() == 1 && !isRootVisible()){//root
			return true;
		}else{
			return expandedPaths.containsKey(path);
		}
	}
	
	/**
	 * Collpases all nodes.
	 * If root visible, there will be only root visible.
	 * If root not visible, there will be only all root's direct children visible.
	 */
	public function collapseAll():Void{
		vector.clear();
		expandedPaths.clear();
		fireIntervalRemoved(this, 0, getSize() - 1);
		var rootPath:TreePath = new TreePath([treeModel.getRoot()]);
		if(rootVisible){
			vector.append(rootPath);
			fireIntervalAdded(this, 0, 0);
		}else{
			setExpandState(rootPath, true);
		}
	}
	
	/**
	 * Expands all the nodes.
	 */
	public function expandAll():Void{
		expandPathAll(new TreePath([treeModel.getRoot()]));
	}
	
	/**
	 * Expands all the nodes's children and sub-children.
	 */
	public function expandPathAll(path:TreePath):Void{
		setExpandState(path, true);
		var parent:Object = path.getLastPathComponent();
		var n:Number = treeModel.getChildCount(parent);
		for(var i:Number=0; i<n; i++){
			var subPath:TreePath = TreePath.createTreePath(path, treeModel.getChild(parent, i));
			expandPathAll(subPath);
		}
	}
	
	/**
	 * Returns the index(row) of the path.
	 * 
	 * @param path the path
	 * @return the index(row) of the path, or -1 the path is not visible
	 */
	public function indexOfPath(path:TreePath):Number{
		var n:Number = vector.size();
		for(var i:Number=0; i<n; i++){
			if(path.equals(vector.get(i))){
				return i;
			}
		}
		return -1;
	}
	
	//--
	
	private function reconstruct():Void{
		collapseAll();
	}
	
	private function doExpandPath(path:TreePath):Void{
		var index:Number = vector.indexOf(path);
		var parent:Object = path.getLastPathComponent();
		var n:Number = treeModel.getChildCount(parent);
		var startI:Number = indexOfPath(path) + 1;
		var arr:Array = new Array(n);
		var needExpands:Array = new Array();
		for(var i:Number=0; i<n; i++){
			var subPath:TreePath = TreePath.createTreePath(path, treeModel.getChild(parent, i));
			arr[i] = subPath;
			if(getExpandedState(subPath)){
				needExpands.push(subPath);
			}
		}
		if(startI > 0){
			fireContentsChanged(this, startI-1, startI-1);
		}
		vector.appendAll(arr, startI);
		fireIntervalAdded(this, startI, startI+n-1);
		for(var i:Number=0; i<needExpands.length; i++){
			doExpandPath(TreePath(needExpands[i]));
		}
	}
	
	//deltaChildren the children number has changed
	private function doCollapsePath(path:TreePath, deltaChildren:Number):Void{
		if(deltaChildren == undefined){
			deltaChildren = 0;
		}
		var parent:Object = path.getLastPathComponent();
		var n:Number = treeModel.getChildCount(parent) - deltaChildren;
		if(n <= 0){
			return;
		}
		var startI:Number = indexOfPath(path);
		for(var i:Number=startI+1; i<=startI+n; i++){
			var subPath:TreePath = TreePath(vector.get(i));
			if(getExpandedState(subPath)){
				doCollapsePath(subPath);
			}
		}
		if(startI >= 0){
			fireContentsChanged(this, startI, startI);
		}
		vector.removeRange(startI+1, startI+n);
		fireIntervalRemoved(this, startI+1, startI+n);
	}
	//-----------------------------
	
	public function treeNodesChanged(e : TreeModelEvent) : Void {
		var path:TreePath = e.getTreePath();
		if(isPathVisible(path)){
			var i0:Number = indexOfPath(path);
			if(i0 >= 0){
				if(isExpanded(path)){
					fireContentsChanged(this, i0, treeModel.getChildCount(path.getLastPathComponent()));
				}else{
					fireContentsChanged(this, i0, i0);
				}
			}
		}
	}

	public function treeNodesInserted(e : TreeModelEvent) : Void {
		var path:TreePath = e.getTreePath();
		var indices:Array = e.getChildIndices();
		var number:Number = indices.length;
		if(isPathVisible(path)){
			if(getExpandedState(path)){
				doCollapsePath(path, number);
				doExpandPath(path);
			}
		}
	}

	public function treeNodesRemoved(e : TreeModelEvent) : Void {
		var path:TreePath = e.getTreePath();
		var indices:Array = e.getChildIndices();
		var number:Number = -indices.length;
		if(isPathVisible(path)){
			if(expandedPaths.containsKey(path)){
				doCollapsePath(path, number);
				doExpandPath(path);
			}
		}
	}

	public function treeStructureChanged(e : TreeModelEvent) : Void {
		reconstruct();
	}

}