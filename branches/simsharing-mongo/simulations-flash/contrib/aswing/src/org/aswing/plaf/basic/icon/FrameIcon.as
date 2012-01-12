/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.graphics.Graphics;
import org.aswing.Icon;
import org.aswing.plaf.UIResource;
import org.aswing.UIManager;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.icon.FrameIcon implements Icon, UIResource {
		
	private static var DEFAULT_ICON_WIDTH:Number = 13;
	
	private var width:Number;
	private var height:Number;
	
	private var color:ASColor;
	
	/**
	 * @param width the width of the icon square.
	 */	
	public function FrameIcon(width:Number){
		this.width = width;
		height = width;
		color = UIManager.getColor("Frame.activeCaptionText");
	}
	
	public function getColor():ASColor{
		return color;
	}
	
	public function getIconWidth() : Number {
		return width;
	}

	public function getIconHeight() : Number {
		return height;
	}

	public function paintIcon(com : Component, g : Graphics, x : Number, y : Number) : Void {
	}

	public function uninstallIcon(com : Component) : Void {
	}
}
