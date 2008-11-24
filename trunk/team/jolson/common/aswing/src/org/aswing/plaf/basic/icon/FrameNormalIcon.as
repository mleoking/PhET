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
class org.aswing.plaf.basic.icon.FrameNormalIcon extends FrameIcon {
		
	/**
	 * @param width the width of the icon square.
	 */
	public function FrameNormalIcon(){
		super(DEFAULT_ICON_WIDTH);
	}

	public function paintIcon(com : Component, g : Graphics, x : Number, y : Number) : Void {
		var w:Number = width/2;
		var borderBrush:SolidBrush = new SolidBrush(getColor());
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
