/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing {

import flash.display.*;

/**
 * The Cursor definited from Look and Feels.
 * @author iiley
 */
public class Cursor{
	
	/**
	 * Horizontal resize cursor key.
	 */
	public static var H_RESIZE_CURSOR:String = "System.hResizeCursor";

	/**
	 * Vertical resize cursor key.
	 */
	public static var V_RESIZE_CURSOR:String = "System.vResizeCursor";
	
	/**
	 * Horizontal move cursor key.
	 */
	public static var H_MOVE_CURSOR:String = "System.hMoveCursor";

	/**
	 * Vertical move cursor key.
	 */
	public static var V_MOVE_CURSOR:String = "System.vMoveCursor";
	
	/**
	 * All direction resize cursor key.
	 */
	public static var HV_RESIZE_CURSOR:String = "System.hvResizeCursor";
	
	/**
	 * All direction move cursor key.
	 */
	public static var HV_MOVE_CURSOR:String = "System.hvMoveCursor";
	
	/**
	 * Create a cursor from the look and feel defined system cursor.
	 * @param the type of the cursor
	 * @return a cursor, or null if there is not such cursor of this type.
	 */
	public static function createCursor(type:String):DisplayObject{
		var cursor:DisplayObject = UIManager.getInstance(type) as DisplayObject;
		if(cursor == null){
			return null;
		}else if(cursor is Bitmap){
			var sp:Sprite = AsWingUtils.createSprite(null, "bmCursorAdap");
			sp.addChild(cursor);
			cursor.x = -cursor.width/2;
			cursor.y = -cursor.height/2;
			return sp;
		}else{
			return cursor;
		}
	}
}
}