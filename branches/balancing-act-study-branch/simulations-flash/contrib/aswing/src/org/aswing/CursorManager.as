/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASWingUtils;
import org.aswing.Component;
import org.aswing.ElementCreater;
import org.aswing.Image;
import org.aswing.util.Delegate;
import org.aswing.util.DepthManager;

/**
 * The CursorManager, manage the cursor, hide system mouse cursor, show custom cursor, 
 * etc.
 * @author iiley
 */
class org.aswing.CursorManager {
	
	private static var cursorMC:MovieClip;
	private static var cursorImage:Image;
	
	/**
	 * The default layer for cursor, default value is null, mean to use {@link org.aswing.ASWingUtils#getRootMovieClip}.
	 * If you set a specified value to it, it will be used to replace {@link org.aswing.ASWingUtils#getRootMovieClip}.
	 */
	public static var CURSOR_LAYER : MovieClip = null;
	
	/**
	 * showCustomCursor(cursorImage:Image, baseCom:Component)<br>
	 * showCustomCursor(cursorImage:Image)
	 * <p>
	 * Shows a custom image as a the cursor.
	 * @param image the cursor image
	 * @param  baseCom (optional), <code>baseCom.getRootAncestorMovieClip</code> to create cursor mc.
	 *         if miss this param, <code>ASWingUtils.getRootMovieClip()</code> will be 
	 *         called to create the cursor mc. 
	 */
	public static function showCustomCursor(image:Image, baseCom:Component):Void{
		cursorImage = image;
		var rootMC:MovieClip = baseCom.getRootAncestorMovieClip();
		if(rootMC == null){
			if(CURSOR_LAYER != null){
				rootMC = CURSOR_LAYER;
			}else{
				rootMC = ASWingUtils.getRootMovieClip();
			}
		}
		
		cursorMC.onEnterFrame = undefined;
		cursorMC.removeMovieClip();
		cursorMC = ElementCreater.getInstance().createMC(rootMC);
		cursorImage.paintImage(cursorMC, 0, 0);
		Mouse.removeListener(CursorManager);
		Mouse.addListener(CursorManager);
		cursorMC.onEnterFrame = Delegate.create(CursorManager, onEnterFrame);
		onEnterFrame();//update now cursor position instead of waiting a frame loop
	}
	
	public static function hideCustomCursor():Void{
		cursorMC.onEnterFrame = undefined;
		cursorMC.removeMovieClip();
		Mouse.removeListener(CursorManager);
	}
	
	public static function isCustomCursorShowing():Boolean{
		return (cursorMC.onEnterFrame != undefined);
	}
	
	public static function getShowingCursorImage():Image{
		return cursorImage;
	}
	
	public static function showSystemCursor():Void{
		Mouse.show();
	}
	
	public static function hideSystemCursor():Void{
		Mouse.hide();
	}
	
	private static function onMouseMove():Void{
		onEnterFrame();
	}
	
	private static function onEnterFrame():Void{
		DepthManager.bringToTop(cursorMC);
		cursorMC._x = cursorMC._parent._xmouse;
		cursorMC._y = cursorMC._parent._ymouse;
	}	
	
}