/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.colorchooser.AbstractColorChooserPanel;
import org.aswing.Component;
import org.aswing.plaf.ColorSwatchesUI;
import org.aswing.UIManager;

/**
 * @author iiley
 */
class org.aswing.colorchooser.JColorSwatches extends AbstractColorChooserPanel {
		
	public function JColorSwatches() {
		super();
		updateUI();
	}

	public function setUI(ui:ColorSwatchesUI):Void{
		super.setUI(ui);
	}
	
	public function getUI():ColorSwatchesUI{
		return ColorSwatchesUI(ui);
	}
	
	public function updateUI():Void{
		setUI(ColorSwatchesUI(UIManager.getUI(this)));
	}
	
	public function getUIClassID():String{
		return "ColorSwatchesUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicColorSwatchesUI;
    }
	
	/**
	 * Adds a component to this panel's sections bar
	 * @param com the component to be added
	 */
	public function addComponentColorSectionBar(com:Component):Void{
		getUI().addComponentColorSectionBar(com);
	}
}