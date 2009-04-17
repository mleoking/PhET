/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.AbstractButton;
import org.aswing.Icon;
import org.aswing.plaf.ButtonUI;
import org.aswing.overflow.ToggleButtonModel;
import org.aswing.UIManager;

/**
 * An implementation of a two-state button.  
 * The <code>JRadioButton</code> and <code>JCheckBox</code> classes
 * are subclasses of this class.
 * @author iiley
 */
class org.aswing.overflow.JToggleButton extends AbstractButton{
	/**
     * JToggleButton(text:String, icon:Icon)<br>
     * JToggleButton(text:String)<br>
     * JToggleButton(icon:Icon)
     * <p>
	 */
	public function JToggleButton(text, icon:Icon){
		super(text, icon);
		setName("JToggleButton");
    	setModel(new ToggleButtonModel());
		updateUI();
	}
	
	public function updateUI():Void{
    	setUI(ButtonUI(UIManager.getUI(this)));
    }
    
	public function getUIClassID():String{
		return "ToggleButtonUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.asw.ASWingToggleButtonUI;
    }
}
