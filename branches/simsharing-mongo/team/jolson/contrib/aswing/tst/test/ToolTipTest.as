/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASFont;
import org.aswing.FlowLayout;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JLabel;
import org.aswing.JToolTip;

/**
 *
 * @author iiley
 */
class test.ToolTipTest extends JFrame {
	
	private var dButton:JButton;
	private var dTip:JToolTip;
	
	public function ToolTipTest() {
		super("ToolTipTest");
		
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER));
		
		var button1:JButton = new JButton("one line tip");
		var tip1:JToolTip = new JToolTip();
		tip1.setFont(new ASFont("Arial", 20));
		tip1.setTipText("one line tip");
		tip1.setTargetComponent(button1);
		tip1.setOffsetsRelatedToMouse(false);
		
		var label:JLabel = new JLabel("two line tip");
		label.setToolTipText("first line\nsencond line!!!!!!!!!");
		
		dButton = new JButton("Change My ToolTip words");
		dTip = new JToolTip();
		dTip.setTipText("Change My ToolTip words");
		dTip.setTargetComponent(dButton);
		dButton.addActionListener(__changeToolTip, this);
		
		getContentPane().append(button1);
		getContentPane().append(label);
		getContentPane().append(dButton);
		
		getContentPane().setToolTipText("Frame Content Tip\n2 line\n3line\nend!");
	}
	
	private function __changeToolTip():Void{
		dTip.setTipText("Changed : " + Math.random());
	}

	public static function main():Void{
		try{
			trace("try ToolTipTest");
			var p:ToolTipTest = new ToolTipTest();
			p.setClosable(false);
			p.setLocation(50, 50);
			p.setSize(400, 400);
			p.show();
			trace("done ToolTipTest");
		}catch(e){
			trace("error : " + e);
		}
	}
}
