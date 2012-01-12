/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.border
{
	
import org.aswing.*;
import org.aswing.border.*;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.plaf.*;

/**
 * @private
 */
public class ProgressBarBorder extends LineBorder implements UIResource
{
	private var color:ASColor;
	public function ProgressBarBorder(){
		super();
	}
	
	override public function updateBorderImp(c:Component, g:Graphics2D, b:IntRectangle):void
	{
		if(color == null){
			color = c.getUI().getColor("ProgressBar.foreground");
			setColor(color);
		}
		super.updateBorderImp(c, g, b);
	}
}
}