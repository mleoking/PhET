/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.icon{
	
import org.aswing.graphics.*;
import org.aswing.*;
import flash.display.DisplayObject;
import org.aswing.plaf.UIResource;
import flash.geom.Point;
import flash.display.Shape;

/**
 * The default frame title icon.
 * @author iiley
 * @private
 */
public class TitleIcon implements Icon, UIResource{
	
	private static const WIDTH:int = 16;
	private static const HEIGHT:int = 12;
	protected var shape:Shape;
	
	public function TitleIcon(){
		shape = new Shape();
	}
	
	public function updateIcon(c:Component, g:Graphics2D, x:int, y:int):void{
		shape.graphics.clear();
		g = new Graphics2D(shape.graphics);
		//This is just for test the icon
		//TODO draw a real beautiful icon for AsWing frame title
		//g.fillCircleRingWithThickness(new SolidBrush(ASColor.GREEN), x + WIDTH/2, y + WIDTH/2, WIDTH/2, WIDTH/4);
		var outterRect:ASColor = c.getUI().getColor("Frame.activeCaptionBorder");
		//var innerRect:ASColor = UIManager.getColor("Frame.inactiveCaptionBorder");
		var innerRect:ASColor = new ASColor(0xFFFFFF);
		
		x = x + 2;
		var width:int = WIDTH;
		var height:int = HEIGHT;
		
		var w4:Number = width/4;
		var h23:Number = 2*height/3;
		var w2:Number = width/2;
		var h:Number = height;
		var w:Number = width;
		
		var points:Array = new Array();
		points.push(new Point(x, y));
		points.push(new Point(x+w4, y+h));
		points.push(new Point(x+w2, y+h23));
		points.push(new Point(x+w4*3, y+h));
		points.push(new Point(x+w, y));
		points.push(new Point(x+w2, y+h23));
		
		g.drawPolygon(new Pen(outterRect, 2), points);
		g.fillPolygon(new SolidBrush(innerRect), points);		
	}
	
	public function getIconHeight(c:Component):int
	{
		return HEIGHT;
	}
	
	public function getIconWidth(c:Component):int
	{
		return WIDTH + 2;
	}
	
	public function getDisplay(c:Component):DisplayObject
	{
		return shape;
	}
	
}
}