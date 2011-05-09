/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.DecorateIcon;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;
 

/**
 * 
 * @author iiley
 */
class test.ColorIcon extends DecorateIcon{
	private var color:ASColor;
	private var width:Number;
	private var height:Number;
	
	/**
	 * 
	 */
	public function ColorIcon(bottomIcon:Icon, color:ASColor, width:Number, height:Number){
		super(bottomIcon);
		this.color = color;
		this.width = Math.round(width);
		this.height = Math.round(height);
	}

	/**
	 * Return the icon's width.
	 */
	public function getIconWidthImp():Number{
		return width;
	}
	
	/**
	 * Return the icon's height.
	 */
	public function getIconHeightImp():Number{
		return height;
	}
	
	/**
	 * Draw the icon at the specified location.
	 */
	public function paintIconImp(com:Component, g:Graphics, x:Number, y:Number):Void{
		g.fillRectangle(new SolidBrush(color), x, y, width, height);
	}
}
