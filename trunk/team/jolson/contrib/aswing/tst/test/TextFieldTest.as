/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.BorderLayout;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JPanel;
import org.aswing.JTextField;

/**
 * @author iiley
 */
class test.TextFieldTest extends JFrame {
	
	private var tf1:JTextField;
	private var tf2:JTextField;
	private var button1:JButton;
	
	public function TextFieldTest() {
		super(_root, "TextFieldTest", false);
		
		var pane1:JPanel = new JPanel();
		tf1 = new JTextField("tf1", 10);
		tf2 = new JTextField("tf2");
		var tf3:JTextField = new JTextField("tf3d", 10); 
		tf1.setEditable(true);
		tf2.setEditable(false);
		tf3.setEnabled(false);
		tf1.setMaxChars(10);
		pane1.append(tf1);
		pane1.append(tf2);
		pane1.append(tf3);
		
		getContentPane().append(pane1, BorderLayout.CENTER);
		
		button1 = new JButton("Text1 -> Text2");
		button1.setToolTipText("Copy the text from first to second.");
		getContentPane().append(button1, BorderLayout.SOUTH);
		
		var t1:JTextField = tf1;
		var t2:JTextField = tf2;
		button1.addActionListener(function(){ 
			t2.setText(t1.getText());
			trace("try to set t2 text : " + t1.getText());
			trace("t2 text set to : " + t2.getText());
			});
	}


	public static function main():Void{
		try{
			trace("try TextFieldTest");
			
			var p:TextFieldTest = new TextFieldTest();
			p.setClosable(false);
			
			p.setLocation(50, 50);
			p.setSize(400, 400);
			p.show();
			
			trace("done TextFieldTest");
		}catch(e){
			trace("error : " + e);
		}
	}
}
