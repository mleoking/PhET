/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.border.TitledBorder;
import org.aswing.BoxLayout;
import org.aswing.Container;
import org.aswing.GeneralListCellFactory;
import org.aswing.JFrame;
import org.aswing.JListTree;
import org.aswing.JScrollPane;
import org.aswing.JTree;
import org.aswing.tree.DefaultMutableTreeNode;
import org.aswing.tree.DefaultTreeModel;
import org.aswing.tree.TreeModel;

import test.tree.IconListTreeCell;

/**
 * @author iiley
 */
class test.tree.JListTreeVSJTree extends JFrame {
	
	public function JListTreeVSJTree() {
		super("JListTree VS JTree");
		
		var model:TreeModel = getDefaultTreeModel();
		
		var tree:JTree = new JTree(model);
		tree.setRowHeight(20);
		tree.setRootVisible(false);
		
		var listTree:JListTree = new JListTree(
			model,
			new GeneralListCellFactory(IconListTreeCell, true, true, 20)
		);
		listTree.setOpaque(true);
		listTree.setRootVisible(false);
		
		var scrollPane1:JScrollPane = new JScrollPane(tree);
		scrollPane1.setBorder(new TitledBorder(null, "JTree"));
		var scrollPane2:JScrollPane = new JScrollPane(listTree);
		scrollPane2.setBorder(new TitledBorder(null, "JListTree"));
		
		var pane:Container = getContentPane();
		pane.setLayout(new BoxLayout(BoxLayout.X_AXIS));
		pane.append(scrollPane1);
		pane.append(scrollPane2);
	}

	
	public static function main():Void {
		Stage.scaleMode = "noScale";
		Stage.align = "TL";
		try{
			var myWindow1:JListTreeVSJTree = new JListTreeVSJTree ();
			myWindow1.setLocation (0, 0);
			myWindow1.setSize (400, 400);
			myWindow1.show();
		}catch(e:Error){
			trace("Catched a error : " + e);
		}
	}
	
	private static function getDefaultTreeModel():TreeModel {
        var root:DefaultMutableTreeNode = new DefaultMutableTreeNode("Root");
		addChildren(root, 3, 4);
        return new DefaultTreeModel(root);
    }
    
    private static function addChildren(node:DefaultMutableTreeNode, level:Number, n:Number):Void{
    	if(level > 0){
	    	for(var i:Number=0; i<n; i++){
	    		var sn:DefaultMutableTreeNode = new DefaultMutableTreeNode("This is a Tree Node " + level + "_" + i);
	    		node.append(sn);
	    		addChildren(sn, level-1, n*4);
	    	}
    	}
    }
}