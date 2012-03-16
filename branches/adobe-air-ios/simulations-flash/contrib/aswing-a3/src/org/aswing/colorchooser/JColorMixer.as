/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.colorchooser { 
import org.aswing.colorchooser.AbstractColorChooserPanel;
import org.aswing.UIManager;
import org.aswing.plaf.basic.BasicColorMixerUI;

/**
 * @author iiley
 */
public class JColorMixer extends AbstractColorChooserPanel {
	
	public function JColorMixer() {
		super();
		updateUI();
	}
	
	override public function updateUI():void{
		setUI(UIManager.getUI(this));
	}
	
	override public function getUIClassID():String{
		return "ColorMixerUI";
	}
	
    override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicColorMixerUI;
    }

}
}