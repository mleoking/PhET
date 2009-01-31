/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.AttachIcon;
import org.aswing.border.LineBorder;
import org.aswing.BorderLayout;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JLabel;
import org.aswing.JPanel;
import org.aswing.util.Delegate;

/**
 * @author iiley
 */
class test.AttachIconDemo extends JFrame {
	
	private var names:Array;
	private var mcIcon:AttachIcon;
	private var mcLabel:JLabel;
	private var statusText:JLabel;
	
	public function AttachIconDemo() {
		super("AttachIconDemo");
		
		//all_actions is the linkageID of hero symbol
		mcIcon = new AttachIcon("all_actions"); 
		mcLabel = new JLabel("", mcIcon, JLabel.CENTER);
		
		var labelPanel:JPanel = new JPanel(new BorderLayout());
		labelPanel.append(mcLabel, BorderLayout.CENTER);
		labelPanel.setBorder(new LineBorder(null, ASColor.RED, 2));
				
		var topButtonPanel:JPanel = new JPanel();
		var button:JButton;
		names = ["Walk","Stand","Small Attack","Run","Middle Attack",
							"Jump","Hurt","Fall","Block","Big Attack"];
							
		for(var i:Number=0; i<names.length; i++){
			button = new JButton(names[i]);
			button.addActionListener(Delegate.create(this, __playAction, i+1));
			topButtonPanel.append(button);
		}
		
		getContentPane().append(topButtonPanel, BorderLayout.NORTH);
		getContentPane().append(labelPanel, BorderLayout.CENTER);
		statusText = new JLabel();
		getContentPane().append(statusText, BorderLayout.SOUTH);
	}
	
	private function __playAction(source:JButton, number:Number):Void{
		var mc:MovieClip = mcIcon.getDecorateMC(mcLabel);
		mc.gotoAndStop(number);
		mc["mc"].gotoAndPlay(1);
		statusText.setText("Do " + names[number-1] + " Action!");
		statusText.revalidate();
	}
	
	public static function main():Void{
		Stage.scaleMode = "noScale";
		var p:AttachIconDemo = new AttachIconDemo();
		p.setBounds(20, 20, 500, 300);
		p.show();
	}	

}
