/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.plaf.asw.ASWingGraphicsUtils;
import org.aswing.plaf.basic.BasicTableHeaderUI;

/**
 * @author iiley
 */
class org.aswing.plaf.asw.ASWingTableHeaderUI extends BasicTableHeaderUI {
	
	public function ASWingTableHeaderUI(){
		super();
	}
	    
	private function paintBackGround(c:Component, g:Graphics, b:Rectangle):Void{
		if(c.isOpaque()){
	 		var bgColor:ASColor = (c.getBackground() == null ? ASColor.WHITE : c.getBackground());
	    	ASWingGraphicsUtils.drawControlBackground(g, b, bgColor, Math.PI/2);
		}
	}
}