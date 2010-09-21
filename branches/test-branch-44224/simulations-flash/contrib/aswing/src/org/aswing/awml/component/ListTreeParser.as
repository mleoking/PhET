/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.component.ListParser;
import org.aswing.awml.component.tree.TreeRootParser;
import org.aswing.overflow.JListTree;
import org.aswing.tree.DefaultMutableTreeNode;
import org.aswing.tree.DefaultTreeModel;
import org.aswing.tree.TreePath;
import org.aswing.VectorListModel;

/**
 * Parses {@link org.aswing.overflow.JListTree} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.ListTreeParser extends ListParser {
    
    private static var ATTR_ROOT_VISIBLE:String = "root-visible";
    private static var ATTR_TOGGLE_CLICK_COUNT:String = "toggle-click-count";
    private static var ATTR_SELECTED_ROW:String = "selected-row";
    private static var ATTR_SELECTED_ROWS:String = "selected-rows";
    
    /**
     * Constructor.
     */
    public function ListTreeParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, listTree:JListTree, namespace:AwmlNamespace) {
        
        listTree = super.parse(awml, listTree, namespace);
                
        // init toggle click count
        listTree.setToggleClickCount(getAttributeAsNumber(awml, ATTR_TOGGLE_CLICK_COUNT, listTree.getToggleClickCount()));
        
        // init root visible
        listTree.setRootVisible(getAttributeAsBoolean(awml, ATTR_ROOT_VISIBLE, listTree.isRootVisible()));
        
        // init selected rows
        var selectedRow:Number = getAttributeAsNumber(awml, ATTR_SELECTED_ROW, null);
        if (selectedRow != null) listTree.setSelectionRow(selectedRow);
        listTree.setSelectionRows(getAttributeAsArray(awml, ATTR_SELECTED_ROWS, listTree.getSelectionRows()));
        
        return listTree;
    }
    
    private function parseChild(awml:XMLNode, nodeName:String, treeList:JListTree, namespace:AwmlNamespace):Void {
        super.parseChild(awml, nodeName, treeList, namespace);
    }
    
    private function parseChildItem(awml:XMLNode, nodeName:String, treeList:JListTree):Void {
        switch (nodeName) {
            case AwmlConstants.NODE_TREE_ROOT:
                var root:DefaultMutableTreeNode = AwmlParser.parse(awml);
                if (root != null) {
                    treeList.setTreeModel(new DefaultTreeModel(root));

                    // init expanded
                    for (var i = 0; i < TreeRootParser.getExpandedNodes().length; i++) {
                        var path:Array = (DefaultMutableTreeNode(TreeRootParser.getExpandedNodes()[i])).getPath();
                        treeList.setExpandState(new TreePath(path), true);
                    }
                    
                    // init selection
                    for (var i = 0; i < TreeRootParser.getSelectedNodes().length; i++) {
                        var path:Array = (DefaultMutableTreeNode(TreeRootParser.getSelectedNodes()[i])).getPath();
                        treeList.addSelectionPath(new TreePath(path));
                    }
                }   
                break;
			case AwmlConstants.NODE_LIST_TREE_ITEMS:
				var collection:Array = AwmlParser.parse(awml);
				if (collection != null) treeList.setModel(new VectorListModel(collection)); 
				break;
        }
    }
    
    private function getClass(Void):Function {
    	return JListTree;	
    }
    
}
