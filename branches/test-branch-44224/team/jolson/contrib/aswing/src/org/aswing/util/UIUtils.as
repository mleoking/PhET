/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;

/**
 * 
 * @author iiley
 */
class org.aswing.util.UIUtils{
	public static function center(obj:Object, com:Component):Void{
		obj._x = (com.getWidth() - obj._width)/2;
		obj._y = (com.getHeight() - obj._height)/2;
	}
}
