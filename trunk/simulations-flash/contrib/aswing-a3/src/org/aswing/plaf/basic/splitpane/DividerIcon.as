package org.aswing.plaf.basic.splitpane
{
import flash.display.DisplayObject;

import org.aswing.Component;
import org.aswing.Icon;
import org.aswing.JSplitPane;
import org.aswing.geom.*;
import org.aswing.graphics.*;

/**
 * @private
 */
public class DividerIcon implements Icon
{
	public function DividerIcon(){
	}
	
	public function updateIcon(com:Component, g:Graphics2D, x:int, y:int):void
	{
    	var w:Number = com.getWidth();
    	var h:Number = com.getHeight();
    	var ch:Number = h/2;
    	var cw:Number = w/2;
    	var divider:Divider = Divider(com);
    	var p:Pen = new Pen(divider.getOwner().getForeground(), 0);
    	if(divider.getOwner().getOrientation() == JSplitPane.VERTICAL_SPLIT){
	    	var hl:Number = Math.min(5, w-1);
	    	g.drawLine(p, cw-hl, ch, cw+hl, ch);
	    	if(ch + 2 < h){
	    		g.drawLine(p, cw-hl, ch+2, cw+hl, ch+2);
	    	}
	    	if(ch - 2 > 0){
	    		g.drawLine(p, cw-hl, ch-2, cw+hl, ch-2);
	    	}
    	}else{
	    	var h2:Number = Math.min(5, h-1);
	    	g.drawLine(p, cw, ch-h2, cw, ch+h2);
	    	if(cw + 2 < h){
	    		g.drawLine(p, cw+2, ch-h2, cw+2, ch+h2);
	    	}
	    	if(cw - 2 > 0){
	    		g.drawLine(p, cw-2, ch-h2, cw-2, ch+h2);
	    	}
    	}			
	}
	
	public function getIconHeight(c:Component):int
	{
		return 0;
	}
	
	public function getIconWidth(c:Component):int
	{
		return 0;
	}
	
	public function getDisplay(c:Component):DisplayObject
	{
		return null;
	}
	
}
}