
package org.aswing.plaf.basic.background{

import org.aswing.graphics.*;
import org.aswing.GroundDecorator;
import org.aswing.geom.IntRectangle;
import org.aswing.Component;
import flash.display.DisplayObject;
import org.aswing.plaf.UIResource;
import flash.geom.Matrix;

/**
 * @private
 */
public class TextComponentBackBround implements GroundDecorator, UIResource{
	
	public function updateDecorator(c:Component, g:Graphics2D, r:IntRectangle):void{
    	if(c.isOpaque() && c.isEnabled()){
			var x:Number = r.x;
			var y:Number = r.y;
			var w:Number = r.width;
			var h:Number = r.height;
			g.fillRectangle(new SolidBrush(c.getBackground()), x, y, w, h);
			
			var colors:Array = [0xF7F7F7, c.getBackground().getRGB()];
			var alphas:Array = [0.5, 0];
			var ratios:Array = [0, 100];
			var matrix:Matrix = new Matrix();
			matrix.createGradientBox(w, h, (90/180)*Math.PI, x, y);     
		    var brush:GradientBrush = new GradientBrush(GradientBrush.LINEAR, colors, alphas, ratios, matrix);
		    g.fillRectangle(brush, x, y, w, h);
    	}
	}
	
	public function getDisplay(c:Component):DisplayObject
	{
		return null;
	}
	
}
}