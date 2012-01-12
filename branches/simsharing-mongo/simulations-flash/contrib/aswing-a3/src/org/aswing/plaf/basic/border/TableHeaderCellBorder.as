/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.border{
	
import org.aswing.graphics.*;
import org.aswing.*;
import org.aswing.geom.*;
import flash.display.DisplayObject;
import org.aswing.plaf.UIResource;
import org.aswing.plaf.ComponentUI;

/**
 * @private
 */
public class TableHeaderCellBorder implements Border, UIResource{
	
	private var shadow:ASColor;
    private var darkShadow:ASColor;
    private var highlight:ASColor;
    private var lightHighlight:ASColor;
    
	public function TableHeaderCellBorder(){
	}
	
	private function reloadColors(ui:ComponentUI):void{
		shadow = ui.getColor("Button.shadow");
		darkShadow = ui.getColor("Button.darkShadow");
		highlight = ui.getColor("Button.light");
		lightHighlight = ui.getColor("Button.highlight");
	}
	
	public function updateBorder(c:Component, g:Graphics2D, b:IntRectangle):void{
		if(shadow == null){
			reloadColors(c.getUI());
		}
		var pen:Pen = new Pen(darkShadow, 1);
		g.drawLine(pen, b.x+b.width-0.5, b.y+4, b.x+b.width-0.5, Math.max(b.y+b.height-2, b.y+4));
		g.fillRectangle(new SolidBrush(darkShadow), b.x, b.y+b.height-1, b.width, 1);
	}
	
	public function getBorderInsets(com:Component, bounds:IntRectangle):Insets
	{
		return new Insets(0, 0, 1, 1);
	}
	
	public function getDisplay(c:Component):DisplayObject
	{
		return null;
	}
	
}
}