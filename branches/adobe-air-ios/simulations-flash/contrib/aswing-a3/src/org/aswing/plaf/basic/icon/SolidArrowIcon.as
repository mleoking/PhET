/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.icon{
	
import flash.display.DisplayObject;

import org.aswing.*;
import org.aswing.geom.IntPoint;
import org.aswing.graphics.*;
import org.aswing.plaf.UIResource;
import flash.geom.Point;
import flash.display.Shape;

/**
 * @private
 * @author iiley
 */
public class SolidArrowIcon implements Icon, UIResource{
	
	protected var shape:Shape;
	protected var width:Number;
	protected var height:Number;
	protected var arrow:Number;
	protected var color:ASColor;
	
	public function SolidArrowIcon(arrow:Number, size:Number, color:ASColor){
		this.arrow = arrow;
		this.width = size;
		this.height = size;
		this.color = color;
		shape = new Shape();
		var x:int = 0;
		var y:int = 0;
		var g:Graphics2D = new Graphics2D(shape.graphics);
		var center:Point = new Point(x + width/2, y + height/2);
		var w:Number = width;
		var ps1:Array = new Array();
		ps1.push(nextPoint(center, arrow, w/2/2));
		var back:Point = nextPoint(center, arrow + Math.PI, w/2/2);
		ps1.push(nextPoint(back, arrow - Math.PI/2, w/2));
		ps1.push(nextPoint(back, arrow + Math.PI/2, w/2));
		
		g.fillPolygon(new SolidBrush(color), ps1);
	}	
	
	public function updateIcon(com:Component, g:Graphics2D, x:int, y:int):void{
		shape.x = x;
		shape.y = y;
	}
	
	//nextPoint with Point
	protected function nextPoint(op:Point, direction:Number, distance:Number):Point{
		return new Point(op.x+Math.cos(direction)*distance, op.y+Math.sin(direction)*distance);
	}
	
	public function getIconHeight(c:Component):int{
		return height;
	}
	
	public function getIconWidth(c:Component):int{
		return width;
	}
	
	public function setArrow(arrow:Number):void{
		this.arrow = arrow;
	}
	
	public function getDisplay(c:Component):DisplayObject{
		return shape;
	}
	
}
}