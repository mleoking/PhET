/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;

/**
 * @author iiley
 */
class org.aswing.colorchooser.ColorRectIcon implements Icon {
	private var width:Number;
	private var height:Number;
	private var color:ASColor;
	
	public function ColorRectIcon(width:Number, height:Number, color:ASColor){
		this.width = width;
		this.height = height;
		this.color = color;
	}
	
	public function setColor(color:ASColor):Void{
		this.color = color;
	}
	
	public function getColor():ASColor{
		return color;
	}

	/**
	 * Return the icon's width.
	 */
	public function getIconWidth():Number{
		return width;
	}
	
	/**
	 * Return the icon's height.
	 */
	public function getIconHeight():Number{
		return height;
	}
	
	/**
	 * Draw the icon at the specified location.
	 */
	public function paintIcon(com:Component, g:Graphics, x:Number, y:Number):Void{
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
	
	public function uninstallIcon(com:Component):Void{
	}	
}