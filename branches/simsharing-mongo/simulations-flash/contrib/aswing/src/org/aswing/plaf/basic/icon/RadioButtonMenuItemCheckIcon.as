/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.overflow.JMenuItem;
import org.aswing.plaf.basic.icon.MenuCheckIcon;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.icon.RadioButtonMenuItemCheckIcon extends MenuCheckIcon {
	
	public function RadioButtonMenuItemCheckIcon() {
		super();
	}
	
	public function paintIcon(com : Component, g : Graphics, x : Number, y : Number) : Void {
		var menu:JMenuItem = JMenuItem(com);
		if(menu.isSelected()){
			g.fillCircle(new SolidBrush(ASColor.BLACK), x+4, y+5, 3, 3);
		}
	}
}