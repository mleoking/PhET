/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.icon{

import org.aswing.*;
import org.aswing.graphics.*;
import org.aswing.geom.*;

/**
 * The icon for frame normal.
 * @author iiley
 * @private
 */
public class FrameNormalIcon extends FrameIcon
{

	public function FrameNormalIcon(){
		super();
	}

	override public function updateIconImp(c:Component, g:Graphics2D, x:int, y:int):void
	{
		var w:Number = width/2;
		var borderBrush:SolidBrush = new SolidBrush(getColor(c));
		g.beginFill(borderBrush);
		g.rectangle(x+w/2+1, y+w/4+0.5, w, w);
		g.rectangle(x+w/2+0.5+1, y+w/4+1.5+0.5, w-1, w-2);
		g.endFill();
		g.beginFill(borderBrush);
		g.rectangle(x+w/4, y+w/2+1.5, w, w);
		g.rectangle(x+w/4+0.5, y+w/2+1.5+1.5, w-1, w-2);
		g.endFill();	
	}	
}
}