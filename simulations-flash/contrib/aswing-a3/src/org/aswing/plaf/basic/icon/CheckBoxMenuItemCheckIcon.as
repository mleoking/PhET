/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.icon{
	
import flash.display.*;
import org.aswing.*;
import org.aswing.graphics.*;

/**
 * @private
 */
public class CheckBoxMenuItemCheckIcon extends MenuCheckIcon{
	
	private var shape:Shape;
	
	public function CheckBoxMenuItemCheckIcon(){
		shape = new Shape();
	}
	
	override public function updateIcon(c:Component, g:Graphics2D, x:int, y:int):void{
		shape.graphics.clear();
		g = new Graphics2D(shape.graphics);
		var menu:AbstractButton = AbstractButton(c);
		if(menu.isSelected()){
			g.beginDraw(new Pen(ASColor.BLACK, 2));
			g.moveTo(x, y+4);
			g.lineTo(x+3, y+7);
			g.lineTo(x+7, y+2);
			g.endDraw();
		}
	}
	
	override public function getDisplay(c:Component):DisplayObject{
		return shape;
	}
}
}