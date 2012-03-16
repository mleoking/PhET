/*
 CopyRight @ 2005 XLands.com INC. All rights reserved.
*/

import org.aswing.BorderLayout;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JListTree;
import org.aswing.JPanel;
import org.aswing.JScrollPane;

/**
 * @author iiley
 */
class test.tree.ListTreeTest extends JFrame {
	
	private var tree:JListTree;
	private var xml:XML;
	
	public function ListTreeTest() {
		super("ListTreeTest");
		var pane:JPanel = new JPanel(new BorderLayout());
		tree = new JListTree();
		pane.append(new JScrollPane(tree), BorderLayout.CENTER);
		pane.setOpaque(true);
		pane.setBackground(tree.getBackground());
		var button:JButton = new JButton("Expand");
		pane.append(button, BorderLayout.SOUTH);
		setContentPane(pane);
		//button.addActionListener(__repaintTree, this);
	}
		
	
	public static function main():Void {
		try{
			var myWindow1:ListTreeTest = new ListTreeTest ();
			myWindow1.setLocation (0, 0);
			myWindow1.setSize (200, 200);
			myWindow1.show();
		}catch(e:Error){
			trace("Catched a error : " + e);
		}
	}
}