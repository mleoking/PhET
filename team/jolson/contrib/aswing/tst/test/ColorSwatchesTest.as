/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.colorchooser.JColorSwatches;
import org.aswing.FlowLayout;
import org.aswing.JFrame;

/**
 * @author iiley
 */
class test.ColorSwatchesTest extends JFrame {
	
	public function ColorSwatchesTest(){
		super("ColorSwatchesTest");
		
		var cs:JColorSwatches = new JColorSwatches();
		cs.setNoColorSectionVisible(true);
		
		getContentPane().setLayout(new FlowLayout());
		getContentPane().append(cs);
	}
	
	public static function main():Void {
		Stage.scaleMode = "noScale";
		try{
			var fj:ColorSwatchesTest = new ColorSwatchesTest();
			fj.setBounds(100, 100, 400, 400);
			fj.setVisible(true);
		}catch(e:Error){
			trace("Error : " + e);
		}
	}
}