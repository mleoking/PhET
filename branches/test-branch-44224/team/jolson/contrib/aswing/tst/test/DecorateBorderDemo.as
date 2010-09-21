/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.border.Border;
import org.aswing.border.LineBorder;
import org.aswing.BorderLayout;
import org.aswing.FlowLayout;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JPanel;

/**
 * The demo to show how DecorateBorder make borders brilliant and easily to create.
 * If you want to make your Border class can decorated, just extends DecorateBorder.
 */
class test.DecorateBorderDemo extends JFrame {
	
	public function DecorateBorderDemo(owner, title, modal : Boolean) {
		super("DecorateBorderDemo");
		
		var button:JButton = new JButton("Borderd");
		var redBorder:Border = new LineBorder(null, ASColor.RED, 2);
		var whiteBorder:Border = new LineBorder(redBorder, ASColor.WHITE, 2);
		var blackBorder:Border = new LineBorder(whiteBorder, ASColor.BLACK, 2);
		
		//avoid to destory button's LAF border, so add button to a panel, and set border to the panel
		var pane:JPanel = new JPanel(new BorderLayout());
		pane.append(button, BorderLayout.CENTER);
		pane.setBorder(blackBorder); 
		
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER));
		getContentPane().append(pane);
		
		setBorder(blackBorder);
	}
	
	public static function main():Void{
		var p:DecorateBorderDemo = new DecorateBorderDemo();
		p.setResizeDirectly(true);
		p.setBounds(100, 100, 300, 300);
		p.show();
	}	

}
