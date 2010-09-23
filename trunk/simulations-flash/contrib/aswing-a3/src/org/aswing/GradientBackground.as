package org.aswing{

import org.aswing.graphics.*;
import org.aswing.geom.IntRectangle;
import flash.display.DisplayObject;
import flash.geom.*;
import flash.display.Shape;

/**
 * A background decorator that paint a gradient color.
 * @author
 */
public class GradientBackground implements GroundDecorator{
	
	private var brush:GradientBrush;
	private var direction:Number;
	private var shape:Shape;
	
	public function GradientBackground(fillType:String , colors:Array, alphas:Array, ratios:Array, direction:Number=0, 
					spreadMethod:String = "pad", interpolationMethod:String = "rgb", focalPointRatio:Number = 0){
		this.brush = new GradientBrush(fillType, colors, alphas, ratios, new Matrix(), 
			spreadMethod, interpolationMethod, focalPointRatio);
		this.direction = direction;
		shape = new Shape();
	}
	
	public function updateDecorator(com:Component, g:Graphics2D, bounds:IntRectangle):void{
		shape.graphics.clear();
		g = new Graphics2D(shape.graphics);
		var matrix:Matrix = new Matrix();
		matrix.createGradientBox(bounds.width, bounds.height, direction, bounds.x, bounds.y);    
		brush.setMatrix(matrix);
		g.fillRectangle(brush, bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	public function getDisplay(c:Component):DisplayObject{
		return shape;
	}
	
}
}