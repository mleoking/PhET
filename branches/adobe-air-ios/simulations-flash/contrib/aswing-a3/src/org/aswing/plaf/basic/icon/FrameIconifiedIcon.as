/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.icon{

import org.aswing.*;
import org.aswing.graphics.*;
import org.aswing.geom.*;

/**
 * The icon for frame iconified.
 * @author iiley
 * @private
 */
public class FrameIconifiedIcon extends FrameIcon{
	
	public function FrameIconifiedIcon()
	{
		super();
	}
	
	override public function updateIconImp(c:Component, g:Graphics2D, x:int, y:int):void
	{
		var w:Number = width/2;
		var h:Number = w/3;
		g.fillRectangle(new SolidBrush(getColor(c)), x+h, y+w+h, w, h);	
	}	
}
}