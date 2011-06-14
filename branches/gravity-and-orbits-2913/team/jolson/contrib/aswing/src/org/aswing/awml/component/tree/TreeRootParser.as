/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlParser;
import org.aswing.tree.DefaultMutableTreeNode;

/**
 *  Parses root tree node.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.tree.TreeRootParser extends AbstractParser {
	
	private static var selectedNodes:Array;
	private static var expandedNodes:Array;
	
	public static function addSelectedNode(node:DefaultMutableTreeNode):Void {
		if (selectedNodes == null) resetSelectedNodes();
		selectedNodes.push(node);
	} 

	public static function getSelectedNodes(Void):Array {
		return selectedNodes; 	
	} 

	private static function resetSelectedNodes(Void):Void {
		selectedNodes = new Array();
	} 

	public static function addExpandedNode(node:DefaultMutableTreeNode):Void {
		if (expandedNodes == null) resetExpandedNodes();
		expandedNodes.push(node);
	} 

	public static function getExpandedNodes(Void):Array {
		return expandedNodes; 	
	} 

	private static function resetExpandedNodes(Void):Void {
		expandedNodes = new Array();
	} 
	
	/**
	 * Constructor.
	 */
	public function TreeRootParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode):DefaultMutableTreeNode {
		
		var root:DefaultMutableTreeNode = new DefaultMutableTreeNode("Root");
		
		resetSelectedNodes();
		resetExpandedNodes();
			
		super.parse(awml, root);
	
		return root;
	}

	private function parseChild(awml:XMLNode, nodeName:String, root:DefaultMutableTreeNode):Void {

		super.parseChild(awml, nodeName, root);
		
		switch (nodeName) {
			case AwmlConstants.NODE_TREE_NODE:
				var node:DefaultMutableTreeNode = AwmlParser.parse(awml);
				if (node != null) root.append(node);
				break;
		}
	}

}