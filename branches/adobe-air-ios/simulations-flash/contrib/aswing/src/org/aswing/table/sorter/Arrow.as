/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.Component;
import org.aswing.geom.Point;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.SolidBrush;
import org.aswing.Icon;

/**
 * @author iiley
 */
class org.aswing.table.sorter.Arrow implements Icon {
	
	private var width:Number;
	private var arrow:Number;
	
	public function Arrow(descending:Boolean, size:Number){
		arrow = descending ? Math.PI/2 : -Math.PI/2;
		this.width = size;
	}
	
	public function getIconWidth() : Number {
		return width;
	}

	public function getIconHeight() : Number {
		return width;
	}

	public function paintIcon(com : Component, g : Graphics, x : Number, y : Number) : Void {
		var center:Point = new Point(x, com.getHeight()/2);
		var w:Number = width;
		var ps1:Array = new Array();
		ps1.push(center.nextPoint(0 + arrow, w/3*2));
		ps1.push(center.nextPoint(Math.PI*2/3 + arrow, w/3*2));
		ps1.push(center.nextPoint(Math.PI*4/3 + arrow, w/3*2));
		
		w--;
		var ps2:Array = new Array();
		ps2.push(center.nextPoint(0 + arrow, w/3*2));
		ps2.push(center.nextPoint(Math.PI*2/3 + arrow, w/3*2));
		ps2.push(center.nextPoint(Math.PI*4/3 + arrow, w/3*2));		
		
		g.fillPolygon(new SolidBrush(ASColor.BLACK), ps1);
		g.fillPolygon(new SolidBrush(ASColor.GRAY.brighter()), ps2);		
	}

	public function uninstallIcon(com : Component) : Void {
	}

}