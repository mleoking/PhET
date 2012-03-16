/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.AdvancedPen;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
 
/**
 *
 * @author iiley
 */
class org.aswing.plaf.basic.BasicGraphicsUtils {
		
	/**
	 * For buttons style bezel by fill function
	 */
	public static function drawUpperedBezel(g:Graphics, r:Rectangle,
                                    shadow:ASColor, darkShadow:ASColor, 
                                 highlight:ASColor, lightHighlight:ASColor):Void{
		var x1:Number = r.x;
		var y1:Number = r.y;
		var w:Number = r.width;
		var h:Number = r.height;
		
		var brush:SolidBrush=new SolidBrush(darkShadow,100);
		g.fillRectangleRingWithThickness(brush, x1, y1, w, h, 1);
		
        brush.setASColor(lightHighlight);
        g.fillRectangleRingWithThickness(brush, x1, y1, w-1, h-1, 1);
        
        brush.setASColor(highlight);
        g.fillRectangleRingWithThickness(brush, x1+1, y1+1, w-2, h-2, 1);
        
        brush.setASColor(shadow);
        g.fillRectangle(brush, x1+w-2, y1+1, 1, h-2);
        g.fillRectangle(brush, x1+1, y1+h-2, w-2, 1);
	}
	
	/**
	 * For buttons style bezel by fill function
	 */
	public static function drawLoweredBezel(g:Graphics, r:Rectangle,
                                    shadow:ASColor, darkShadow:ASColor, 
                                 highlight:ASColor, lightHighlight:ASColor):Void{
                                 	
		var x1:Number = r.x;
		var y1:Number = r.y;
		var w:Number = r.width;
		var h:Number = r.height;
		
        var brush:SolidBrush=new SolidBrush(darkShadow,100);
		g.fillRectangleRingWithThickness(brush, x1, y1, w, h, 1);
		
		brush.setASColor(darkShadow);
        g.fillRectangleRingWithThickness(brush, x1, y1, w-1, h-1, 1);
        
        brush.setASColor(highlight);
        g.fillRectangleRingWithThickness(brush, x1+1, y1+1, w-2, h-2, 1);
        
        brush.setASColor(highlight);
        g.fillRectangle(brush, x1+w-2, y1+1, 1, h-2);
        g.fillRectangle(brush, x1+1, y1+h-2, w-2, 1);
	}
	
	/**
	 * For buttons style bezel by fill function
	 */	
	public static function drawBezel(g:Graphics, r:Rectangle, isPressed:Boolean, 
                                    shadow:ASColor, darkShadow:ASColor, 
                                 highlight:ASColor, lightHighlight:ASColor):Void{
                                 
        if(isPressed) {
            drawLoweredBezel(g, r, shadow, darkShadow, highlight, lightHighlight);
        }else {
        	drawUpperedBezel(g, r, shadow, darkShadow, highlight, lightHighlight);
        }
	}
	
	/**
	 * For buttons  by draw line function
	 */	
	public static function paintBezel(g:Graphics, r:Rectangle, isPressed:Boolean, 
                                    shadow:ASColor, darkShadow:ASColor, 
                                 highlight:ASColor, lightHighlight:ASColor):Void{
                                 
        if(isPressed) {
            paintLoweredBevel(g, r, shadow, darkShadow, highlight, lightHighlight);
        }else {
        	paintRaisedBevel(g, r, shadow, darkShadow, highlight, lightHighlight);
        }
	}	
	
	/**
	 * Use drawLine 
	 */
    public static function paintRaisedBevel(g:Graphics, r:Rectangle,
                                    shadow:ASColor, darkShadow:ASColor, 
                                 highlight:ASColor, lightHighlight:ASColor):Void  {
        var h:Number = r.height - 1;
        var w:Number = r.width - 1;
        var x:Number = r.x + 0.5;
        var y:Number = r.y + 0.5;
        var pen:AdvancedPen = new AdvancedPen(lightHighlight, 1, undefined, "normal", "square", "miter");
        g.drawLine(pen, x, y, x, y+h-2);
        g.drawLine(pen, x+1, y, x+w-2, y);
		
		pen.setASColor(highlight);
        g.drawLine(pen, x+1, y+1, x+1, y+h-3);
        g.drawLine(pen, x+2, y+1, x+w-3, y+1);

		pen.setASColor(darkShadow);
        g.drawLine(pen, x, y+h-1, x+w-1, y+h-1);
        g.drawLine(pen, x+w-1, y, x+w-1, y+h-2);

		pen.setASColor(shadow);
        g.drawLine(pen, x+1, y+h-2, x+w-2, y+h-2);
        g.drawLine(pen, x+w-2, y+1, x+w-2, y+h-3);
    }
    
	/**
	 * Use drawLine 
	 */
    public static function paintLoweredBevel(g:Graphics, r:Rectangle,
                                    shadow:ASColor, darkShadow:ASColor, 
                                 highlight:ASColor, lightHighlight:ASColor):Void  {
        var h:Number = r.height - 1;
        var w:Number = r.width - 1;
        var x:Number = r.x + 0.5;
        var y:Number = r.y + 0.5;
        var pen:AdvancedPen = new AdvancedPen(shadow, 1, undefined, "normal", "square", "miter");
        g.drawLine(pen, x, y, x, y+h-1);
        g.drawLine(pen, x+1, y, x+w-1, y);

       	pen.setASColor(darkShadow);
        g.drawLine(pen, x+1, y+1, x+1, y+h-2);
        g.drawLine(pen, x+2, y+1, x+w-2, y+1);

        pen.setASColor(lightHighlight);
        g.drawLine(pen, x+1, y+h-1, x+w-1, y+h-1);
        g.drawLine(pen, x+w-1, y+1, x+w-1, y+h-2);

        pen.setASColor(highlight);
        g.drawLine(pen, x+2, y+h-2, x+w-2, y+h-2);
        g.drawLine(pen, x+w-2, y+2, x+w-2, y+h-3);
    }	
}
