/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;

/**
 * Pen, to draw line drawing.
 * @author iiley
 */
class org.aswing.graphics.Pen{
	private var _thickness:Number;
	private var _color:Number;
	private var _alpha:Number;
	
	/**
	 * Pen(color:ASColor, thickness:Number)<br>
	 * Pen(color:Number, thickness:Number, alpha:Number)<br>
	 */
	function Pen(color:Object, thickness:Number, alpha:Number){
		if(color instanceof ASColor){
			this._thickness=(thickness==undefined)?0:thickness;
			setASColor(ASColor(color));
		}
		else{
			this._color=(color==undefined)?ASColor.BLACK.getRGB():Number(color);
			this._thickness=(thickness==undefined)?0:thickness;
			this._alpha=(alpha==undefined)?100:Number(alpha);
		}
	}
	
	public function getColor():Number{
		return _color;
	}
	
	public function setColor(color:Number):Void{
		this._color=color;
	}
	
	public function getThickness():Number{
		return _thickness;
	}
	
	public function setThickness(thickness:Number):Void{
		this._thickness=thickness;
	}
	
	public function getAlpha():Number{
		return _alpha;
	}
	
	public function setAlpha(alpha:Number):Void{
		this._alpha=alpha;
	}
	
	public function setTo(target:MovieClip):Void{
		target.lineStyle(_thickness, _color, _alpha);
	}
	
	public function setASColor(color:ASColor):Void{
		if(color!=null){			
			this._color=color.getRGB();
			this._alpha=color.getAlpha();
		}
	}
	
	public function getASColor():ASColor{
		return (new ASColor(_color, _alpha));			
	}
	
}
