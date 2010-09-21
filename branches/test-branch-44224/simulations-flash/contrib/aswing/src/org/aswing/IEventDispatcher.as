/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * A interface that indicate a class must implement event dispatcher functions.
 * @see org.aswing.EventDispatcher
 * @author iiley
 */
interface org.aswing.IEventDispatcher{
	
	public function addEventListener(eventNameOrLis:Object, func:Function, obj:Object):Object;
	
	public function removeEventListener(listener:Object):Void;
}
