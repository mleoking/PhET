package org.aswing.tree { 
/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.tree.AbstractLayoutCache;
import org.aswing.tree.FHTreeStateNode;
import org.aswing.tree.TreePath;

/**
 * @author iiley
 */
public class SearchInfo {
	public var node:FHTreeStateNode;
	public var isNodeParentNode:Boolean;
	public var childIndex:Number;
	private var layoutCatch:AbstractLayoutCache;
	
	public function SearchInfo(layoutCatch:AbstractLayoutCache){
		this.layoutCatch = layoutCatch;
	}

	public function getPath():TreePath {
	    if(node == null){
			return null;
	    }

	    if(isNodeParentNode){
			return node.getTreePath().pathByAddingChild(layoutCatch.getModel().getChild(node.getUserObject(),
						     childIndex));
	    }
	    return node.getTreePath();
	}	
}
}