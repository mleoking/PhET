/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.BorderLayout;
import org.aswing.GeneralListCellFactory;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JListTree;
import org.aswing.JPanel;
import org.aswing.JScrollPane;
import org.aswing.tree.DefaultMutableTreeNode;
import org.aswing.tree.DefaultTreeModel;
import org.aswing.tree.list.DefaultListTreeCell;
import org.aswing.tree.TreeModel;

/**
 * @author iiley
 */
class test.tree.ListTreeWithCellTipTest extends JFrame{
		
	private var tree:JListTree;
	
	public function ListTreeWithCellTipTest() {
		super("ListTreeWithCellTipTest");
		var pane:JPanel = new JPanel(new BorderLayout());
		tree = new JListTree(
			getDefaultTreeModel(), 
			//new GeneralListCellFactory(DefaultListTreeCellWithTip, true, true));
			new GeneralListCellFactory(DefaultListTreeCell, false, true));
			
		pane.append(new JScrollPane(tree), BorderLayout.CENTER);
		pane.setOpaque(true);
		pane.setBackground(tree.getBackground());
		var button:JButton = new JButton("Expand");
		pane.append(button, BorderLayout.SOUTH);
		setContentPane(pane);
	}
		
	
	public static function main():Void {
		try{
			var myWindow1:ListTreeWithCellTipTest = new ListTreeWithCellTipTest ();
			myWindow1.setLocation (0, 0);
			myWindow1.setSize (200, 200);
			myWindow1.show();
		}catch(e:Error){
			trace("Catched a error : " + e);
		}
	}
	
	private static function getDefaultTreeModel():TreeModel {
        var root:DefaultMutableTreeNode = new DefaultMutableTreeNode("JTree");
		var parent:DefaultMutableTreeNode;
	
		parent = new DefaultMutableTreeNode("colors");
		root.append(parent);
		parent.append(new DefaultMutableTreeNode("just long word to test tip blue"));
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
		parent.append(new DefaultMutableTreeNode("just long word to test tip hot dogs"));
		parent.append(new DefaultMutableTreeNode("pizza"));
		parent.append(new DefaultMutableTreeNode("ravioli"));
		parent.append(new DefaultMutableTreeNode("bananas"));
		
		var sp:DefaultMutableTreeNode = new DefaultMutableTreeNode("sub");
		parent.append(sp);
		sp.append(new DefaultMutableTreeNode("111111111111"));
		sp.append(new DefaultMutableTreeNode("2222222222222"));
		sp.append(new DefaultMutableTreeNode("3333333333333"));
		sp.append(new DefaultMutableTreeNode("4444444444"));
		
		parent = new DefaultMutableTreeNode("food2");
		root.append(parent);
		parent.append(new DefaultMutableTreeNode("2just long word to test tip hot dogs"));
		parent.append(new DefaultMutableTreeNode("2pizza"));
		parent.append(new DefaultMutableTreeNode("2ravioli"));
		parent.append(new DefaultMutableTreeNode("2bananas"));
        return new DefaultTreeModel(root);
    }	
}