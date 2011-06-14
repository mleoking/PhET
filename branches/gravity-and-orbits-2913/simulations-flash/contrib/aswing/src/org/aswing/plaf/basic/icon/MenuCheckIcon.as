/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.Icon;
import org.aswing.overflow.JMenuItem;
import org.aswing.plaf.UIResource;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.icon.MenuCheckIcon implements Icon, UIResource {
	
	public function MenuCheckIcon(){
	}
	
	public function getIconWidth() : Number {
		return 8;
	}

	public function getIconHeight() : Number {
		return 8;
	}

	public function paintIcon(com : Component, g : Graphics, x : Number, y : Number) : Void {
//		var menu:JMenuItem = JMenuItem(com);
//		if(menu.isSelected()){
//			g.beginDraw(new Pen(ASColor.BLACK, 2));
//			g.moveTo(x, y+3);
//			g.lineTo(x+3, y+6);
//			g.lineTo(x+7, y+1);
//			g.endDraw();
//		}
		//paint nothing
	}

	public function uninstallIcon(com : Component) : Void {
	}

}