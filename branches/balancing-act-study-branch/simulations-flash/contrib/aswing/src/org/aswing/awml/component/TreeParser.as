/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.component.ComponentParser;
import org.aswing.awml.component.tree.TreeRootParser;
import org.aswing.overflow.JTree;
import org.aswing.tree.DefaultMutableTreeNode;
import org.aswing.tree.DefaultTreeModel;
import org.aswing.tree.TreePath;

/**
 * Parses {@link org.aswing.overflow.JTree} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.TreeParser extends ComponentParser {
    
	private static var ATTR_HORIZONTAL_BLOCK_INCREMENT:String = "horizontal-block-increment";
	private static var ATTR_VERTICAL_BLOCK_INCREMENT:String = "vertical-block-increment";
	private static var ATTR_HORIZONTAL_UNIT_INCREMENT:String = "horizontal-unit-increment";
	private static var ATTR_VERTICAL_UNIT_INCREMENT:String = "vertical-unit-increment";
    private static var ATTR_EDITABLE:String = "editable";
    private static var ATTR_EXPAND_SELECTED_PATHS:String = "expand-selected-paths";
    private static var ATTR_FIXED_CELL_WIDTH:String = "fixed-cell-width";
    private static var ATTR_INVOKE_STOP_CELL_EDITING:String = "invoke-stop-cell-editing";
    private static var ATTR_ROOT_VISIBLE:String = "root-visible";
    private static var ATTR_ROW_HEIGHT:String = "row-height";
    private static var ATTR_SCROLL_ON_EXPAND:String = "scroll-on-expand";
    private static var ATTR_TOGGLE_CLICK_COUNT:String = "toggle-click-count";
    private static var ATTR_VISIBLE_ROW_COUNT:String = "visible-row-count";
    private static var ATTR_SELECTION_MODE:String = "selection-mode";
    private static var ATTR_SELECTED_ROW:String = "selected-row";
    private static var ATTR_SELECTED_ROWS:String = "selected-rows";
    
    private static var ATTR_ON_SELECTION_CHANGED:String = "on-selection-chenged";
    private static var ATTR_ON_PROPERTY_CHANGED:String = "on-property-chenged";
    private static var ATTR_ON_EXPANDED:String = "on-expanded";
    private static var ATTR_ON_COLLAPSED:String = "on-collapsed";
    private static var ATTR_ON_WILL_EXPAND:String = "on-will-expand";
    private static var ATTR_ON_WILL_COLLAPSE:String = "on-will-collapse";
    
    private static var MODE_SINGLE:String = "single";
    private static var MODE_CONTIGUOUS:String = "contiguous";
    private static var MODE_DISCONTIGUOUS:String = "discontiguous";
    
    
    /**
     * Constructor.
     */
    public function TreeParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, tree:JTree, namespace:AwmlNamespace) {
        
        tree = super.parse(awml, tree, namespace);
        
        // init selection mode
        var mode:String = getAttributeAsString(awml, ATTR_SELECTION_MODE, null);
        switch (mode) {
            case MODE_SINGLE:
                tree.setSelectionMode(JTree.SINGLE_TREE_SELECTION);
                break;  
            case MODE_CONTIGUOUS:
                tree.setSelectionMode(JTree.CONTIGUOUS_TREE_SELECTION);
                break;  
            case MODE_CONTIGUOUS:
                tree.setSelectionMode(JTree.CONTIGUOUS_TREE_SELECTION);
                break;  
        }
        
		// init block increments
		tree.setHorizontalBlockIncrement(getAttributeAsNumber(awml, ATTR_HORIZONTAL_BLOCK_INCREMENT, tree.getHorizontalBlockIncrement()));
		tree.setVerticalBlockIncrement(getAttributeAsNumber(awml, ATTR_VERTICAL_BLOCK_INCREMENT, tree.getVerticalBlockIncrement()));
		tree.setHorizontalUnitIncrement(getAttributeAsNumber(awml, ATTR_HORIZONTAL_UNIT_INCREMENT, tree.getHorizontalUnitIncrement()));
		tree.setVerticalUnitIncrement(getAttributeAsNumber(awml, ATTR_VERTICAL_UNIT_INCREMENT, tree.getVerticalUnitIncrement()));
        
        // init editable
        tree.setEditable(getAttributeAsBoolean(awml, ATTR_EDITABLE, tree.isEditable()));
        tree.setInvokesStopCellEditing(getAttributeAsBoolean(awml, ATTR_INVOKE_STOP_CELL_EDITING, tree.isInvokesStopCellEditing()));

        // init expand selected path
        tree.setExpandsSelectedPaths(getAttributeAsBoolean(awml, ATTR_EXPAND_SELECTED_PATHS, tree.isExpandsSelectedPaths()));
        
        // init scroll on expand
        tree.setScrollsOnExpand(getAttributeAsBoolean(awml, ATTR_SCROLL_ON_EXPAND, tree.isScrollsOnExpand()));
        
        // init toggle click count
        tree.setToggleClickCount(getAttributeAsNumber(awml, ATTR_TOGGLE_CLICK_COUNT, tree.getToggleClickCount()));
        
        // init root visible
        tree.setRootVisible(getAttributeAsBoolean(awml, ATTR_ROOT_VISIBLE, tree.isRootVisible()));
        
        // init widths and heights
        tree.setVisibleRowCount(getAttributeAsNumber(awml, ATTR_VISIBLE_ROW_COUNT, tree.getVisibleRowCount()));
        tree.setRowHeight(getAttributeAsNumber(awml, ATTR_ROW_HEIGHT, tree.getRowHeight()));
        tree.setFixedCellWidth(getAttributeAsNumber(awml, ATTR_FIXED_CELL_WIDTH, tree.getFixedCellWidth()));
        
        // init selected rows
        var selectedRow:Number = getAttributeAsNumber(awml, ATTR_SELECTED_ROW, null);
        if (selectedRow != null) tree.setSelectionRow(selectedRow);
        tree.setSelectionRows(getAttributeAsArray(awml, ATTR_SELECTED_ROWS, tree.getSelectionRows()));
        
        // init events
        attachEventListeners(tree, JTree.ON_STATE_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_STATE_CHANGED));
        attachEventListeners(tree, JTree.ON_SELECTION_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_SELECTION_CHANGED));
        attachEventListeners(tree, JTree.ON_PROPERTY_CHANGED, getAttributeAsEventListenerInfos(awml, ATTR_ON_PROPERTY_CHANGED));
        attachEventListeners(tree, JTree.ON_TREE_EXPANDED, getAttributeAsEventListenerInfos(awml, ATTR_ON_EXPANDED));
        attachEventListeners(tree, JTree.ON_TREE_COLLAPSED, getAttributeAsEventListenerInfos(awml, ATTR_ON_COLLAPSED));
        attachEventListeners(tree, JTree.ON_TREE_WILL_EXPAND, getAttributeAsEventListenerInfos(awml, ATTR_ON_WILL_EXPAND));
        attachEventListeners(tree, JTree.ON_TREE_WILL_COLLAPSE, getAttributeAsEventListenerInfos(awml, ATTR_ON_WILL_COLLAPSE));
        
        return tree;
    }
    
    private function parseChild(awml:XMLNode, nodeName:String, tree:JTree, namespace:AwmlNamespace):Void {

        super.parseChild(awml, nodeName, tree, namespace);
        
        switch (nodeName) {
            case AwmlConstants.NODE_SELECTION_BACKGROUND:
                var color:ASColor = AwmlParser.parse(awml);
                if (color != null) tree.setSelectionBackground(color);
                break;
            case AwmlConstants.NODE_SELECTION_FOREGROUND:
                var color:ASColor = AwmlParser.parse(awml);
                if (color != null) tree.setSelectionForeground(color);
                break;
            case AwmlConstants.NODE_TREE_ROOT:
                var root:DefaultMutableTreeNode = AwmlParser.parse(awml);
                if (root != null) {
                    tree.setModel(new DefaultTreeModel(root));

                    // init expanded
                    for (var i = 0; i < TreeRootParser.getExpandedNodes().length; i++) {
                        var path:Array = (DefaultMutableTreeNode(TreeRootParser.getExpandedNodes()[i])).getPath();
                        tree.expandPath(new TreePath(path));
                    }
                    
                    // init selection
                    for (var i = 0; i < TreeRootParser.getSelectedNodes().length; i++) {
                        var path:Array = (DefaultMutableTreeNode(TreeRootParser.getSelectedNodes()[i])).getPath();
                        tree.addSelectionPath(new TreePath(path));
                    }
                }   
                break;
        }
    }
    
    private function getClass(Void):Function {
    	return JTree;	
    }
    
}
