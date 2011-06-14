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
public class ColorRectIcon implements Icon {
	private var width:Number;
	private var height:Number;
	private var color:ASColor;
	
	public function ColorRectIcon(width:int, height:int, color:ASColor){
		this.width = width;
		this.height = height;
		this.color = color;
	}
	
	public function setColor(color:ASColor):void{
		this.color = color;
	}
	
	public function getColor():ASColor{
		return color;
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
	
	public function updateIcon(com:Component, g:Graphics2D, x:int, y:int):void{
		var w:Number = width;
		var h:Number = height;
		g.fillRectangle(new SolidBrush(ASColor.WHITE), x, y, w, h);
		if(color != null){
			var t:Number = 5;
			for(var c:Number=0; c<w; c+=t){
				g.drawLine(new Pen(ASColor.GRAY, 1), x+c, y, x+c, y+h);
			}
			for(var r:Number=0; r<h; r+=t){
				g.drawLine(new Pen(ASColor.GRAY, 1), x, y+r, x+w, y+r);
			}
			g.fillRectangle(new SolidBrush(color), x, y, width, height);
		}else{
			g.drawLine(new Pen(ASColor.RED, 2), x+1, y+h-1, x+w-1, y+1);
		}
	}
	
	public function getDisplay(c:Component):DisplayObject{
		return null;
	}	
}
}