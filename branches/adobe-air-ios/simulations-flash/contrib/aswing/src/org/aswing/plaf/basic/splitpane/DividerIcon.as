/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.Icon;
import org.aswing.overflow.JSplitPane;
import org.aswing.plaf.basic.splitpane.Divider;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.splitpane.DividerIcon implements Icon {
	
	public function DividerIcon(){
	}
	
	public function getIconWidth() : Number {
		return 0;
	}

	public function getIconHeight() : Number {
		return 0;
	}

	public function paintIcon(com : Component, g : Graphics, x : Number, y : Number) : Void {
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
	    	var hl:Number = Math.min(5, h-1);
	    	g.drawLine(p, cw, ch-hl, cw, ch+hl);
	    	if(cw + 2 < h){
	    		g.drawLine(p, cw+2, ch-hl, cw+2, ch+hl);
	    	}
	    	if(cw - 2 > 0){
	    		g.drawLine(p, cw-2, ch-hl, cw-2, ch+hl);
	    	}
    	}		
	}

	public function uninstallIcon(com : Component) : Void {
	}

}