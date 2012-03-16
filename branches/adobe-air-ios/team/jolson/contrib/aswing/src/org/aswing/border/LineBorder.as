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
import org.aswing.graphics.SolidBrush;
import org.aswing.Insets;

/**
 * @author iiley
 */
class org.aswing.border.LineBorder extends DecorateBorder{
	
	private var color:ASColor;
	private var thickness:Number;
	private var round:Number;
	
	/**
	 * LineBorder(interior:Border, color:ASColor, thickness:Number, roundedCorners:Boolean)<br>
	 * LineBorder(interior:Border, color:ASColor, thickness:Number)<br>
	 * LineBorder(interior:Border, color:ASColor)<br>
	 * LineBorder(interior:Border)<br>
	 * LineBorder()<br>
	 * @param interior interior border. Default is null;
	 * @param color the color of the border. Default is ASColor.BLACK
	 * @param thickness the thickness of the border. Default is 1
	 * @param round round rect radius, default is 0 means normal rectangle, not rect.
	 */
	public function LineBorder(interior:Border, color:ASColor, thickness:Number, round:Number){
		super(interior);
		this.color = (color == undefined ? ASColor.BLACK : color);
		this.thickness = (thickness == undefined ? 1 : thickness);
		this.round = (round == undefined ? 0 : round);
		this.round = Math.max(0, this.round);
	}
	
    public function paintBorderImp(c:Component, g:Graphics, b:Rectangle):Void{
 		var t:Number = thickness;
    	if(round <= 0){
    		g.drawRectangle(new Pen(color, thickness), b.x + t/2, b.y + t/2, b.width - t, b.height - t);
    	}else{
    		g.fillRoundRectRingWithThickness(new SolidBrush(color), b.x, b.y, b.width, b.height, round, t);
    	}
    }
    
    public function getBorderInsetsImp(c:Component, bounds:Rectangle):Insets{
    	var width:Number = Math.ceil(thickness + round - round*0.707106781186547); //0.707106781186547 = Math.sin(45 degrees);
    	return new Insets(width, width, width, width);
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

	public function getRound():Number {
		return round;
	}

	public function setRound(round:Number):Void {
		this.round = round;
	}    
}
