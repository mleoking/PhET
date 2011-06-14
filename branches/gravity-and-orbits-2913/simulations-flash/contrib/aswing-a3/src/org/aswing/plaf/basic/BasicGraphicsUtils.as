/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic{

import org.aswing.graphics.*;
import org.aswing.geom.*;
import org.aswing.*;
import flash.geom.Matrix;

/**
 * @private
 */
public class BasicGraphicsUtils{
	
	public static function getDisabledColor(c:Component):ASColor{
		var bg:ASColor = c.getBackground();
		if(bg == null) bg = ASColor.BLACK;
		var hue:Number = bg.getHue();
		var lum:Number = bg.getLuminance();
		var sat:Number = bg.getSaturation();
		if(lum < 0.1){
			lum *= 1.4;
		}else{
			lum *= 0.7;
		}
		sat *= 0.7;
		return ASColor.getASColorWithHLS(hue, lum, sat, bg.getAlpha());
	}
	
	/**
	 * For buttons style bezel by fill function
	 */
	public static function drawUpperedBezel(g:Graphics2D, r:IntRectangle,
                                    shadow:ASColor, darkShadow:ASColor, 
                                 highlight:ASColor, lightHighlight:ASColor):void{
		var x1:Number = r.x;
		var y1:Number = r.y;
		var w:Number = r.width;
		var h:Number = r.height;
		
		var brush:SolidBrush = new SolidBrush(darkShadow);
		g.fillRectangleRingWithThickness(brush, x1, y1, w, h, 1);
		
        brush.setColor(lightHighlight);
        g.fillRectangleRingWithThickness(brush, x1, y1, w-1, h-1, 1);
        
        brush.setColor(highlight);
        g.fillRectangleRingWithThickness(brush, x1+1, y1+1, w-2, h-2, 1);
        
        brush.setColor(shadow);
        g.fillRectangle(brush, x1+w-2, y1+1, 1, h-2);
        g.fillRectangle(brush, x1+1, y1+h-2, w-2, 1);
	}
	
	/**
	 * For buttons style bezel by fill function
	 */
	public static function drawLoweredBezel(g:Graphics2D, r:IntRectangle,
                                    shadow:ASColor, darkShadow:ASColor, 
                                 highlight:ASColor, lightHighlight:ASColor):void{
                                 	
		var x1:Number = r.x;
		var y1:Number = r.y;
		var w:Number = r.width;
		var h:Number = r.height;
		
        var brush:SolidBrush = new SolidBrush(darkShadow);
		g.fillRectangleRingWithThickness(brush, x1, y1, w, h, 1);
		
		brush.setColor(darkShadow);
        g.fillRectangleRingWithThickness(brush, x1, y1, w-1, h-1, 1);
        
        brush.setColor(highlight);
        g.fillRectangleRingWithThickness(brush, x1+1, y1+1, w-2, h-2, 1);
        
        brush.setColor(highlight);
        g.fillRectangle(brush, x1+w-2, y1+1, 1, h-2);
        g.fillRectangle(brush, x1+1, y1+h-2, w-2, 1);
	}
	
	/**
	 * For buttons style bezel by fill function
	 */	
	public static function drawBezel(g:Graphics2D, r:IntRectangle, isPressed:Boolean, 
                                    shadow:ASColor, darkShadow:ASColor, 
                                 highlight:ASColor, lightHighlight:ASColor):void{
                                 
        if(isPressed) {
            drawLoweredBezel(g, r, shadow, darkShadow, highlight, lightHighlight);
        }else {
        	drawUpperedBezel(g, r, shadow, darkShadow, highlight, lightHighlight);
        }
	}
	
	/**
	 * For buttons  by draw line function
	 */	
	public static function paintBezel(g:Graphics2D, r:IntRectangle, isPressed:Boolean, 
                                    shadow:ASColor, darkShadow:ASColor, 
                                 highlight:ASColor, lightHighlight:ASColor):void{
                                 
        if(isPressed) {
            paintLoweredBevel(g, r, shadow, darkShadow, highlight, lightHighlight);
        }else {
        	paintRaisedBevel(g, r, shadow, darkShadow, highlight, lightHighlight);
        }
	}	
	
	/**
	 * Use drawLine 
	 */
    public static function paintRaisedBevel(g:Graphics2D, r:IntRectangle,
                                    shadow:ASColor, darkShadow:ASColor, 
                                 highlight:ASColor, lightHighlight:ASColor):void  {
        var h:Number = r.height - 1;
        var w:Number = r.width - 1;
        var x:Number = r.x + 0.5;
        var y:Number = r.y + 0.5;
        var pen:Pen = new Pen(lightHighlight, 1, false, "normal", "square", "miter");
        g.drawLine(pen, x, y, x, y+h-2);
        g.drawLine(pen, x+1, y, x+w-2, y);
		
		pen.setColor(highlight);
        g.drawLine(pen, x+1, y+1, x+1, y+h-3);
        g.drawLine(pen, x+2, y+1, x+w-3, y+1);

		pen.setColor(darkShadow);
        g.drawLine(pen, x, y+h-1, x+w-1, y+h-1);
        g.drawLine(pen, x+w-1, y, x+w-1, y+h-2);

		pen.setColor(shadow);
        g.drawLine(pen, x+1, y+h-2, x+w-2, y+h-2);
        g.drawLine(pen, x+w-2, y+1, x+w-2, y+h-3);
    }
    
	/**
	 * Use drawLine 
	 */
    public static function paintLoweredBevel(g:Graphics2D, r:IntRectangle,
                                    shadow:ASColor, darkShadow:ASColor, 
                                 highlight:ASColor, lightHighlight:ASColor):void  {
        var h:Number = r.height - 1;
        var w:Number = r.width - 1;
        var x:Number = r.x + 0.5;
        var y:Number = r.y + 0.5;
		var pen:Pen = new Pen(shadow, 1, false, "normal", "square", "miter");
        g.drawLine(pen, x, y, x, y+h-1);
        g.drawLine(pen, x+1, y, x+w-1, y);

       	pen.setColor(darkShadow);
        g.drawLine(pen, x+1, y+1, x+1, y+h-2);
        g.drawLine(pen, x+2, y+1, x+w-2, y+1);

        pen.setColor(lightHighlight);
        g.drawLine(pen, x+1, y+h-1, x+w-1, y+h-1);
        g.drawLine(pen, x+w-1, y+1, x+w-1, y+h-2);

        pen.setColor(highlight);
        g.drawLine(pen, x+2, y+h-2, x+w-2, y+h-2);
        g.drawLine(pen, x+w-2, y+2, x+w-2, y+h-3);
    }
    
    public static function paintButtonBackGround(c:AbstractButton, g:Graphics2D, b:IntRectangle):void{
		var bgColor:ASColor = (c.getBackground() == null ? ASColor.WHITE : c.getBackground());
		if(c.isOpaque()){
			if(c.getModel().isArmed() || c.getModel().isSelected() || !c.isEnabled()){
				g.fillRectangle(new SolidBrush(bgColor), b.x, b.y, b.width, b.height);
			}else{
				drawControlBackground(g, b, bgColor, Math.PI/2);
			}
		}
    }

	public static function drawControlBackground(g:Graphics2D, b:IntRectangle, bgColor:ASColor, direction:Number):void{
		g.fillRectangle(new SolidBrush(bgColor), b.x, b.y, b.width, b.height);
		var x:Number = b.x;
		var y:Number = b.y;
		var w:Number = b.width;
		var h:Number = b.height;
        var colors:Array = [0xFFFFFF, 0xFFFFFF];
		var alphas:Array = [0.75, 0];
		var ratios:Array = [0, 100];
		var matrix:Matrix = new Matrix();
		matrix.createGradientBox(w, h, direction, x, y);       
        var brush:GradientBrush = new GradientBrush(GradientBrush.LINEAR, colors, alphas, ratios, matrix);
        g.fillRectangle(brush, x, y, w, h);
	}
	
	public static function fillGradientRect(g:Graphics2D, b:IntRectangle, c1:ASColor, c2:ASColor, direction:Number, ratios:Array=null):void{
		var x:Number = b.x;
		var y:Number = b.y;
		var w:Number = b.width;
		var h:Number = b.height;
        var colors:Array = [c1.getRGB(), c2.getRGB()];
		var alphas:Array = [c1.getAlpha(), c2.getAlpha()];
		if(ratios == null){
			ratios = [0, 255];
		}
		var matrix:Matrix = new Matrix();
		matrix.createGradientBox(w, h, direction, x, y);       
        var brush:GradientBrush = new GradientBrush(GradientBrush.LINEAR, colors, alphas, ratios, matrix);
        g.fillRectangle(brush, x, y, w, h);
	}    
}

}