/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;

/**
 * Utils functions for Math.
 * @author iiley
 */
class org.aswing.util.MathUtils{
	
	public static var STRING_REPRESENTABLE_MAX:Number = 1E15 - 1;
	public static var STRING_REPRESENTABLE_MIN:Number = 1 - 1E15;
	
	
	public static function roundRectangle(r:Rectangle):Rectangle{
		r.x = Math.round(r.x);
		r.y = Math.round(r.y);
		r.width = Math.round(r.width);
		r.height = Math.round(r.height);
		return r;
	}
	
	public static function roundRectanglePos(r:Rectangle):Rectangle{
		r.x = Math.round(r.x);
		r.y = Math.round(r.y);
		return r;
	}
	
	public static function roundRectangleSize(r:Rectangle):Rectangle{
		r.width = Math.round(r.width);
		r.height = Math.round(r.height);
		return r;
	}
	
	public static function roundSize(s:Dimension):Dimension{
		s.width = Math.round(s.width);
		s.height = Math.round(s.height);
		return s;
	}
	
	public static function roundPoint(p:Point):Point{
		p.x = Math.round(p.x);
		p.y = Math.round(p.y);
		return p;
	}
	
	/**
	 * Searches all passed-in arguments for the element having maximal value and returns it.
	 * 
	 * @param .. any number of arguments of any type
	 * @return element with maximal value among the passed-in arguments
	 */
	public static function max():Number {
		var m:Number = arguments[0];
		for (var i = 1; i < arguments.length; i++) {
			if (m < arguments[i]) m = arguments[i];	
		}	
		return m;
	}

	/**
	 * Searches all passed-in arguments for the element having minimal value and returns it.
	 * 
	 * @param .. any number of arguments of any type
	 * @return element with minimal value among the passed-in arguments
	 */
	public static function min():Number {
		var m:Number = arguments[0];
		for (var i = 1; i < arguments.length; i++) {
			if (m > arguments[i]) m = arguments[i];	
		}	
		return m;
	}
	
	public static function getSign(n:Number):Number {
		return (n > 0) ? 1 : (n < 0) ? -1 : 0;	
	}	
	
	public static function isTendToZero(n:Number):Boolean {
		return (Math.abs(n) < 1E-8);	
	}
}
