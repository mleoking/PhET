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

import test.tree.IconListTreeCell;

/**
 * @author iiley
 */
class test.tree.ListTreeWithIconTest extends JFrame {
	
	private var tree:JListTree;
	private var xml:XML;
	
	public function ListTreeWithIconTest() {
		super("ListTreeWithIconTest");
		var pane:JPanel = new JPanel(new BorderLayout());
		tree = new JListTree(null, new GeneralListCellFactory(IconListTreeCell, true, true));
		pane.append(new JScrollPane(tree), BorderLayout.CENTER);
		pane.setOpaque(true);
		pane.setBackground(tree.getBackground());
		var button:JButton = new JButton("Expand");
		pane.append(button, BorderLayout.SOUTH);
		setContentPane(pane);
	}
		
	
	public static function main():Void {
		try{
			var myWindow1:ListTreeWithIconTest = new ListTreeWithIconTest ();
			myWindow1.setLocation (0, 0);
			myWindow1.setSize (200, 200);
			myWindow1.show();
		}catch(e:Error){
			trace("Catched a error : " + e);
		}
	}
	
}