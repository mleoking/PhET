/*
 Copyright aswing.org, see the LICENCE.txt.
*/
package org.aswing.colorchooser { 
import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.Icon;
import org.aswing.graphics.*;
import flash.display.DisplayObject;
/**
 * @author iiley
 */
public class NoColorIcon implements Icon {
	private var width:Number;
	private var height:Number;
	
	public function NoColorIcon(width:int, height:int){
		this.width = width;
		this.height = height;
	}

	/**
	 * Return the icon's width.
	 */
	public function getIconWidth(c:Component):int{
		return width;
	}
	
	/**
	 * Return the icon's height.
	 */
	public function getIconHeight(c:Component):int{
		return height;
	}
	
	/**
	 * Draw the icon at the specified location.
	 */
	public function updateIcon(com:Component, g:Graphics2D, x:int, y:int):void{
		g.beginDraw(new Pen(ASColor.BLACK, 1));
		g.beginFill(new SolidBrush(ASColor.WHITE));
		var w:Number = width/2 + 1;
		var h:Number = height/2 + 1;
		x = x + w/2 - 1;
		y = y + h/2 - 1;
		g.rectangle(x, y, w, h);
		g.endFill();
		g.endDraw();
		g.drawLine(new Pen(ASColor.RED, 2), x+1, y+h-1, x+w-1, y+1);
	}
	
	public function getDisplay(c:Component):DisplayObject{
		return null;
	}	
}
}