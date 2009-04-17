/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.overflow.ComponentDecorator;
import org.aswing.graphics.Graphics;
import org.aswing.Icon;
 
/**
 * GrayFilteredIcon use to filter a icon to be gray.
 * Current this is just approximate effect, will be implement 
 * ture gray filter with flash 8ball new functions.
 * //TODO implement this with flash 8 api
 * @author iiley
 */
class org.aswing.overflow.GrayFilteredIcon extends ComponentDecorator implements Icon{
	public static var DEFAULT_GRAY:Number = 50;
	
	private var icon:Icon;
	private var filterTranform:Object;
	
	/**
	 * Create a gray filtered icon by given icon.
	 * <p>
	 * GrayFilterIcon(icon:Icon) gray by default.<br> 
	 * GrayFilterIcon(icon:Icon, gray:Number)<br>
	 * <p>
	 * @param icon the icon need to be filtered.
	 * @param gray the gray value, in range [-100,-100], default is 50.
	 */
	public function GrayFilteredIcon(icon:Icon, gray:Number){
		super();
		this.icon = icon;
		if(gray == undefined) gray = DEFAULT_GRAY;
		filterTranform = { ra:gray, rb:0, ga:gray, gb:0, ba:gray, bb:0, aa:gray, ab:0};
	}
	
	/**
	 * Return the icon's width.
	 */
	public function getIconWidth():Number{
		return icon.getIconWidth();
	}
	
	/**
	 * Return the icon's height.
	 */
	public function getIconHeight():Number{
		return icon.getIconHeight();
	}
	
	/**
	 * This method call <code>super.createDecorateMC()</code> to create the MC
	 * and then set gray transform to the MC.
	 */
	public function createDecorateMC(c:Component):MovieClip{
		var mc:MovieClip = super.createDecorateMC(c);
		var color:Color = new Color(mc);
		color.setTransform(filterTranform);
		return mc;
	}
	
	public function paintIcon(com:Component, g:Graphics, x:Number, y:Number):Void{
		var mc:MovieClip = this.getCreateDecorateMC(com);
		mc.clear();
		var graphics:Graphics = new Graphics(mc);
		icon.paintIcon(com, graphics, x, y);
	}
	
	public function uninstallIcon(com:Component):Void{
		icon.uninstallIcon(com);
		removeDecorateMC(com);
	}
}
