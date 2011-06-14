/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.ASWingUtils;
import org.aswing.BorderLayout;
import org.aswing.geom.Point;
import org.aswing.JButton;
import org.aswing.JColorChooser;
import org.aswing.JFrame;
import org.aswing.JScrollPane;
import org.aswing.JTextArea;
import org.aswing.util.Delegate;

/**
 * @author iiley
 */
class test.ColorChooserTest extends JFrame {
	
	private var infoText:JTextArea;
	private var chooserDialog:JFrame;
	
	public function ColorChooserTest(){
		super("ColorChooserTest");
		
		var button:JButton = new JButton("Choose Color");
		button.addActionListener(__openColorChooserDialog, this);
		
		getContentPane().append(button, BorderLayout.NORTH);
		
		infoText = new JTextArea();
		getContentPane().append(new JScrollPane(infoText), BorderLayout.CENTER);
		
		chooserDialog = JColorChooser.createDialog(new JColorChooser(), this, "Chooser a color to test", 
			false, Delegate.create(this, __colorSelected), 
			Delegate.create(this, __colorCanceled));
		//center it
		var location:Point = ASWingUtils.getScreenCenterPosition();
		location.x -= chooserDialog.getWidth()/2;
		location.y -= chooserDialog.getHeight()/2;
		chooserDialog.setLocation(location);
	}
	
	private function __openColorChooserDialog():Void{
		chooserDialog.show();
	}
	
	private function __colorSelected(color:ASColor):Void{
		infoText.appendText("Selected Color : " + color + "\n");
	}
	private function __colorCanceled():Void{
		infoText.appendText("Selecting canceled!\n");
	}
	
	public static function main():Void {
		Stage.scaleMode = "noScale";
		try{
			var fj:ColorChooserTest = new ColorChooserTest();
			fj.setBounds(100, 100, 400, 400);
			fj.setVisible(true);
		}catch(e:Error){
			trace("Error : " + e);
		}
	}
}