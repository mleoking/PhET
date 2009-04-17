/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.overflow.JTabbedPane;
import org.aswing.plaf.asw.ASWingGraphicsUtils;
import org.aswing.plaf.basic.BasicTabbedPaneUI;
import org.aswing.UIManager;

/**
 * @author iiley
 */
class org.aswing.plaf.asw.ASWingTabbedPaneUI extends BasicTabbedPaneUI {
	
	private var windowBG:ASColor;
	
	public function ASWingTabbedPaneUI(){
		super();
	}
	
	private function installDefaults():Void{
		super.installDefaults();
		windowBG = UIManager.getColor("window");
		if(windowBG == null) windowBG = tabbedPane.getBackground();
	}
	
    /**
     * override this method to draw different tab base line for your LAF
     */
    private function drawBaseLine(tabBarBounds:Rectangle, g:Graphics, fullB:Rectangle):Void{
    	var b:Rectangle = new Rectangle(tabBarBounds);
    	var placement:Number = tabbedPane.getTabPlacement();
    	if(isTabHorizontalPlacing()){
    		var isTop:Boolean = (placement == JTabbedPane.TOP);
    		if(isTop){
    			b.y = b.y + b.height - baseLineThickness;
    		}
    		b.height = baseLineThickness;
    		b.width = fullB.width;
    		b.x = fullB.x;
    		ASWingGraphicsUtils.fillGradientRect(g, b, 
    			tabbedPane.getBackground(), windowBG, 
    			isTop ? Math.PI/2 : -Math.PI/2);
    		var pen:Pen = new Pen(darkShadow, 1);
			if(isTop){
    			g.drawLine(pen, b.x, b.y + 0.5, b.x+b.width, b.y+0.5);
			}else{
				g.drawLine(pen, b.x, b.y + b.height - 0.5, b.x+b.width, b.y + b.height - 0.5);
			}
    	}else{
    		var isLeft:Boolean = (placement == JTabbedPane.LEFT);
    		if(isLeft){
    			b.x = b.x + b.width - baseLineThickness;
    		}
    		b.width = baseLineThickness;
    		b.height = fullB.height;
    		b.y = fullB.y;
    		
    		ASWingGraphicsUtils.fillGradientRect(g, b, 
    			tabbedPane.getBackground(), windowBG, 
    			isLeft ? 0 : -Math.PI);
    		var pen:Pen = new Pen(darkShadow, 1);
			if(isLeft){
    			g.drawLine(pen, b.x+0.5, b.y, b.x+0.5, b.y+b.height);
			}else{
				g.drawLine(pen, b.x+b.width-0.5, b.y, b.x+b.width-0.5, b.y+b.height);
			}
    		
    	}
    }	
	
    /**
     * override this method to draw different tab border for your LAF.<br>
     * Note, you must call setDrawnTabBounds() to set the right bounds for each tab in this method
     */
    private function drawTabBorderAt(index:Number, b:Rectangle, paneBounds:Rectangle, g:Graphics):Void{
    	var placement:Number = tabbedPane.getTabPlacement();
    	if(index == tabbedPane.getSelectedIndex()){
    		b = new Rectangle(b);//make a clone to be safty modification
    		if(isTabHorizontalPlacing()){
    			b.x -= tabBorderInsets.left;
    			b.width += (tabBorderInsets.left + tabBorderInsets.right);
	    		b.height += Math.round(topBlankSpace/2+2);
    			if(placement == JTabbedPane.BOTTOM){
	    			b.y -= 2;
    			}else{
	    			b.y -= Math.round(topBlankSpace/2);
    			}
    		}else{
    			b.y -= tabBorderInsets.left;
    			b.height += (tabBorderInsets.left + tabBorderInsets.right);
	    		b.width += Math.round(topBlankSpace/2+2);
    			if(placement == JTabbedPane.RIGHT){
	    			b.x -= 2;
    			}else{
	    			b.x -= Math.round(topBlankSpace/2);
    			}
    		}
    	}
    	//This is important, should call this in sub-implemented drawTabBorderAt method
    	setDrawnTabBounds(index, b, paneBounds);
    	var x1:Number = b.x+0.5;
    	var y1:Number = b.y+0.5;
    	var x2:Number = b.x + b.width-0.5;
    	var y2:Number = b.y + b.height-0.5;
    	if(placement == JTabbedPane.LEFT){
    		ASWingGraphicsUtils.drawControlBackground(g, b, getTabColor(index), Math.PI/2);
    		
    		var pen:Pen = new Pen(darkShadow, 1);
    		g.beginDraw(pen);
    		g.moveTo(x2, y1);
    		g.lineTo(x1, y1);
    		g.lineTo(x1, y2);
    		g.lineTo(x2, y2);
    		g.endDraw();
    	}else if(placement == JTabbedPane.RIGHT){
    		ASWingGraphicsUtils.drawControlBackground(g, b, getTabColor(index), Math.PI/2);
    		
    		var pen:Pen = new Pen(darkShadow, 1);
    		g.beginDraw(pen);
    		g.moveTo(x1, y1);
    		g.lineTo(x2, y1);
    		g.lineTo(x2, y2);
    		g.lineTo(x1, y2);
    		g.endDraw();
    	}else if(placement == JTabbedPane.BOTTOM){
    		ASWingGraphicsUtils.drawControlBackground(g, b, getTabColor(index), -Math.PI/2);
    		
    		var pen:Pen = new Pen(darkShadow, 1);
    		g.beginDraw(pen);
    		g.moveTo(x1, y1);
    		g.lineTo(x1, y2);
    		g.lineTo(x2, y2);
    		g.lineTo(x2, y1);
    		g.endDraw();
    	}else{
    		ASWingGraphicsUtils.drawControlBackground(g, b, getTabColor(index), Math.PI/2);
    		
    		var pen:Pen = new Pen(darkShadow, 1);
    		g.beginDraw(pen);
    		g.moveTo(x1, y2);
    		g.lineTo(x1, y1);
    		g.lineTo(x2, y1);
    		g.lineTo(x2, y2);
    		g.endDraw();
    		//removed below make it cleaner than button style
//    		x1 += 1;
//    		y1 += 1;
//    		x2 -=1;
//    		y2 -=1;
//    		pen = new Pen(highlight, 1);
//    		g.beginDraw(pen);
//    		g.moveTo(x1, y2);
//    		g.lineTo(x1, y1);
//    		g.lineTo(x2, y1);
//    		g.endDraw();
//    		pen = new Pen(shadow, 1);
//    		g.beginDraw(pen);
//    		g.moveTo(x1, y1);
//    		g.lineTo(x2, y1);
//    		g.lineTo(x2, y2);
//    		g.endDraw();
    	}
    }	
}