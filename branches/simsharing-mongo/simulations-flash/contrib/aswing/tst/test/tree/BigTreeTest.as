/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.BorderLayout;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JPanel;
import org.aswing.JScrollPane;
import org.aswing.JTree;
import org.aswing.tree.DefaultMutableTreeNode;
import org.aswing.tree.DefaultTreeModel;

import test.tree.Item;

/**
 * For a big model tree, there's two way to speed up the performance.
 * <br>
 * 1. Make all model item implemented Identifiable.<br>
 * 2. call JTree.setFixedCellWidth(width:Number) to fix the cell width.
 * 
 * @author iiley
 */
class test.tree.BigTreeTest extends JFrame {
	
	private var tree:JTree;
	
	public function BigTreeTest() {
		super("TreeTest");
		var pane:JPanel = new JPanel(new BorderLayout());
		

        var root:DefaultMutableTreeNode = new DefaultMutableTreeNode(createItem("JTree"));
		var parent:DefaultMutableTreeNode;
	
		parent = new DefaultMutableTreeNode(createItem("folder1"));
		root.append(parent);
		for(var i:Number=0; i<100; i++){
			parent.append(new DefaultMutableTreeNode(createItem("item one " + i)));
		}
		
		parent = new DefaultMutableTreeNode(createItem("folder2"));
		root.append(parent);
		for(var i:Number=0; i<500; i++){
			parent.append(new DefaultMutableTreeNode(createItem("item two " + i)));
		}

		parent = new DefaultMutableTreeNode(createItem("folder3"));
		root.append(parent);
		for(var i:Number=0; i<100; i++){
			parent.append(new DefaultMutableTreeNode(createItem("item three " + i)));
		}
				
        var model:DefaultTreeModel = new DefaultTreeModel(root);		
		
		tree = new JTree();
		tree.setModel(model);
		tree.setFixedCellWidth(100);
		pane.append(new JScrollPane(tree), BorderLayout.CENTER);
		var button:JButton = new JButton("Expand");
		pane.append(button, BorderLayout.SOUTH);
		setContentPane(pane);
		button.addActionListener(__repaintTree, this);
	}
	
	private function createItem(value:String):Item{
		return new Item(value);
	}
	
	private function __repaintTree():Void{
		tree.expandPath(tree.getPathForRow(1));
		tree.expandPath(tree.getPathForRow(0));
	}
	
	public static function main():Void {
		try{
			var myWindow1:BigTreeTest = new BigTreeTest ();
			myWindow1.setLocation (0, 0);
			myWindow1.setSize (200, 200);
			myWindow1.show();
		}catch(e:Error){
			trace("Catched a error : " + e);
		}
	}
}