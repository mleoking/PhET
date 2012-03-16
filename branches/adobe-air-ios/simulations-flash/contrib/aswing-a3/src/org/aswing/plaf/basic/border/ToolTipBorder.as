/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic.border{
	
import org.aswing.ASColor;
import org.aswing.border.LineBorder;
import org.aswing.plaf.UIResource;
import org.aswing.Component;
import org.aswing.graphics.Graphics2D;
import org.aswing.geom.IntRectangle;

/**
 * @private
 */
public class ToolTipBorder extends LineBorder implements UIResource
{
	private var color:ASColor;
	
	public function ToolTipBorder()
	{
		super();
	}
	
	override public function updateBorderImp(c:Component, g:Graphics2D, b:IntRectangle):void
	{
		if(color == null){
			color = c.getUI().getColor("ToolTip.borderColor");
			setColor(color);
		}
		super.updateBorderImp(c, g, b);
	}
}
}