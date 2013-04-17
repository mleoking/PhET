/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.icon{

import org.aswing.*;
import org.aswing.graphics.*;
import org.aswing.geom.*;

/**
 * The icon for frame maximize.
 * @author iiley
 * @private
 */
public class FrameMaximizeIcon extends FrameIcon{

	public function FrameMaximizeIcon(){
		super();
	}	

	override public function updateIconImp(c:Component, g:Graphics2D, x:int, y:int):void
	{
		var w:Number = width/1.5;
		var borderBrush:SolidBrush = new SolidBrush(getColor(c));
		g.beginFill(borderBrush);
		g.rectangle(x+w/4, y+w/4, w, w);
		g.rectangle(x+w/4+1, y+w/4+2, w-2, w-3);
		g.endFill();	
	}	
}

}