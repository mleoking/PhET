/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.GradientBrush;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.plaf.basic.frame.TitleBarUI;
 
class org.aswing.plaf.asw.frame.ASWingTitleBarUI extends TitleBarUI{

	//can't share instance
    public function ASWingTitleBarUI(){
    	super();
    }
	
	private function paintBackGround(com:Component, g:Graphics, b:Rectangle):Void{
		if(!com.isOpaque()){
			 return;
		}
        var x:Number = b.x;
		var y:Number = b.y;
		var w:Number = b.width;
		var h:Number = b.height;
		
		var colors:Array;
		if(frame.isActive()){
	    	colors = [activeColor.darker(0.9).getRGB(), activeColor.getRGB()];
		}else{
	    	colors = [inactiveColor.darker(0.9).getRGB(), inactiveColor.getRGB()];
		}
		var alphas:Array = [100, 100];
		var ratios:Array = [75, 255];
		var matrix:Object = {matrixType:"box", x:x, y:y, w:w, h:h, r:(90/180)*Math.PI};        
	    var brush:GradientBrush=new GradientBrush(GradientBrush.LINEAR, colors, alphas, ratios, matrix);
	    g.fillRoundRect(brush, x, y, w, h, 4, 4, 0, 0);    
		
		if(frame.isActive()){
			colors = [activeColor.getRGB(), activeColor.getRGB()];
		}else{
			colors = [inactiveColor.getRGB(), inactiveColor.getRGB()];
		}
        
		alphas = [80, 0];
		ratios = [0, 100];
	    matrix = {matrixType:"box", x:x, y:y, w:b.width, h:b.height, r:(90/180)*Math.PI};        
        brush=new GradientBrush(GradientBrush.LINEAR, colors, alphas, ratios, matrix);
        g.fillRoundRect(brush, x, y, w, h-h/4, 4, 4, 0, 0);
        var penTool:Pen=new Pen(activeBorderColor);
        g.drawLine(penTool, x, y+h-1, x+w, y+h-1);
	}
}
