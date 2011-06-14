/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.Component;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.plaf.basic.icon.FrameIcon;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.icon.FrameIconifiedIcon extends FrameIcon {

	/**
	 * @param width the width of the icon square.
	 */
	public function FrameIconifiedIcon(){
		super(DEFAULT_ICON_WIDTH);
	}
	
	public function paintIcon(com : Component, g : Graphics, x : Number, y : Number) : Void {
		var w:Number = width/2;
		var h:Number = w/3;
		g.fillRectangle(new SolidBrush(getColor()), x+h, y+w+h, w, h);		
	}
}
