/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.BorderLayout;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JPanel;
import org.aswing.JScrollPane;
import org.aswing.JTree;

/**
 * @author iiley
 */
class test.tree.TreeTest extends JFrame {
	private var tree:JTree;
	
	public function TreeTest() {
		super("TreeTest");
		var pane:JPanel = new JPanel(new BorderLayout());
		tree = new JTree();
		tree.setEditable(true);
		tree.setRowHeight(20);
		//tree.setFixedCellWidth(80);
		pane.append(new JScrollPane(tree), BorderLayout.CENTER);
		var button:JButton = new JButton("Expand");
		pane.append(button, BorderLayout.SOUTH);
		setContentPane(pane);
		//button.addActionListener(__repaintTree, this);
	}
	
	private function __repaintTree():Void{
		tree.expandPath(tree.getPathForRow(1));
		tree.expandPath(tree.getPathForRow(0));
	}
	
	public static function main():Void {
		try{
			var myWindow1:TreeTest = new TreeTest ();
			myWindow1.setLocation (0, 0);
			myWindow1.setSize (200, 200);
			myWindow1.show();
		}catch(e:Error){
			trace("Catched a error : " + e);
		}
	}
}