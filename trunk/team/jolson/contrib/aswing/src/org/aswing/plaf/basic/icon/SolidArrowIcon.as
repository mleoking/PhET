/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.geom.Point;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;
import org.aswing.plaf.UIResource;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.icon.SolidArrowIcon implements Icon, UIResource {
	
	private var width:Number;
	private var height:Number;
	private var arrow:Number;
	private var color:ASColor;
	
	public function SolidArrowIcon(arrow:Number, size:Number, color:ASColor){
		this.arrow = arrow;
		this.width = size;
		this.height = size;
		this.color = color;
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
	
	public function setArrow(arrow:Number):Void{
		this.arrow = arrow;
	}
	
	/**
	 * Draw the icon at the specified location.
	 */
	public function paintIcon(com:Component, g:Graphics, x:Number, y:Number):Void{
		var center:Point = new Point(x + width/2, y + height/2);
		var w:Number = width;
		var ps1:Array = new Array();
		ps1.push(center.nextPoint(arrow, w/2/2));
		var back:Point = center.nextPoint(arrow + Math.PI, w/2/2);
		ps1.push(back.nextPoint(arrow - Math.PI/2, w/2));
		ps1.push(back.nextPoint(arrow + Math.PI/2, w/2));
		
		g.fillPolygon(new SolidBrush(color), ps1);
	}
	
	public function uninstallIcon(com:Component):Void{
	}	

}