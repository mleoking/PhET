/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.colorchooser.AbstractColorChooserPanel;
import org.aswing.plaf.ColorMixerUI;
import org.aswing.UIManager;

/**
 * @author iiley
 */
class org.aswing.colorchooser.JColorMixer extends AbstractColorChooserPanel {
	
	public function JColorMixer() {
		super();
		updateUI();
	}

	public function setUI(ui:ColorMixerUI):Void{
		super.setUI(ui);
	}
	
	public function getUI():ColorMixerUI{
		return ColorMixerUI(ui);
	}
	
	public function updateUI():Void{
		setUI(ColorMixerUI(UIManager.getUI(this)));
	}
	
	public function getUIClassID():String{
		return "ColorMixerUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicColorMixerUI;
    }
}