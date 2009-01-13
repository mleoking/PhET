/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Container;
import org.aswing.EmptyLayout;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JLabel;
import org.aswing.JPanel;
import org.aswing.resizer.ResizerController;

/**
 * @author iiley
 */
class test.resizer.ResizerTest extends JFrame {
	
	public function ResizerTest() {
		super("ResizerTest");
		var button : JButton = new JButton ("click to remove resizer"); 
		button.setSize (100, 25 );
		button.setLocation (100, 100 ); 
		
		var label1 : JLabel = new JLabel ("avoid resizing");
		label1.setSize (100, 25);
		label1.setLocation (200, 50 );
		
		var label2 : JLabel = new JLabel ("ok for resizing");
		label2.setSize (100, 25);
		label2.setLocation (50, 50 );
		label2.setOpaque (true );
		
		// Direct affectation
		var resizer : ResizerController = ResizerController.init ( button );
		resizer.setResizeDirectly(true);
		button.addActionListener(function(){
			ResizerController.remove(button);
		});
		
		// Simple call to #init method
		ResizerController.init ( label1 );
		ResizerController.init ( label2 );
		
		//We can retreive ResizerController for a specific component like : 
		var labelResizer : ResizerController = ResizerController.getController ( label1 );
		labelResizer.setResizable (false );
		
		var content:Container = getContentPane();
		content.setLayout(new EmptyLayout());
		// content must have a layout set to EmptyLayout to allow correct resizing 
		content.append (button );
		content.append (label1 );
		content.append (label2 );
		
		var p:JPanel = new JPanel();
		p.setOpaque(true);
		p.setBackground(ASColor.YELLOW);
		var b1:JButton = new JButton("resizing not directly");
		var b2:JButton = new JButton("do resizing directly");
		p.append(b1);
		p.append(b2);
		p.setBounds(50, 200, 200, 100);
		var pr:ResizerController = ResizerController.init(p);
		content.append(p);
		
		b1.addActionListener(function(){
			pr.setResizeDirectly(false);
		});
		b2.addActionListener(function(){
			pr.setResizeDirectly(true);
		});
		
	}


	public static function main():Void{
		try{
			Stage.scaleMode = "noScale";
			trace("try ResizerTest");
			var p:ResizerTest = new ResizerTest();
			p.setClosable(false);
			p.setLocation(0, 0);
			p.setSize(600, 400);
			p.show();
			trace("done ResizerTest");
		}catch(e){
		   trace("error : " + e);
		}
	}
}