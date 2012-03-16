/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.BorderLayout;
import org.aswing.JFrame;
import org.aswing.JLabel;
import org.aswing.JSlider;

/**
 * @author iiley
 */
class test.SliderTest extends JFrame {
	private var hSlider:JSlider;
	private var vSlider:JSlider;
	private var valueLabel:JLabel;	
	
	public function SliderTest(){
		super("ForJSlider");
		valueLabel = new JLabel("slider value");
		hSlider = new JSlider(JSlider.HORIZONTAL);
		vSlider = new JSlider(JSlider.VERTICAL);
		
		hSlider.setEnabled(false);
		vSlider.setMajorTickSpacing(20);
		vSlider.setMinorTickSpacing(2);
		vSlider.setSnapToTicks(true);
		vSlider.setPaintTicks(true);
		vSlider.setPaintTrack(true);
		//vSlider.setShowValueTip(true);
		//vSlider.setInverted(true);
		hSlider.setPaintTicks(true);
		hSlider.setMajorTickSpacing(25);
		hSlider.setMinorTickSpacing(5);
		//hSlider.setEnabled(false);
		
		getContentPane().append(hSlider, BorderLayout.CENTER);
		getContentPane().append(vSlider, BorderLayout.EAST);
		hSlider.setExtent(50);
		hSlider.setSnapToTicks(true);
		
		var slider3:JSlider = new JSlider(JSlider.HORIZONTAL);
		slider3.setShowValueTip(true);
		slider3.setToolTipText("This Slider has a value tip!");
		//slider3.setInverted(true);
		getContentPane().append(slider3, BorderLayout.SOUTH);
		getContentPane().append(valueLabel, BorderLayout.NORTH);
		
		hSlider.addChangeListener(__valueChanged, this);
		vSlider.addChangeListener(__valueChanged, this);
		slider3.addChangeListener(__valueChanged, this);
	}
	private function __valueChanged(slider:JSlider):Void{
		valueLabel.setText(slider.getValue()+"");
	}
	/**
	 * @param args
	 */
	public static function main():Void {
		Stage.scaleMode = "noScale";
		var fj:SliderTest = new SliderTest();
		fj.setBounds(100, 100, 400, 400);
		fj.setVisible(true);
	}

}