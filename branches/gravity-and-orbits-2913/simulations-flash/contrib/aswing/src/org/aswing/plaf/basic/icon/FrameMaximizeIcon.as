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
class org.aswing.plaf.basic.icon.FrameMaximizeIcon extends FrameIcon {

	/**
	 * @param width the width of the icon square.
	 */
	public function FrameMaximizeIcon(){
		super(DEFAULT_ICON_WIDTH);
	}	

	public function paintIcon(com : Component, g : Graphics, x : Number, y : Number) : Void {
		var w:Number = width/1.5;
		var borderBrush:SolidBrush = new SolidBrush(getColor());
		g.beginFill(borderBrush);
		g.rectangle(x+w/4, y+w/4, w, w);
		g.rectangle(x+w/4+1, y+w/4+2, w-2, w-3);
		g.endFill();	
	}

}
