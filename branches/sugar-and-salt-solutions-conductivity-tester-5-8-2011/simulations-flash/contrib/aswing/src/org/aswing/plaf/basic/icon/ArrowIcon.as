/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.geom.Point;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;
import org.aswing.plaf.UIResource;
 
/**
 * Basic Arrow Icon for scrollbar buttons
 * @author iiley
 */
class org.aswing.plaf.basic.icon.ArrowIcon implements Icon, UIResource{
	private var arrow:Number;
	private var width:Number;
	private var height:Number;
	private var background:ASColor;
	private var shadow:ASColor;
	private var darkShadow:ASColor;
	private var highlight:ASColor;
	
	public function ArrowIcon(arrow:Number, size:Number, background:ASColor, shadow:ASColor,
			 darkShadow:ASColor, highlight:ASColor){
		this.arrow = arrow;
		this.width = size;
		this.height = size;
		this.background = background;
		this.shadow = shadow;
		this.darkShadow = darkShadow;
		this.highlight = highlight;
	}

	/**
	 * Return the icon's width.
	 */
	public function getIconWidth():Number{
		return width;
	}
	
	/**
	 * Return the icon's height.
	 */
	public function getIconHeight():Number{
		return height;
	}
	
	/**
	 * Draw the icon at the specified location.
	 */
	public function paintIcon(com:Component, g:Graphics, x:Number, y:Number):Void{
		var center:Point = new Point(com.getWidth()/2, com.getHeight()/2);
		var w:Number = width;
		var ps1:Array = new Array();
		ps1.push(center.nextPoint(arrow, w/2/2));
		var back:Point = center.nextPoint(arrow + Math.PI, w/2/2);
		ps1.push(back.nextPoint(arrow - Math.PI/2, w/2));
		ps1.push(back.nextPoint(arrow + Math.PI/2, w/2));
		
		//w -= (w/4);
		var ps2:Array = new Array();
		ps2.push(center.nextPoint(arrow, w/2/2-1));
		back = center.nextPoint(arrow + Math.PI, w/2/2-1);
		ps2.push(back.nextPoint(arrow - Math.PI/2, w/2-2));
		ps2.push(back.nextPoint(arrow + Math.PI/2, w/2-2));
		
		g.fillPolygon(new SolidBrush(darkShadow), ps1);
		g.fillPolygon(new SolidBrush(shadow), ps2);
	}
	
	public function uninstallIcon(com:Component):Void{
	}	
}
