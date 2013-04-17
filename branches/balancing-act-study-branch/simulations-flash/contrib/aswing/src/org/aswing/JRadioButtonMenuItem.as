/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Icon;
import org.aswing.overflow.JMenuItem;
import org.aswing.overflow.ToggleButtonModel;

/**
 * An implementation of a radio button menu item.
 * A <code>JRadioButtonMenuItem</code> is
 * a menu item that is part of a group of menu items in which only one
 * item in the group can be selected. The selected item displays its
 * selected state. Selecting it causes any other selected item to
 * switch to the unselected state.
 * To control the selected state of a group of radio button menu items,  
 * use a <code>ButtonGroup</code> object.
 * @author iiley
 */
class org.aswing.JRadioButtonMenuItem extends JMenuItem {
	
	public function JRadioButtonMenuItem(text, icon : Icon) {
		super(text, icon);
		setName("JRadioButtonMenuItem");
    	setModel(new ToggleButtonModel());
	}

	public function getUIClassID():String{
		return "RadioButtonMenuItemUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicRadioButtonMenuItemUI;
    }

}