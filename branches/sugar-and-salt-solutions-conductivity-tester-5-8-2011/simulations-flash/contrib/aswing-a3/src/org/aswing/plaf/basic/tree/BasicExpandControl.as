/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.tree{

import org.aswing.*;
import org.aswing.plaf.UIResource;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.tree.TreePath;

/**
 * @private
 */
public class BasicExpandControl implements ExpandControl, UIResource{
	
	public function paintExpandControl(c:Component, g:Graphics2D, bounds:IntRectangle, 
		totalChildIndent:int, path:TreePath, row:int, expanded:Boolean, leaf:Boolean):void{
		if(leaf){
			return;
		}
		var w:int = totalChildIndent;
		var cx:Number = bounds.x - w/2;
		var cy:Number = bounds.y + bounds.height/2;
		var r:Number = 4;
		var trig:Array;
		if(!expanded){
			cx -= 2;
			trig = [new IntPoint(cx, cy-r), new IntPoint(cx, cy+r), new IntPoint(cx+r, cy)];
		}else{
			cy -= 2;
			trig = [new IntPoint(cx-r, cy), new IntPoint(cx+r, cy), new IntPoint(cx, cy+r)];
		}
		g.fillPolygon(new SolidBrush(ASColor.BLACK), trig);
	}
	
}
}