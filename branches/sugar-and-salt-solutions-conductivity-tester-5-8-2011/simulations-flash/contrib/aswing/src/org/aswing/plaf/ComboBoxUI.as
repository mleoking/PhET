/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.JComboBox;
import org.aswing.plaf.ComponentUI;

/**
 * Pluggable look and feel interface for ComboBox.
 * <p>
 * Subclass shoud override the three method of this class.
 * @author iiley
 */
class org.aswing.plaf.ComboBoxUI extends ComponentUI {
	
	public function ComboBoxUI() {
		super();
	}

	/**
     * Set the visiblity of the popup
     */
	public function setPopupVisible(c:JComboBox, v:Boolean):Void{}
	/** 
     * Determine the visibility of the popup
     */
	public function isPopupVisible(c:JComboBox):Boolean{return false;}
	/** 
     * Determine whether or not the combo box itself is traversable 
     */
	public function isFocusTraversable(c:JComboBox):Boolean{return false;}
	
}
