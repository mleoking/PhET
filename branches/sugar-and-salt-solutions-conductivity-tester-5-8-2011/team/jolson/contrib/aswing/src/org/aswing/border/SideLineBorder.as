/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.border.Border;
import org.aswing.border.DecorateBorder;
import org.aswing.Component;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.Insets;

/**
 * A border that draw a line at one side of a component.
 * @author iiley
 */
class org.aswing.border.SideLineBorder extends DecorateBorder {
	
    /**
     * The north side constraint (top of component).
     */
    public static var NORTH:Number  = 0;

    /**
     * The south side constraint (bottom of component).
     */
    public static var SOUTH:Number  = 1;

    /**
     * The east side constraint (right side of component).
     */
    public static var EAST :Number  = 2;

    /**
     * The west side constraint (left side of component).
     */
    public static var WEST :Number  = 3;
	
	private var side:Number;
	private var color:ASColor;
	private var thickness:Number;
	
	/**
	 * SideLineBorder(interior:Border, side:Number, color:ASColor, thickness:Number) <br>
	 * SideLineBorder(interior:Border, side:Number, color:ASColor) <br>
	 * SideLineBorder(interior:Border, side:Number) <br>
	 * SideLineBorder(interior:Border) <br>
	 * SideLineBorder() <br>
	 * <p>
	 * @param interior interior border. Default is null;
	 * @param side the side of the line. Must be one of bottom value:
	 * <ul>
	 *   <li>#NORTH
	 *   <li>#SOUTH
	 *   <li>#EAST
	 *   <li>#WEST
	 * </ul>
	 * .Default is NORTH.
	 * @param color the color of the border. Default is ASColor.BLACK
	 * @param thickness the thickness of the border. Default is 1
	 */
	public function SideLineBorder(interior:Border, side:Number, color:ASColor, thickness:Number) {
		super(interior);
		this.side = (side == undefined ? NORTH : side);
		this.color = (color == undefined ? ASColor.BLACK : color);
		this.thickness = (thickness == undefined ? 1 : thickness);
	}

	
    public function paintBorderImp(c:Component, g:Graphics, b:Rectangle):Void{
 		var pen:Pen = new Pen(color, thickness);
 		var x1:Number, x2:Number, y1:Number, y2:Number;
 		if(side == SOUTH){
 			x1 = b.x;
 			y1 = b.y + b.height - thickness/2;
 			x2 = b.x + b.width;
 			y2 = y1;
 		}else if(side == EAST){
 			x1 = b.x + b.width - thickness/2;
 			y1 = b.y;
 			x2 = x1;
 			y2 = b.y + b.height;
 		}else if(side == WEST){
 			x1 = b.x + thickness/2;
 			y1 = b.y;
 			x2 = x1;
 			y2 = b.y + b.height;
 		}else{
 			x1 = b.x;
 			y1 = b.y + thickness/2;
 			x2 = b.x + b.width;
 			y2 = y1;
 		}
 		g.drawLine(pen, x1, y1, x2, y2);
    }
    
    public function getBorderInsetsImp(c:Component, b:Rectangle):Insets{
    	var i:Insets = new Insets();
 		if(side == SOUTH){
 			i.bottom = thickness;
 		}else if(side == EAST){
 			i.right = thickness;
 		}else if(side == WEST){
 			i.left = thickness;
 		}else{
 			i.top = thickness;
 		}
    	return i;
    }
    
	public function getColor():ASColor {
		return color;
	}

	public function setColor(color:ASColor):Void {
		this.color = color;
	}

	public function getThickness():Number {
		return thickness;
	}

	public function setThickness(thickness:Number):Void {
		this.thickness = thickness;
	}

	public function getSide():Number {
		return side;
	}

	public function setSide(side:Number):Void {
		this.side = side;
	}
	    
}

