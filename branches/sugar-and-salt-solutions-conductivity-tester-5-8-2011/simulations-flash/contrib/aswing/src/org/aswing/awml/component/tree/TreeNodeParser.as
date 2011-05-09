/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AbstractParser;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.component.tree.TreeRootParser;
import org.aswing.tree.DefaultMutableTreeNode;

/**
 *  Parses tree node.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.tree.TreeNodeParser extends AbstractParser {
	
	private static var ATTR_VALUE:String = "value";
	private static var ATTR_TYPE:String = "type";
	private static var ATTR_SELECTED:String = "selected";
	private static var ATTR_EXPANDED:String = "expanded";
	
	private static var TYPE_STRING:String = "string";
	private static var TYPE_NUMBER:String = "number";
	private static var TYPE_BOOLEAN:String = "boolean";
	
	private static var DEFAULT_TYPE:String = TYPE_STRING;
	
	/**
	 * Constructor.
	 */
	public function TreeNodeParser(Void) {
		super();
	}
	
	public function parse(awml:XMLNode):DefaultMutableTreeNode {
		
		// init node
		var node:DefaultMutableTreeNode = new DefaultMutableTreeNode();

		// get type
		var type:String = getAttributeAsString(awml, ATTR_TYPE, DEFAULT_TYPE);
		
		// init value
		switch (type) {
			case TYPE_STRING:
				node.setUserObject(getAttributeAsString(awml, ATTR_VALUE));
				break;
			case TYPE_NUMBER:
				node.setUserObject(getAttributeAsNumber(awml, ATTR_VALUE));
				break;
			case TYPE_BOOLEAN:
				node.setUserObject(getAttributeAsBoolean(awml, ATTR_VALUE));
				break;
		}

		// parse selection 
		if (getAttributeAsBoolean(awml, ATTR_SELECTED, false)) {
			TreeRootParser.addSelectedNode(node);
		}

		// parse expanded 
		if (getAttributeAsBoolean(awml, ATTR_EXPANDED, false)) {
			TreeRootParser.addExpandedNode(node);
		}

		super.parse(awml, node);
	
		return node;
	}

	private function parseChild(awml:XMLNode, nodeName:String, node:DefaultMutableTreeNode):Void {

		super.parseChild(awml, nodeName, node);
		
		switch (nodeName) {
			case AwmlConstants.NODE_TREE_NODE:
				var child:DefaultMutableTreeNode = AwmlParser.parse(awml, node);
				if (child != null) node.append(child);
				break;
		}
	}

}