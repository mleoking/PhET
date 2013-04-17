/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.EmptyLayout;
import org.aswing.event.TreeModelEvent;
import org.aswing.event.TreeModelListener;
import org.aswing.event.TreeSelectionEvent;
import org.aswing.FocusManager;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.Insets;
import org.aswing.overflow.JTree;
import org.aswing.LookAndFeel;
import org.aswing.plaf.basic.CellRendererPane;
import org.aswing.plaf.TreeUI;
import org.aswing.plaf.UIResource;
import org.aswing.tree.AbstractLayoutCache;
import org.aswing.tree.DefaultTreeSelectionModel;
import org.aswing.tree.FixedHeightLayoutCache;
import org.aswing.tree.NodeDimensions;
import org.aswing.tree.TreeCell;
import org.aswing.tree.TreeCellEditor;
import org.aswing.tree.TreeModel;
import org.aswing.tree.TreePath;
import org.aswing.tree.TreeSelectionModel;
import org.aswing.UIManager;
import org.aswing.util.Delegate;
import org.aswing.util.Vector;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicTreeUI extends TreeUI implements NodeDimensions, TreeModelListener{
	
    private static var EMPTY_INSETS:Insets;

    /** Object responsible for handling sizing and expanded issues. */
    private var treeState:AbstractLayoutCache;	
    
    private var rendererPane:CellRendererPane;    
    /** Total distance that will be indented.  The sum of leftChildIndent
      * and rightChildIndent. 
      */
    private var totalChildIndent:Number;
    /** How much the depth should be offset to properly calculate
     * x locations. This is based on whether or not the root is visible,
     * and if the root handles are visible. 
     */
    private var depthOffset:Number;
    /** Distance between left margin and where vertical dashes will be
      * drawn. */
    private var leftChildIndent:Number;
    /** Distance to add to leftChildIndent to determine where cell
      * contents will be drawn. */
    private var rightChildIndent:Number;       
    /** If true, the property change event for LEAD_SELECTION_PATH_PROPERTY,
     * or ANCHOR_SELECTION_PATH_PROPERTY will not generate a repaint. */
    private var ignoreLAChange:Boolean;
    
    private var tree:JTree;
    private var treeModel:TreeModel;
    private var editor:TreeCellEditor;
    private var selectionModel:TreeSelectionModel;
    private var treeListener:Object;
    private var selectionModelListener:Object;
    private var cells:Vector;
    private var validCachedViewSize:Boolean;
    private var viewSize:Dimension;
    private var lastViewPosition:Point;
	
	public function BasicTreeUI() {
		super();
		if(EMPTY_INSETS == undefined){
			EMPTY_INSETS = new Insets(0, 0, 0, 0);
		}
		totalChildIndent = 0;
		depthOffset = 0;
		leftChildIndent = 0;
		rightChildIndent = 0;
		
		paintFocusedIndex = -1;
		cells = new Vector();
		lastViewPosition = new Point();
		viewSize = new Dimension();
		validCachedViewSize = false;
	}
	

    public function installUI(c:Component):Void {
        tree = JTree(c);
        installDefaults();
        installComponents();
        installListeners();
    }
	public function uninstallUI(c:Component):Void {
        uninstallDefaults();
        uninstallComponents();
        uninstallListeners();
    }
    
    private function installDefaults():Void {
    	var pp:String = "Tree.";
        LookAndFeel.installColorsAndFont(tree, pp + "background", pp + "foreground", pp + "font");
        LookAndFeel.installBorder(tree, pp+"border");
        LookAndFeel.installBasicProperties(tree, pp);
        
		var sbg:ASColor = tree.getSelectionBackground();
		if (sbg === undefined || sbg instanceof UIResource) {
			tree.setSelectionBackground(UIManager.getColor("Table.selectionBackground"));
		}

		var sfg:ASColor = tree.getSelectionForeground();
		if (sfg === undefined || sfg instanceof UIResource) {
			tree.setSelectionForeground(UIManager.getColor("Table.selectionForeground"));
		}
		setLeftChildIndent(10);
		setRightChildIndent(0);
		updateDepthOffset();
		treeState = new FixedHeightLayoutCache();
		treeState.setModel(tree.getModel());
		treeState.setSelectionModel(tree.getSelectionModel());
		treeState.setNodeDimensions(this);
		treeState.setRowHeight(tree.getRowHeight());
		editor = tree.getCellEditor();
		setRootVisible(tree.isRootVisible());
    }
    private function uninstallDefaults():Void {
        LookAndFeel.uninstallBorder(tree);
    }
    	
	private function installComponents():Void{
		rendererPane = new CellRendererPane();
		rendererPane.setLayout(new EmptyLayout());
		tree.append(rendererPane);
    }
	private function uninstallComponents():Void{
		tree.remove(rendererPane);
		cells.clear();
		rendererPane = null;
    }
    
    private function installListeners():Void{
    	treeListener = new Object();
    	treeListener[JTree.ON_TREE_EXPANDED] = Delegate.create(this, __treeExpanded);
    	treeListener[JTree.ON_TREE_COLLAPSED] = Delegate.create(this, __treeCollapsed);
    	treeListener[JTree.ON_STATE_CHANGED] = Delegate.create(this, __viewportStateChanged);
    	treeListener[JTree.ON_PROPERTY_CHANGED] = Delegate.create(this, __propertyChanged);
    	treeListener[JTree.ON_PRESS] = Delegate.create(this, __onPressed);
    	treeListener[JTree.ON_RELEASE] = Delegate.create(this, __onReleased);
    	treeListener[JTree.ON_CLICKED] = Delegate.create(this, __onClicked);
    	treeListener[JTree.ON_MOUSE_WHEEL] = Delegate.create(this, __onMouseWheel);
    	treeListener[JTree.ON_KEY_DOWN] = Delegate.create(this, __onKeyDown);
    	tree.addEventListener(treeListener);
    	
    	selectionModelListener = new Object();
    	selectionModelListener[DefaultTreeSelectionModel.ON_PROPERTY_CHANGED] = Delegate.create(this, __selectionModelPropertyChanged);
    	selectionModelListener[DefaultTreeSelectionModel.ON_SELECTION_CHANGED] = Delegate.create(this, __selectionChanged);
    	
    	setModel(tree.getModel());
    	setSelectionModel(tree.getSelectionModel());
    }
    private function uninstallListeners():Void{
    	tree.removeEventListener(treeListener);
    	treeModel.removeTreeModelListener(this);
    	selectionModel.removeEventListener(selectionModelListener);
    }
    
    private function setModel(tm:TreeModel):Void{
    	cancelEditing();
		if(treeModel != null){
		    treeModel.removeTreeModelListener(this);
		}
		treeModel = tm;
		if(treeModel != null) {
			treeModel.addTreeModelListener(this);
		}
		if(treeState != null) {
		    treeState.setModel(tm);
		    updateLayoutCacheExpandedNodes();
		    updateSize();
		}
    }
    
    private function setSelectionModel(sm:TreeSelectionModel):Void{
    	if(selectionModel != null && selectionModelListener != null){
    		selectionModel.removeEventListener(selectionModelListener);
    	}
    	selectionModel = sm;
    	if(selectionModel != null && selectionModelListener != null){
    		selectionModel.addEventListener(selectionModelListener);
    	}
		if(treeState != null){
		    treeState.setSelectionModel(selectionModel);
		}
		tree.repaint();
    }
    
    /**
     * Sets the root to being visible.
     */
    private function setRootVisible(newValue:Boolean):Void {
		cancelEditing();
		updateDepthOffset();
		if(treeState != null) {
		    treeState.setRootVisible(newValue);
		    treeState.invalidateSizes();
		    updateSize();
		}
    }    
    
    /**
     * Sets the row height, this is forwarded to the treeState.
     */
    private function setRowHeight(rowHeight:Number):Void {
		cancelEditing();
		if(treeState != null) {
		    treeState.setRowHeight(rowHeight);
		    updateSize();
		}
    }    
    
    private function setCellEditor(editor:TreeCellEditor):Void{
    	cancelEditing(tree);
    	this.editor = editor;
    }
    
    /**
     * Configures the receiver to allow, or not allow, editing.
     */
    private function setEditable(newValue:Boolean):Void {
    	cancelEditing(tree);
    	if(newValue){
    		editor = tree.getCellEditor();
    	}else{
    		editor = null;
    	}
    }    
    
    private function repaintPath(path:TreePath):Void {
    }
    
    private function cellFactoryChanged():Void{
    	for(var i:Number=cells.size()-1; i>=0; i--){
    		var cell:TreeCell = TreeCell(cells.get(i));
    		cell.getCellComponent().removeFromContainer();
    	}
    	cells.clear();
    	treeState.invalidateSizes();
		updateSize();
    }
    
    /**
     * Makes all the nodes that are expanded in JTree expanded in LayoutCache.
     * This invokes updateExpandedDescendants with the root path.
     */
    private function updateLayoutCacheExpandedNodes():Void {
		if(treeModel != null && treeModel.getRoot() != null){
		    updateExpandedDescendants(new TreePath([treeModel.getRoot()]));
		}
    }
        
    /**
     * Returns true if <code>mouseX</code> and <code>mouseY</code> fall
     * in the area of row that is used to expand/collapse the node and
     * the node at <code>row</code> does not represent a leaf.
     */
    private function isLocationInExpandControl(path:TreePath, mouseX:Number, mouseY:Number):Boolean {
		if(path != null && !treeModel.isLeaf(path.getLastPathComponent())){
		    var boxWidth:Number;
		   // var i:Insets = tree.getInsets();
			boxWidth = leftChildIndent;
	
		    var boxLeftX:Number = getRowX(tree.getRowForPath(path), path.getPathCount() - 1) - boxWidth;
	        //boxLeftX += i.left;
		    var boxRightX:Number = boxLeftX + boxWidth;
	
		    return mouseX >= boxLeftX && mouseX <= boxRightX;
		}
		return false;
    }    
    
    /**
     * Expands path if it is not expanded, or collapses row if it is expanded.
     * If expanding a path and JTree scrolls on expand, ensureRowsAreVisible
     * is invoked to scroll as many of the children to visible as possible
     * (tries to scroll to last visible descendant of path).
     */
    private function toggleExpandState(path:TreePath):Void {
		if(!tree.isExpanded(path)) {
		    var row:Number = getRowForPath(tree, path);
		    tree.expandPath(path);
		    updateSize();
		    if(row != -1) {
				if(tree.isScrollsOnExpand()){
				    ensureRowsAreVisible(row, row + treeState.getVisibleChildCount(path));
				}else{
				    ensureRowsAreVisible(row, row);
				}
		    }
		}else{
		    tree.collapsePath(path);
		    updateSize();
		}
    }    
    
    /**
      * Ensures that the rows identified by beginRow through endRow are
      * visible.
      */
    private function ensureRowsAreVisible(beginRow:Number, endRow:Number):Void {
		if(tree != null && beginRow >= 0 && endRow < getRowCount(tree)) {
			tree.scrollRowToVisible(endRow);
			tree.scrollRowToVisible(beginRow);
		}
    }    
    
    /**
     * Messaged when the user clicks the particular row, this invokes
     * toggleExpandState.
     */
    private function handleExpandControlClick(path:TreePath, mouseX:Number, mouseY:Number):Void {
		toggleExpandState(path);
    }    
    
    private function selectPathForEvent(path:TreePath):Void{
    	doSelectWhenRelease = false;
		pressedPath = path;
    	if(tree.isPathSelected(path)){
    		doSelectWhenRelease = true;
    	}else{
    		doSelectPathForEvent();
    	}
		paintFocusedIndex = tree.getRowForPath(path);
    }
    
    private function doSelectPathForEvent():Void{
    	var path:TreePath = pressedPath;
		if(isMultiSelectEvent()) {
		    var anchor:TreePath = tree.getAnchorSelectionPath();
		    var anchorRow:Number = (anchor == null) ? -1 : getRowForPath(tree, anchor);
	
		    if(anchorRow == -1 || selectionModel.getSelectionMode() == JTree.SINGLE_TREE_SELECTION) {
				tree.setSelectionPath(path);
		    }else {
				var row:Number = getRowForPath(tree, path);
				var lastAnchorPath:TreePath = anchor;
		
				if (isToggleSelectionEvent()) {
					if (tree.isRowSelected(anchorRow)) {
						tree.addSelectionInterval(anchorRow, row);
					} else {
						tree.removeSelectionInterval(anchorRow, row);
						tree.addSelectionInterval(row, row);
					}
				} else if(row < anchorRow) {
					tree.setSelectionInterval(row, anchorRow);
				} else {
					tree.setSelectionInterval(anchorRow, row);
				}
				ignoreLAChange = true;
				//lastSelectedRow = row;
				tree.setAnchorSelectionPath(lastAnchorPath);
				tree.setLeadSelectionPath(path);
				ignoreLAChange = false;
		    }
		}else if(isToggleSelectionEvent()) {
	        // Should this event toggle the selection of this row?
	        /* Control toggles just this node. */
            if(tree.isPathSelected(path)){
                tree.removeSelectionPath(path);
            }else{
                tree.addSelectionPath(path);
            }
            //lastSelectedRow = getRowForPath(tree, path);
			ignoreLAChange = true;
            tree.setAnchorSelectionPath(path);
            tree.setLeadSelectionPath(path);
			ignoreLAChange = false;
        }else{
			tree.setSelectionPath(path);
		}
    }
    
    private function isMultiSelectEvent():Boolean{
    	return Key.isDown(Key.SHIFT);
    }
    private function isToggleSelectionEvent():Boolean{
    	return Key.isDown(Key.CONTROL);
    }
    //------------------------------handlers------------------------
    private function __selectionModelPropertyChanged(source, pn:String, ov, nv):Void{
    	selectionModel.resetRowSelection();
    }
    private function __selectionChanged(source, event:TreeSelectionEvent):Void{
    	// Stop editing
	    stopEditing();
	    // Make sure all the paths are visible, if necessary.
	    if(tree.isExpandsSelectedPaths() && selectionModel != null) {
			var paths:Array = selectionModel.getSelectionPaths();
	
			if(paths != null) {
			    for(var counter:Number = paths.length - 1; counter >= 0; counter--) {
                    var path:TreePath = paths[counter].getParentPath();
                    var expand:Boolean = true;

                    while (path != null) {
                        // Indicates this path isn't valid anymore,
                        // we shouldn't attempt to expand it then.
                        if (treeModel.isLeaf(path.getLastPathComponent())){
                            expand = false;
                            path = null;
                        }else {
                            path = path.getParentPath();
                        }
                    }
                    if (expand) {
                        tree.makePathVisible(paths[counter]);
                    }
			    }
			}
	    }
		
    	paintFocusedIndex = tree.getMinSelectionRow();
	    var lead:TreePath = tree.getSelectionModel().getLeadSelectionPath();
	    ignoreLAChange = true;
	    tree.setAnchorSelectionPath(lead);
	    tree.setLeadSelectionPath(lead);
	    ignoreLAChange = false;
		tree.repaint();
    }
    
    private function __onClicked(source, clickCount:Number):Void{
    	var edit:Boolean = (tree.isEditable() && editor != null && editor.isCellEditable(clickCount));
    	var toggle:Boolean = (clickCount == tree.getToggleClickCount());
    	if(!(edit || toggle)){
    		return;
    	}
    	var p:Point = rendererPane.getMousePosition();
    	p.y += tree.getViewPosition().y;
		var path:TreePath = getClosestPathForLocation(tree, p.x, p.y);
		if(path != null){
			var bounds:Rectangle = getPathBounds(tree, path);
			if (p.x > bounds.x && p.x <= (bounds.x + bounds.width)) {
				if(edit){
					tree.startEditingAtPath(path);
				}else{
					toggleExpandState(path);
				}
			}
		}
    }
    
    private function __onPressed():Void{
    	var p:Point = rendererPane.getMousePosition();
    	p.y += tree.getViewPosition().y;
		var path:TreePath = getClosestPathForLocation(tree, p.x, p.y);
		if(path != null){
	    	if(isLocationInExpandControl(path, p.x, p.y)){
	    		handleExpandControlClick(path, p.x, p.y);
	    	}
		}
		var bounds:Rectangle = getPathBounds(tree, path);
		if (p.x > bounds.x && p.x <= (bounds.x + bounds.width)) {
		   selectPathForEvent(path);
		}
    }

    private var doSelectWhenRelease:Boolean;
    private var pressedPath:TreePath;
        
    private function __onReleased():Void{
    	if(doSelectWhenRelease){
			doSelectPathForEvent();
			doSelectWhenRelease = false;
    	}
    }
    
    private function __onMouseWheel(source, delta:Number):Void{
    	var pos:Point = tree.getViewPosition();
    	if(Key.isDown(Key.ALT)){
    		pos.x -= tree.getHorizontalUnitIncrement()*delta;
    	}else{
    		pos.y -= tree.getVerticalUnitIncrement()*delta;
    	}
    	tree.setViewPosition(pos);
    }
    
    private var paintFocusedIndex:Number;
    private function __onKeyDown():Void{
		if(!tree.isEnabled()){
			return;
		}
    	var code:Number = Key.getCode();
    	var dir:Number = 0;
    	if(isControlKey(code)){
	    	FocusManager.getCurrentManager().setTraversing(true);
    	}else{
    		return;
    	}
    	if(code == Key.UP){
    		dir = -1;
    	}else if(code == Key.DOWN){
    		dir = 1;
    	}
    	
    	if(paintFocusedIndex == undefined){
    		paintFocusedIndex = tree.getSelectionModel().getMinSelectionRow();
    	}
    	if(paintFocusedIndex < -1){
    		paintFocusedIndex = -1;
    	}else if(paintFocusedIndex > tree.getRowCount()){
    		paintFocusedIndex = tree.getRowCount();
    	}
    	var index:Number = paintFocusedIndex + dir;
    	if(code == Key.HOME){
    		index = 0;
    	}else if(code == Key.END){
    		index = tree.getRowCount() - 1;
    	}
    	if(index < 0 || index >= tree.getRowCount()){
    		return;
    	}
    	var path:TreePath = tree.getPathForRow(index);
    	if(code == Key.LEFT){
    		tree.collapseRow(index);
    	}else if(code == Key.RIGHT){
    		tree.expandRow(index);
    	}else if(dir != 0 || (code == Key.HOME || code == Key.END)){
    		if(isMultiSelectEvent()){
			    var anchor:TreePath = tree.getAnchorSelectionPath();
			    var anchorRow:Number = (anchor == null) ? -1 : getRowForPath(tree, anchor);
				var lastAnchorPath:TreePath = anchor;
				if(index < anchorRow) {
					tree.setSelectionInterval(index, anchorRow);
				} else {
					tree.setSelectionInterval(anchorRow, index);
				}
				ignoreLAChange = true;
				tree.setAnchorSelectionPath(lastAnchorPath);
				tree.setLeadSelectionPath(path);
				ignoreLAChange = false;
				
				paintFocusedIndex = index;
    		}else if(isToggleSelectionEvent()){
    			paintFocusedIndex = index;
    		}else{
		    	tree.setSelectionInterval(index, index);
    		}
    		tree.scrollRowToVisible(index);
    	}else{
    		if(code == Key.SPACE){
	            tree.addSelectionInterval(index, index);
		    	tree.scrollRowToVisible(index);
				ignoreLAChange = true;
	            tree.setAnchorSelectionPath(path);
	            tree.setLeadSelectionPath(path);
				ignoreLAChange = false;
    		}else if(code == getEditionKey()){
    			tree.startEditingAtPath(path);
    			return;
    		}
    	}
    	tree.repaint();
    }
    private function isControlKey(code:Number):Boolean{
    	return (code == Key.UP || code == Key.DOWN || code == Key.SPACE
    		|| code == Key.LEFT || code == Key.RIGHT || code == Key.HOME 
    		|| code == Key.END || code == getEditionKey());
    }
    
    private function getEditionKey():Number{
    	return Key.ENTER;
    }
    
	private function __viewportStateChanged():Void{
		var viewPosition:Point = tree.getViewPosition();
		if(!lastViewPosition.equals(viewPosition)){
			if(lastViewPosition.y == viewPosition.y){
				positRendererPaneX(viewPosition.x);
				lastViewPosition.setLocation(viewPosition);
				return;
			}
			tree.repaint();
		}
	}
	
	private function __propertyChanged(source, changeName:String, ov, nv):Void{
        if (changeName == JTree.LEAD_SELECTION_PATH_PROPERTY) {
		    if (!ignoreLAChange) {
				updateLeadRow();
				repaintPath(TreePath(ov));
				repaintPath(TreePath(nv));
		    }
		}else if (changeName == JTree.ANCHOR_SELECTION_PATH_PROPERTY) {
		    if (!ignoreLAChange) {
				repaintPath(TreePath(ov));
				repaintPath(TreePath(nv));
		    }
		}else if(changeName == JTree.CELL_FACTORY_PROPERTY) {
		    cellFactoryChanged();
		}else if(changeName == JTree.TREE_MODEL_PROPERTY) {
		    setModel(TreeModel(nv));
		}else if(changeName == JTree.ROOT_VISIBLE_PROPERTY) {
		    setRootVisible(nv == true);
        }else if(changeName == JTree.ROW_HEIGHT_PROPERTY) {
		    setRowHeight(Number(nv));
		}else if(changeName == JTree.CELL_EDITOR_PROPERTY) {
		    setCellEditor(TreeCellEditor(nv));
		}else if(changeName == JTree.EDITABLE_PROPERTY) {
		    setEditable(nv == true);
		}else if(changeName == JTree.SELECTION_MODEL_PROPERTY) {
		    setSelectionModel(tree.getSelectionModel());
		}else if(changeName == JTree.FONT_PROPERTY) {
		    cancelEditing();
		    if(treeState != null)
				treeState.invalidateSizes();
		    updateSize();
		}	
	}
	
	private function positRendererPaneX(viewX:Number):Void{
		rendererPane.setX(tree.getInsets().left - viewX);
		rendererPane.validate();
	}
    
    //---------------------------------------------------
    public function setLeftChildIndent(newAmount:Number):Void {
		leftChildIndent = newAmount;
		totalChildIndent = leftChildIndent + rightChildIndent;
		if(treeState != null)
		    treeState.invalidateSizes();
		updateSize();
    }
    public function getLeftChildIndent():Number {
		return leftChildIndent;
    }

    public function setRightChildIndent(newAmount:Number):Void {
		rightChildIndent = newAmount;
		totalChildIndent = leftChildIndent + rightChildIndent;
		if(treeState != null)
		    treeState.invalidateSizes();
		updateSize();
    }

    public function getRightChildIndent():Number {
		return rightChildIndent;
    }    
    
    /**
     * Updates how much each depth should be offset by.
     */
    private function updateDepthOffset():Void {
		if(tree.isRootVisible()) {
			depthOffset = 1;
		}else{
		    depthOffset = 0;
		}
    }    
    
    /**
     * Marks the cached size as being invalid, and messages the
     * tree with <code>treeDidChange</code>.
     */
    private function updateSize():Void {
		validCachedViewSize = false;
		tree.treeDidChange();
    }
    
    //**********************************************************************
    //                        Paint methods
    //**********************************************************************
    public function paintFocus(c:Component, g:Graphics):Void{
    	super.paintFocus(c, g);
    	
    	var b:Rectangle = treeState.getBounds(tree.getPathForRow(paintFocusedIndex));
    	b.setLocation(tree.getPixelLocationFromLogicLocation(b.getLocation()));
		
    	g.drawRectangle(new Pen(getDefaultFocusColorInner(), 1), b.x+0.5, b.y+0.5, b.width-1, b.height-1);
    	g.drawRectangle(new Pen(getDefaultFocusColorOutter(), 1), b.x+1.5, b.y+1.5, b.width-3, b.height-3);
    }    	
	public function paint(c:Component, g:Graphics, b:Rectangle):Void{
		super.paint(c, g, b);
		var viewSize:Dimension = getViewSize();
		rendererPane.setBounds(0, b.y, viewSize.width, b.height);
		checkCreateCells();
		var viewPosition:Point = tree.getViewPosition();
		lastViewPosition.setLocation(viewPosition);
		var x:Number = Math.round(viewPosition.x);
		var y:Number = Math.round(viewPosition.y);
		var ih:Number = tree.getRowHeight();
		var startIndex:Number = Math.floor(y/ih);
		var startY:Number = startIndex*ih - y;
		var rowCount:Number = getRowCount(tree);
		
		positRendererPaneX(x);
		
		var cx:Number = 0 - x;
		var cy:Number = startY;
		var maxY:Number = b.height;
		
		var showBounds:Rectangle = b.clone();
		showBounds.y = y;
		showBounds.x = x;
		var showRowCount:Number = Math.min(cells.size(), rowCount);
		var initialPath:TreePath = getClosestPathForLocation(tree, 0, showBounds.y);
		var paintingEnumerator:Array = treeState.getVisiblePathsFrom(initialPath, showRowCount);
		var row:Number = treeState.getRowContainingYLocation(showBounds.y);
		var endY:Number = showBounds.y + showBounds.height;
		
		var expanded:Boolean;
		var leaf:Boolean;
		var selected:Boolean;
		var bounds:Rectangle = new Rectangle();
		var boundsBuffer:Rectangle = new Rectangle();
		var treeModel:TreeModel = tree.getModel();
		g = rendererPane.clearGraphics();
		for(var i:Number = 0; i<cells.getSize(); i++){
			var cell:TreeCell = TreeCell(cells.get(i));
			var path:TreePath = paintingEnumerator[i];
			var cellCom:Component = cell.getCellComponent();
			if(i < paintingEnumerator.length){
			    leaf = treeModel.isLeaf(path.getLastPathComponent());
			    if(leaf){
					expanded = false;
			    }else {
					expanded = treeState.getExpandedState(path);
			    }
			    selected = tree.getSelectionModel().isPathSelected(path);
			    //trace("cell path = " + path);
			    bounds = treeState.getBounds(path, bounds);
				//trace("bounds : " + bounds);
				cell.setCellValue(path.getLastPathComponent());
				//trace(path.getLastPathComponent() + " cell value of " + cell);
				cellCom.setVisible(true);
				cell.setTreeCellStatus(tree, selected, expanded, leaf, row);
				boundsBuffer.setRect(bounds.x, cy, bounds.width, ih);
				cellCom.setBoundsImmediately(boundsBuffer);
				cellCom.validate();
				cellCom.paintImmediately();
				boundsBuffer.x += b.x;
				boundsBuffer.y += b.y;
				paintExpandControl(g, boundsBuffer, path, row, expanded, leaf);
				
				cy += ih;
				row++;
			}else{
				cellCom.setVisible(false);
				cellCom.validate();
			}
		}
	}
	
	private function paintExpandControl(g:Graphics, bounds:Rectangle, path:TreePath,
				      row:Number, expanded:Boolean, leaf:Boolean):Void{
		if(leaf){
			return;
		}
		var w:Number = totalChildIndent;
		var cx:Number = bounds.x - w/2;
		var cy:Number = bounds.y + bounds.height/2;
		var r:Number = 4;
		var trig:Array;
		if(!expanded){
			cx -= 2;
			trig = [new Point(cx, cy-r), new Point(cx, cy+r), new Point(cx+r, cy)];
		}else{
			cy -= 2;
			trig = [new Point(cx-r, cy), new Point(cx+r, cy), new Point(cx, cy+r)];
		}
		g.fillPolygon(new SolidBrush(ASColor.BLACK), trig);
	}
	
	private function checkCreateCells():Void{
		var ih:Number = tree.getRowHeight();
		var needNum:Number = Math.floor(tree.getExtentSize().height/ih) + 2;
		
		if(cells.getSize() == needNum/* || !displayable*/){
			return;
		}
		
		//create needed
		if(cells.getSize() < needNum){
			var addNum:Number = needNum - cells.getSize();
			for(var i:Number=0; i<addNum; i++){
				var cell:TreeCell = tree.getCellFactory().createNewCell();
				rendererPane.append(cell.getCellComponent());
				cells.append(cell);
			}
		}else if(cells.getSize() > needNum){ //remove mored
			var removeIndex:Number = needNum;
			var removed:Array = cells.removeRange(removeIndex, cells.getSize()-1);
			for(var i:Number=0; i<removed.length; i++){
				var cell:TreeCell = TreeCell(removed[i]);
				rendererPane.remove(cell.getCellComponent());
			}
		}
	}
	
	//---------------------------------------
    private function updateLeadRow():Void {
		paintFocusedIndex = getRowForPath(tree, tree.getLeadSelectionPath());
    }	
    
    /**
     * Returns the location, along the x-axis, to render a particular row
     * at. The return value does not include any Insets specified on the JTree.
     * This does not check for the validity of the row or depth, it is assumed
     * to be correct and will not throw an Exception if the row or depth
     * doesn't match that of the tree.
     *
     * @param row Row to return x location for
     * @param depth Depth of the row
     * @return amount to indent the given row.
     */
    private function getRowX(row:Number, depth:Number):Number {
        return totalChildIndent * (depth + depthOffset);
    }
    
    /**
     * Updates the expanded state of all the descendants of <code>path</code>
     * by getting the expanded descendants from the tree and forwarding
     * to the tree state.
     */
    private function updateExpandedDescendants(path:TreePath):Void {
		//completeEditing();
		if(treeState != null) {
		    treeState.setExpandedState(path, true);
	
		    var descendants:Array = tree.getExpandedDescendants(path);
	
		    if(descendants != null) {
				for(var i:Number=0; i<descendants.length; i++){
				    treeState.setExpandedState(TreePath(descendants[i]), true);
				}
		    }
		    updateLeadRow();
		    updateSize();
		}
    }    
	
    //**********************************************************************
    //                        NodeDimensions methods
    //**********************************************************************
	private var currentCellRenderer:TreeCell;
	public function countNodeDimensions(value : Object, row : Number, depth : Number, expanded : Boolean, size : Rectangle) : Rectangle {
		var prefSize:Dimension;
		if(tree.getFixedCellWidth() >= 0){
			prefSize = new Dimension(tree.getFixedCellWidth(), tree.getRowHeight());
		}else{
		    if(currentCellRenderer == null){
		    	currentCellRenderer = tree.getCellFactory().createNewCell();
		    }
		    currentCellRenderer.setCellValue(value);
		    currentCellRenderer.setTreeCellStatus(tree, /*selected*/false, expanded, tree.getModel().isLeaf(value), row);
	
			prefSize = currentCellRenderer.getCellComponent().getPreferredSize();
		}
		if(size != null) {
		    size.x = getRowX(row, depth);
		    size.width = prefSize.width;
		    size.height = prefSize.height;
		}else {
		    size = new Rectangle(getRowX(row, depth), 0,
					 prefSize.width, prefSize.height);
		}
		return size;
	}
    //**********************************************************************
    //                        TreeUI methods
    //**********************************************************************

    /**
      * Returns the Rectangle enclosing the label portion that the
      * last item in path will be drawn into.  Will return null if
      * any component in path is currently valid.
      */
    public function getPathBounds(tree:JTree, path:TreePath):Rectangle {
		if(tree != null && treeState != null) {
		    var i:Insets = tree.getInsets();
		    var bounds:Rectangle = treeState.getBounds(path, null);
	
		    if(bounds != null && i != null) {
				bounds.x += i.left;
				bounds.y += i.top;
		    }
		    return bounds;
		}
		return null;
    }

    /**
      * Returns the path for passed in row.  If row is not visible
      * null is returned.
      */
    public function getPathForRow(tree:JTree, row:Number):TreePath {
		return (treeState != null) ? treeState.getPathForRow(row) : null;
    }

    /**
      * Returns the row that the last item identified in path is visible
      * at.  Will return -1 if any of the elements in path are not
      * currently visible.
      */
    public function getRowForPath(tree:JTree, path:TreePath):Number {
		return (treeState != null) ? treeState.getRowForPath(path) : -1;
    }

    /**
      * Returns the number of rows that are being displayed.
      */
    public function getRowCount(tree:JTree):Number {
		return (treeState != null) ? treeState.getRowCount() : 0;
    }

    /**
      * Returns the path to the node that is closest to x,y.  If
      * there is nothing currently visible this will return null, otherwise
      * it'll always return a valid path.  If you need to test if the
      * returned object is exactly at x, y you should get the bounds for
      * the returned path and test x, y against that.
      */
    public function getClosestPathForLocation(tree:JTree, x:Number, y:Number):TreePath {
		if(tree != null && treeState != null) {
		    var i:Insets = tree.getInsets();
	
		    if(i == null){
				i = EMPTY_INSETS;
		    }
	
		    return treeState.getPathClosestTo(x - i.left, y - i.top);
		}
		return null;
    }
    
    /**
     * Returns the treePath that the user mouse pointed, null if no path was pointed.
     */
    public function getMousePointedPath():TreePath{
    	var p:Point = rendererPane.getMousePosition();
    	p.y += tree.getViewPosition().y;
		var path:TreePath = getClosestPathForLocation(tree, p.x, p.y);
		return path;
    }

    /**
      * Returns true if the tree is being edited.  The item that is being
      * edited can be returned by getEditingPath().
      */
    public function isEditing(tree:JTree):Boolean {
    	return editor.isCellEditing();
    }

    /**
      * Stops the current editing session.  This has no effect if the
      * tree isn't being edited.  Returns true if the editor allows the
      * editing session to stop.
      */
    public function stopEditing(tree:JTree):Boolean {
		if(editor != null && editor.isCellEditing()) {
		    return editor.stopCellEditing();
		}
		return false;
    }

    /**
      * Cancels the current editing session.
      */
    public function cancelEditing(tree:JTree):Void {
		if(editor != null && editor.isCellEditing()) {
		    editor.cancelCellEditing();
		}
    }

    /**
      * Selects the last item in path and tries to edit it.  Editing will
      * fail if the CellEditor won't allow it for the selected item.
      * 
      * @return true is started sucessful, editing fail
      */
    public function startEditingAtPath(tree:JTree, path:TreePath):Boolean {
    	if(editor == null){
    		return false;
    	}
		tree.scrollPathToVisible(path);
		if(path != null && tree.isPathVisible(path)){
			var editor:TreeCellEditor = tree.getCellEditor();
			if (editor.isCellEditing()){
				if(!editor.stopCellEditing()){
					return false;
				}
			}
			editingPath = path;
			var bounds:Rectangle = tree.getPathBounds(path);
			bounds.setLocation(tree.getPixelLocationFromLogicLocation(bounds.getLocation()));
			editor.startCellEditing(tree, path.getLastPathComponent(), bounds);
			return true;
		}
		return false;
    }
    private var editingPath:TreePath;

    /**
     * Returns the path to the element that is being edited.
     */
    public function getEditingPath(tree:JTree):TreePath {
		return editingPath;
    }
    
	//******************************************************************
	//	                         Size Methods
	//******************************************************************
	
	private function updateCachedViewSize():Void{
		if(treeState != null) {
			viewSize.width = treeState.getPreferredWidth(null);
		    viewSize.height = treeState.getPreferredHeight();
		}
		validCachedViewSize = true;
	}
	
	public function getMinimumSize(c:Component):Dimension {
		return c.getInsets().getOutsideSize();
	}

	public function getPreferredSize(c:Component):Dimension {
		var height:Number =  tree.getVisibleRowCount() * tree.getRowHeight();
		var width:Number = getViewSize(tree).width;
		return c.getInsets().getOutsideSize(new Dimension(width, height));
	}
	
    public function getViewSize(theTree:JTree):Dimension {
		if(!validCachedViewSize){
		    updateCachedViewSize();
		}
		if(tree != null) {
		    return new Dimension(viewSize.width, viewSize.height);
		}else{
		    return new Dimension(0, 0);
		}
    }

	public function getMaximumSize(c:Component):Dimension {
		return new Dimension(Number.MAX_VALUE, Number.MAX_VALUE);
	}
        //
        // TreeExpansionListener
        //
	public function __treeExpanded(source:JTree, path:TreePath):Void {
	    if(path != null) {
			updateExpandedDescendants(path);
	    }
	}

	public function __treeCollapsed(source:JTree, path:TreePath):Void {
	    if(path != null) {
			//completeEditing();
			if(path != null && tree.isPathVisible(path)) {
			    treeState.setExpandedState(path, false);
			    updateLeadRow();
			    updateSize();
			}
	    }
	}	

	//******************************************************************
	//	                         TreeModelListener Methods
	//******************************************************************
	
	public function treeNodesChanged(e : TreeModelEvent) : Void {
	    if(treeState != null && e != null) {
			treeState.treeNodesChanged(e);
	
			var pPath:TreePath = e.getTreePath().getParentPath();
	
			if(pPath == null || treeState.isExpanded(pPath)){
			    updateSize();
			}
	    }
	}

	public function treeNodesInserted(e : TreeModelEvent) : Void {
	    if(treeState != null && e != null) {
			treeState.treeNodesInserted(e);
	
			updateLeadRow();
	
			var path:TreePath = e.getTreePath();
	
			if(treeState.isExpanded(path)) {
			    updateSize();
			}else {
			    // PENDING(sky): Need a method in TreeModelEvent
			    // that can return the count, getChildIndices allocs
			    // a new array!
			    var indices:Array = e.getChildIndices();
			    var childCount:Number = tree.getModel().getChildCount(path.getLastPathComponent());
	
			    if(indices != null && (childCount - indices.length) == 0){
					updateSize();
			    }
			}
	    }
	}

	public function treeNodesRemoved(e : TreeModelEvent) : Void {
	    if(treeState != null && e != null) {
			treeState.treeNodesRemoved(e);
	
			updateLeadRow();
	
			var path:TreePath = e.getTreePath();
	
			if(treeState.isExpanded(path) || tree.getModel().getChildCount(path.getLastPathComponent()) == 0){
			    updateSize();
			}
	    }
	}

	public function treeStructureChanged(e : TreeModelEvent) : Void {
	    if(treeState != null && e != null) {
			treeState.treeStructureChanged(e);
	
			updateLeadRow();
	
			var pPath:TreePath = e.getTreePath();
	
	        if (pPath != null) {
	            pPath = pPath.getParentPath();
	        }
	        if(pPath == null || treeState.isExpanded(pPath)){
	            updateSize();
	        }
	    }
	}
	
	public function toString():String{
		return "BasicTreeUI[]";
	}
}