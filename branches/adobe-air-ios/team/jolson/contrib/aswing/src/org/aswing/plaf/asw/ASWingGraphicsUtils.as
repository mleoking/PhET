/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.ASColor;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.GradientBrush;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;

/**
 * @author iiley
 */
class org.aswing.plaf.asw.ASWingGraphicsUtils {
		
	public static function drawControlBackground(g:Graphics, b:Rectangle, bgColor:ASColor, direction:Number):Void{
		g.fillRectangle(new SolidBrush(bgColor), b.x, b.y, b.width, b.height);
		var x:Number = b.x;
		var y:Number = b.y;
		var w:Number = b.width;
		var h:Number = b.height;
        var colors:Array = [0xFFFFFF, 0xFFFFFF];
		var alphas:Array = [75, 0];
		var ratios:Array = [0, 100];
	    var matrix:Object = {matrixType:"box", x:x, y:y, w:w, h:h, r:direction};        
        var brush:GradientBrush = new GradientBrush(GradientBrush.LINEAR, colors, alphas, ratios, matrix);
        g.fillRectangle(brush, x, y, w, h);
	}
	
	public static function fillGradientRect(g:Graphics, b:Rectangle, c1:ASColor, c2:ASColor, direction:Number, ratios:Array):Void{
		var x:Number = b.x;
		var y:Number = b.y;
		var w:Number = b.width;
		var h:Number = b.height;
        var colors:Array = [c1.getRGB(), c2.getRGB()];
		var alphas:Array = [c1.getAlpha(), c2.getAlpha()];
		if(ratios == undefined){
			ratios = [0, 255];
		}
	    var matrix:Object = {matrixType:"box", x:x, y:y, w:w, h:h, r:direction};        
        var brush:GradientBrush = new GradientBrush(GradientBrush.LINEAR, colors, alphas, ratios, matrix);
        g.fillRectangle(brush, x, y, w, h);		
	}
}