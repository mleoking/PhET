/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.icon
{
	
import org.aswing.graphics.*;
import org.aswing.*;
import org.aswing.geom.*;
import flash.display.DisplayObject;
import org.aswing.plaf.UIResource;
import flash.geom.Point;

/**
 * @private
 */
public class ArrowIcon implements Icon, UIResource
{
	
	private var arrow:Number;
	private var width:int;
	private var height:int;
	private var shadow:ASColor;
	private var darkShadow:ASColor;
	
	public function ArrowIcon(arrow:Number, size:int, shadow:ASColor,
			 darkShadow:ASColor){
		this.arrow = arrow;
		this.width = size;
		this.height = size;
		this.shadow = shadow;
		this.darkShadow = darkShadow;
	}
	
	public function updateIcon(c:Component, g:Graphics2D, x:int, y:int):void
	{
		var center:Point = new Point(c.getWidth()/2, c.getHeight()/2);
		var w:Number = width;
		var ps1:Array = new Array();
		ps1.push(nextPoint(center, arrow, w/2/2));
		var back:Point = nextPoint(center, arrow + Math.PI, w/2/2);
		ps1.push(nextPoint(back, arrow - Math.PI/2, w/2));
		ps1.push(nextPoint(back, arrow + Math.PI/2, w/2));
		
		//w -= (w/4);
		var ps2:Array = new Array();
		ps2.push(nextPoint(center, arrow, w/2/2-1));
		back = nextPoint(center, arrow + Math.PI, w/2/2-1);
		ps2.push(nextPoint(back, arrow - Math.PI/2, w/2-2));
		ps2.push(nextPoint(back, arrow + Math.PI/2, w/2-2));
		
		g.fillPolygon(new SolidBrush(darkShadow), ps1);
		g.fillPolygon(new SolidBrush(shadow), ps2);		
	}
	
	protected function nextPoint(p:Point, dir:Number, dis:Number):Point{
		return new Point(p.x+Math.cos(dir)*dis, p.y+Math.sin(dir)*dis);
	}
	
	public function getIconHeight(c:Component):int
	{
		return width;
	}
	
	public function getIconWidth(c:Component):int
	{
		return height;
	}
	
	public function getDisplay(c:Component):DisplayObject
	{
		return null;
	}
	
}
}