/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Icon;
import org.aswing.overflow.JMenuItem;
import org.aswing.overflow.ToggleButtonModel;

/**
 * A menu item that can be selected or deselected. If selected, the menu
 * item typically appears with a checkmark next to it. If unselected or
 * deselected, the menu item appears without a checkmark. Like a regular
 * menu item, a check box menu item can have either text or a graphic
 * icon associated with it, or both.
 * @author iiley
 */
class org.aswing.JCheckBoxMenuItem extends JMenuItem {
	
	public function JCheckBoxMenuItem(text, icon : Icon) {
		super(text, icon);
		setName("JCheckBoxMenuItem");
    	setModel(new ToggleButtonModel());
	}

	public function getUIClassID():String{
		return "CheckBoxMenuItemUI";
	}
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicCheckBoxMenuItemUI;
    }
}