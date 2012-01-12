/*
 CopyRight @ 2005 XLands.com INC. All rights reserved.
*/

import org.aswing.Component;
import org.aswing.dnd.ListSourceData;
import org.aswing.GeneralListCellFactory;
import org.aswing.geom.Point;
import org.aswing.overflow.JList;
import org.aswing.ListCell;
import org.aswing.ListCellFactory;
import org.aswing.tree.DefaultMutableTreeNode;
import org.aswing.tree.DefaultTreeModel;
import org.aswing.tree.list.AbstractListTreeCell;
import org.aswing.tree.list.DefaultListTreeCell;
import org.aswing.tree.list.ListTreeModel;
import org.aswing.tree.TreeModel;
import org.aswing.tree.TreePath;

/**
 * JListTree is a tree component that simulated by <code>JList</code>.
 * It is very faster and simpler than <code>JTree</code>, it use same model(<code>TreeModel</code>) type from <code>JTree</code>, 
 * so you can easy switch from JTree to JListTree or reverve. But JListTree has no editing functionity.
 * <p>
 * JListTree use a adapter class <code>ListTreeModel</code> to adpapt from <code>TreeModel</code> 
 * to <code>ListModel</code>, most controller method are defined in <code>ListTreeModel</code>.
 * <p>
 * <b>Note:</b><br>
 * <ul>
 * <li>You should use a subclass of <code>AbstractListTreeCell</code> to render the cells, default is 
 * <code>DefaultListTreeCell</code>, you can define your owner by extending <code>AbstractListTreeCell</code>.
 * <li>Because of beeing simulated by <code>JList</code>, so JListTree has no editing functionity, if you use it 
 * to replace a JTree with edit function. You must implement a edit function manually or just ignor it.
 * <li>JListTree also can take advantages of speed if your tree node object implemented <code>Identifiable</code>. 
 * see class test.tree.Item for example.
 * <li>Again, because of extending <code>JList</code>, JListTree can use 3 different way to manage cells. See 
 * {@link org.aswing.ListCellFactory} for details.
 * <li>Note that <code>JListTree</code> does not allow duplicate nodes to exist as children under 
 * the same parent too -- each sibling must be a unique object.
 * <li>JListTree auto Drag and Drop handler inherited from JList does not works, if you need you can take JList 
 * as example to implement it.
 * <ul>
 * @see org.aswing.overflow.JTree
 * @see org.aswing.overflow.JList
 * @see org.aswing.ListCellFactory
 * @see org.aswing.tree.TreeModel
 * @see org.aswing.tree.list.ListTreeModel
 * @see org.aswing.tree.list.AbstractListTreeCell
 * @see org.aswing.tree.Identifiable
 * @see #autoDragAndDropHandler()
 * 
 * @author iiley
 */
class org.aswing.overflow.JListTree extends JList {
	
	private var treeModel:TreeModel;
	private var toggleClickCount:Number;
		
	/**
	 * JListTree(treeModel : TreeModel, cellFactory : ListCellFactory)<br>
	 * JListTree(treeModel : TreeModel) use default cell factory<br>
	 * JListTree() use default model and default cell factory
	 * <p>
	 * Create a JListTree.
     * @param treeModel  (optional)the <code>TreeModel</code> to use as the data model. If miss 
     * 					it, a defaul model will be created.
     * @param cellFactory  (optional)the <code>ListCellFactory</code> to generat the cells. If miss 
     * 					it, a defaul factory will be created.
	 */
	public function JListTree(treeModel : TreeModel, cellFactory : ListCellFactory) {
		super(new ListTreeModel(treeModel == undefined ? getDefaultTreeModel() : treeModel), 
			cellFactory == undefined ? new GeneralListCellFactory(DefaultListTreeCell, true, true) : cellFactory);
		setName("JListTree");
		this.treeModel = getListTreeModel().getTreeModel();
		this.addEventListener(ON_ITEM_CLICKED, __onXTreeItemClicked, this);
		toggleClickCount = 2;
		setOpaque(true);
		
		autoDragAndDropType = DND_NONE;	
	}
	
    /**
     * Creates and returns a sample <code>TreeModel</code>.
     * Used primarily for beanbuilders to show something interesting.
     *
     * @return the default <code>TreeModel</code>
     */
    public static function getDefaultTreeModel():DefaultTreeModel {
        var root:DefaultMutableTreeNode = new DefaultMutableTreeNode("JTree");
		var parent:DefaultMutableTreeNode;
	
		parent = new DefaultMutableTreeNode("colors");
		root.append(parent);
		parent.append(new DefaultMutableTreeNode("blue"));
		parent.append(new DefaultMutableTreeNode("violet"));
		parent.append(new DefaultMutableTreeNode("red"));
		parent.append(new DefaultMutableTreeNode("yellow"));
	
		parent = new DefaultMutableTreeNode("sports");
		root.append(parent);
		parent.append(new DefaultMutableTreeNode("basketball"));
		parent.append(new DefaultMutableTreeNode("soccer"));
		parent.append(new DefaultMutableTreeNode("football"));
		parent.append(new DefaultMutableTreeNode("hockey"));
			
		parent = new DefaultMutableTreeNode("food");
		root.append(parent);
		parent.append(new DefaultMutableTreeNode("hot dogs"));
		parent.append(new DefaultMutableTreeNode("pizza"));
		parent.append(new DefaultMutableTreeNode("ravioli"));
		parent.append(new DefaultMutableTreeNode("bananas"));
		root.append(parent);
        return new DefaultTreeModel(root);
    }	
	
	/**
	 * Sets the <code>TreeModel</code>.
	 * A new <code>ListTreeModel</code> will be created.
	 */
	public function setTreeModel(treeModel:TreeModel):Void{
		if(treeModel != this.treeModel){
			this.treeModel = treeModel;
			this.setModel(new ListTreeModel(treeModel));
		}
	}
	
	/**
	 * Returns the <code>TreeModel</code>
	 */
	public function getTreeModel():TreeModel{
		return treeModel;
	}
	
	/**
	 * Returns the <code>ListTreeModel</code>
	 */
	public function getListTreeModel():ListTreeModel{
		return ListTreeModel(getModel());
	}
	
    /**
     * Determines whether or not the root node from
     * the <code>TreeModel</code> is visible.
     *
     * @param rootVisible true if the root node of the tree is to be displayed
     */		
	public function setRootVisible(b:Boolean):Void{
		getListTreeModel().setRootVisible(b);
	}
	
	/**
     * Returns true if the root node of the tree is displayed.
     *
     * @return true if the root node of the tree is displayed
     */	
	public function isRootVisible():Boolean{
		return getListTreeModel().isRootVisible();
	}
	
    /**
     * Returns true if the node identified by the path is currently expanded,
     * 
     * @param path  the <code>TreePath</code> specifying the node to check
     * @return false if any of the nodes in the node's path are collapsed, 
     *               true if all nodes in the path are expanded
     */		
	public function isExpanded(path:TreePath):Boolean{
		return getListTreeModel().isExpanded(path);
	}
	
	/**
	 * Sets the specified path expand or collapse.
	 * This method will not influence it's parent or children expanding or collapsing.
	 * @param path the path
	 * @param isExpand true to set to be expanded, false to be collapsed.
	 */	
	public function setExpandState(path:TreePath, isExpand:Boolean):Void{
		getListTreeModel().setExpandState(path, isExpand);
	}
	
	/**
	 * Returns whether specified path is set expanded.
	 * @return whether specified path is set expanded.
	 * @see #setExpandState()
	 */	
	public function getExpandedState(path:TreePath):Boolean{
		return getListTreeModel().getExpandedState(path);
	}
	
	/**
	 * Collpases all nodes.
	 * If root visible, there will be only root visible.
	 * If root not visible, there will be only all root's direct children visible.
	 */	
	public function collapseAll():Void{
		getListTreeModel().collapseAll();
	}
	
	/**
	 * Expands all the nodes.
	 */	
	public function expandAll():Void{
		getListTreeModel().expandAll();
	}
	
	/**
	 * Expands all the nodes's children and sub-children.
	 */	
	public function expandPathAll(path:TreePath):Void{
		getListTreeModel().expandPathAll(path);
	}
	
    /**
     * Returns the path for the specified row. 
     *
     * @param row  an integer specifying a row
     * @return the <code>TreePath</code> to the specified node.
     */
    public function getPathForRow(row:Number):TreePath {
    	return TreePath(getModel().getElementAt(row));
    }
	
    /**
     * Returns the path to the first selected node.
     *
     * @return the <code>TreePath</code> for the first selected node,
     *		or <code>null</code> if nothing is currently selected
     */	
	public function getSelectionPath():TreePath{
		return TreePath(getSelectedValue());
	}
	
    /**
     * Returns the paths of all selected values.
     *
     * @return an array of <code>TreePath</code> objects indicating the selected.
     */	
	public function getSelectionPaths():Array{
		return getSelectedValues();
	}
	
    /**
     * Gets the first selected row.
     *
     * @return an integer designating the first selected row, where 0 is the 
     *         first row in the display
     */	
	public function getSelectionRow():Number{
		return getSelectedIndex();
	}
	
    /**
     * Returns all of the currently selected rows.
     *
     * @return an array of integers that identifies all currently selected rows
     *         where 0 is the first row in the display
     */	
	public function getSelectionRows():Array{
		return getSelectedIndices();
	}
	
    /**
     * Adds the node identified by the specified <code>TreePath</code>
     * to the current selection. If any
     * component of the path is hidden, nothing will happen. So make sure 
     * the path is visible(parent is expanded) before call this method.
     *
     * @param path the <code>TreePath</code> to add
     * @return true selected, false that the path is not visible, so can't do selection. 
     */
    public function addSelectionPath(path:TreePath):Boolean {
    	var index:Number = getListTreeModel().indexOfPath(path);
    	if(index >= 0){
        	addSelectionInterval(index, index);
        	return true;
    	}else{
    		return false;
    	}
    }	
    
    /**
     * Adds each path in the array of paths to the current selection. If any
     * component of the path is hidden, nothing will happen. So make sure 
     * the path is visible(parent is expanded) before call this method.
     *
     * @param paths an array of <code>TreePath</code> objects that specifies
     *		the nodes to add
     */
    public function addSelectionPaths(paths:Array):Void {
        for(var i:Number=0; i<paths.length; i++){
        	addSelectionPath(TreePath(paths[i]));
        }
    }    
    
    /** 
     * @see org.aswing.ListSelectionModel#addSelectionInterval()
     * @see #removeSelectionRowInterval()
     * @see #addSelectionInterval()
     */	    
    public function addSelectionRowInterval(row0:Number, row1:Number):Void{
    	addSelectionInterval(row0, row1);
    }
    
    /** 
     * @see org.aswing.ListSelectionModel#removeSelectionInterval()
     * @see #removeSelectionInterval()
     * @see #addSelectionRowInterval()
     */	    
    public function removeSelectionRowInterval(row0:Number, row1:Number):Void{
    	removeSelectionInterval(row0, row1);
    }
    
    /**
     * Removes the node identified by the specified path from the current
     * selection.
     * 
     * @param path  the <code>TreePath</code> identifying a node
     */
    public function removeSelectionPath(path:TreePath):Void {
    	var index:Number = getListTreeModel().indexOfPath(path);
    	if(index >= 0){
        	removeSelectionInterval(index, index);
    	}
    }

    /**
     * Removes the nodes identified by the specified paths from the 
     * current selection.
     *
     * @param paths an array of <code>TreePath</code> objects that
     *              specifies the nodes to remove
     */
    public function removeSelectionPaths(paths:Array):Void {
        for(var i:Number=0; i<paths.length; i++){
        	removeSelectionPath(TreePath(paths[i]));
        }
    }    

    /** 
     * Selects the node identified by the specified path. If any
     * component of the path is hidden, nothing will happen. So make sure 
     * the path is visible(parent is expanded) before call this method.
     * <p>
     * <code>setSelectionRow</code> is faster than this method.
     * 
     * @param path the <code>TreePath</code> specifying the node to select
     * @return true selected, false that the path is not visible, so can't do selection. 
     * @see #setSelectionRow()
     */
    public function setSelectionPath(path:TreePath):Boolean {
    	var index:Number = getListTreeModel().indexOfPath(path);
    	if(index >= 0){
        	setSelectedIndex(index);
        	return true;
    	}else{
    		return false;
    	}
    }

    /** 
     * Selects the nodes identified by the specified array of paths.
     * Only visible path will be selected successfully.
     * <p>
     * <code>setSelectionRows</code> is faster than this method.
     *
     * @param paths an array of <code>TreePath</code> objects that specifies
     *		the nodes to select
     * @see #setSelectionRows()
     * @see #setSelectionPath()
     */
    public function setSelectionPaths(paths:Array):Void {
    	clearSelection();
        addSelectionPaths(paths);
    }
    
    /**
     * Selects the node at the specified row in the display.
     *
     * @param row  the row to select, where 0 is the first row in
     *             the display
     */
    public function setSelectionRow(row:Number):Void {
        setSelectedIndex(row);
    }

    /**
     * Selects the nodes corresponding to each of the specified rows
     * in the display. 
     * 
     * @param rows  an array of ints specifying the rows to select,
     *              where 0 indicates the first row in the display
     */
    public function setSelectionRows( rows:Array):Void {
        setSelectedIndices(rows);
    }

    /**
     * Sets the number of mouse clicks before a node will expand or close.
     * The default is 2. 
     */
    public function setToggleClickCount(clickCount:Number):Void {
		toggleClickCount = clickCount;
    }

    /**
     * Returns the number of mouse clicks needed to expand or close a node.
     *
     * @return number of mouse clicks before node is expanded
     */
    public function getToggleClickCount():Number {
		return toggleClickCount;
    }    
    
	//-------------------------
	
	private function createNewCell():ListCell{
		var cell:AbstractListTreeCell  = AbstractListTreeCell(super.createNewCell());
		cell.setTree(this);
		return cell;
	}
	
	private function isMouseOnItemBody(source:Component):Boolean{
		var cell:ListCell = getCellByCellComponent(source);
		var tc:AbstractListTreeCell = AbstractListTreeCell(cell);
		var com:Component = cell.getCellComponent();
		var p:Point = com.getMousePosition();
		return (p.x > tc.getIndentAndArrowAmount());
	}
	
    /**
     * Overrided method from super
     */
	private function __onItemPress(source:Component):Void{
		if(isMouseOnItemBody(source)){
			super.__onItemPress(source);
		}else{
			var cell:ListCell = getCellByCellComponent(source);
			var tc:AbstractListTreeCell = AbstractListTreeCell(cell);
			var com:Component = cell.getCellComponent();
			var p:Point = com.getMousePosition();
			if(p.x > tc.getIndent()){
				var path:TreePath = TreePath(cell.getCellValue());
				getListTreeModel().setExpandState(path, !getListTreeModel().isExpanded(path));
			}
		}
	}
	
    /**
     * Overrided method from super
     */	
	private function __onItemRelease(source:Component):Void{
		if(isMouseOnItemBody(source)){
			super.__onItemRelease(source);
		}
	}
	
    /**
     * Overrided method from super
     */	
	private function __onItemClicked(source:Component, clickCount:Number):Void{
		if(isMouseOnItemBody(source)){
			super.__onItemClicked(source, clickCount);
		}
	}
	
	private function __onXTreeItemClicked(source:JList, value, cell:ListCell, clickCount:Number):Void{
		if(clickCount == toggleClickCount){
			var tc:AbstractListTreeCell = AbstractListTreeCell(cell);
			var com:Component = cell.getCellComponent();
			var p:Point = com.getMousePosition();
			var path:TreePath = TreePath(value);
			getListTreeModel().setExpandState(path, !getListTreeModel().isExpanded(path));
		}
	}
}