/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.BorderLayout;
import org.aswing.colorchooser.AbstractColorChooserPanel;
import org.aswing.colorchooser.JColorMixer;
import org.aswing.colorchooser.JColorSwatches;
import org.aswing.Container;
import org.aswing.JFrame;
import org.aswing.JPanel;
import org.aswing.JScrollPane;
import org.aswing.JTextArea;

/**
 * @author iiley
 */
class test.ColorMixerTest extends JFrame {
	
	private var infoText:JTextArea;
	
	public function ColorMixerTest(){
		super("test.ColorMixerTest");
		
		var p:Container = new JPanel();
		var cm:JColorMixer = new JColorMixer();
		cm.addChangeListener(__colorChanged, this);
		cm.setNoColorSectionVisible(true);
		p.append(cm);		
		var cs:JColorSwatches = new JColorSwatches();
		cs.addChangeListener(__colorChanged, this);
		cs.setNoColorSectionVisible(true);
		//p.append(cs);
		
		getContentPane().append(p, BorderLayout.NORTH);
		
		infoText = new JTextArea("");
		getContentPane().append(new JScrollPane(infoText), BorderLayout.CENTER);
	}
	
	private function __colorChanged(cp:AbstractColorChooserPanel):Void{
		infoText.appendText(cp.getSelectedColor() + "\n");
	}
	
	public static function main():Void {
		Stage.scaleMode = "noScale";
		trace("ColorMixerTest");
		try{
			var fj:ColorMixerTest = new ColorMixerTest();
			fj.setBounds(100, 100, 400, 400);
			fj.setVisible(true);
		}catch(e:Error){
			trace("Error : " + e);
		}
	}
}