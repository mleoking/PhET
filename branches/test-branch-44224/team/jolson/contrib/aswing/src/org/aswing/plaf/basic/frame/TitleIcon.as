/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.geom.Point;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;
import org.aswing.plaf.UIResource;
import org.aswing.UIManager;

/**
 *
 * @author iiley
 */
class org.aswing.plaf.basic.frame.TitleIcon implements Icon, UIResource {
	private static var width:Number = 16;
	private static var height:Number = 12;
	
	private static var instance:Icon;
	
	//create shared instance
	public static function createInstance():Icon{
		if(instance == null){
			instance = new TitleIcon();
		}
		return instance;
	}
	
	public function TitleIcon(){
	}
	
	public function getIconWidth() : Number {
		return width + 2;
	}

	public function getIconHeight() : Number {
		return height;
	}

	public function paintIcon(com : Component, g : Graphics, x : Number, y : Number) : Void {
		//This is just for test the icon
		//TODO draw a real beautiful icon for AsWing frame title
		//g.fillCircleRingWithThickness(new SolidBrush(ASColor.GREEN), x + WIDTH/2, y + WIDTH/2, WIDTH/2, WIDTH/4);
		var outterRect:ASColor = UIManager.getColor("Frame.activeCaptionBorder");
		//var innerRect:ASColor = UIManager.getColor("Frame.inactiveCaptionBorder");
		var innerRect:ASColor = new ASColor(0xFFFFFF);
		
		x = x + 2;
		
		var w4:Number = width/4;
		var h23:Number = 2*height/3;
		var w2:Number = width/2;
		var h:Number = height;
		var w:Number = width;
		
		var points:Array = new Array();
		points.push(new Point(x, y));
		points.push(new Point(x+w4, y+h));
		points.push(new Point(x+w2, y+h23));
		points.push(new Point(x+w4*3, y+h));
		points.push(new Point(x+w, y));
		points.push(new Point(x+w2, y+h23));
		
		g.drawPolygon(new Pen(outterRect, 2), points);
		g.fillPolygon(new SolidBrush(innerRect), points);
	}

	public function uninstallIcon(com : Component) : Void {
	}
}
