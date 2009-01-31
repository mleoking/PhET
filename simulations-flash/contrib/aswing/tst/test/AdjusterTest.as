/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Container;
import org.aswing.JAdjuster;
import org.aswing.JFrame;
import org.aswing.SoftBox;

/**
 * @author iiley
 */
class test.AdjusterTest extends JFrame {
	
	private var adjuster1:JAdjuster;
	private var adjuster2:JAdjuster;
	private var adjuster3:JAdjuster;
	
	public function AdjusterTest() {
		super("AdjusterTest");
		
		var p:Container = SoftBox.createVerticalBox(2);
		adjuster1 = new JAdjuster();
		//adjuster1.setValues(1250, 0, 1250, 1350);
		adjuster1.setMaximum(1350);
		adjuster1.setMinimum(1250);		
		adjuster2 = new JAdjuster();
		adjuster3 = new JAdjuster(6);
		adjuster3.setOrientation(JAdjuster.HORIZONTAL);
		
		adjuster2.setValueTranslator(
			function(value:Number):String{
				return Math.round(value) + "%";
			}
		);
		
		adjuster3.setValueTranslator(
			function(value:Number):String{
				return Math.round(value) + " cm";
			}
		);		
		
		p.append(adjuster1);
		p.append(adjuster2);
		p.append(adjuster3);
		
		setContentPane(p);
	}

	public static function main():Void {
		Stage.scaleMode = "noScale";
		try{
			var fj:AdjusterTest = new AdjusterTest();
			fj.setBounds(100, 100, 400, 400);
			fj.setVisible(true);
		}catch(e:Error){
			trace("Error : " + e);
		}
	}
}