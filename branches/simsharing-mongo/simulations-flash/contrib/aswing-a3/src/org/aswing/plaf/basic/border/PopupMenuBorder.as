package org.aswing.plaf.basic.border{

import org.aswing.geom.*;
import org.aswing.*;
import flash.display.*;
import org.aswing.plaf.*;
import org.aswing.graphics.*;

/**
 * @private
 */
public class PopupMenuBorder implements Border, UIResource{
	
	protected var color:ASColor;
	
	public function PopupMenuBorder(){
	}
	
	public function updateBorder(c:Component, g:Graphics2D, bounds:IntRectangle):void{
		if(color == null){
			color = c.getUI().getColor("PopupMenu.borderColor");
		}
		g.beginDraw(new Pen(color.changeAlpha(0.5), 4));
		g.moveTo(bounds.x + bounds.width - 2, bounds.y+8);
		g.lineTo(bounds.x + bounds.width - 2, bounds.y+bounds.height-2);
		g.lineTo(bounds.x + 8, bounds.y+bounds.height-2);
		g.endDraw();
		g.drawRectangle(new Pen(color, 1), 
			bounds.x+0.5, bounds.y+0.5, 
			bounds.width - 4,
			bounds.height - 4);
	}
	
	public function getBorderInsets(com:Component, bounds:IntRectangle):Insets{
		return new Insets(1, 1, 4, 4);
	}
	
	public function getDisplay(c:Component):DisplayObject{
		return null;
	}
	
}
}