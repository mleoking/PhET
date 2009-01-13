/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.JViewport;

/**
 * @author iiley
 */
class test.CenterJViewport extends JViewport {
	
	public function CenterJViewport(view : Component, tracksWidth : Boolean, tracksHeight : Boolean) {
		super(view, tracksWidth, tracksHeight);
	}
	
	private function restrictionViewPos(p:Point):Point{
		var maxPos:Point = getViewMaxPos();
		if(maxPos.x < 0){
			p.x = maxPos.x;
		}else{
			p.x = Math.max(0, Math.min(maxPos.x, p.x));
		}
		if(maxPos.y < 0){
			p.y = maxPos.y;
		}else{
			p.y = Math.max(0, Math.min(maxPos.y, p.y));
		}
		return p;
	}
	
	private function getViewMaxPos():Point{
		var showSize:Dimension = getExtentSize();
		var viewSize:Dimension = getViewSize();
		var p:Point = new Point(viewSize.width-showSize.width, viewSize.height-showSize.height);
		if(p.x < 0){
			p.x = -Math.round((showSize.width - viewSize.width)/2);
		}
		if(p.y < 0){
			p.y = -Math.round((showSize.height - viewSize.height)/2);
		}
		return p;
	}
}