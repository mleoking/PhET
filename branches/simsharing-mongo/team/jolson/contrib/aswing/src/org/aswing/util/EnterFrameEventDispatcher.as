/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ElementCreater;
import org.aswing.Event;
import org.aswing.EventDispatcher;
import org.aswing.util.Delegate;

/**
 * @author Sadovskiy
 */
class org.aswing.util.EnterFrameEventDispatcher {
	
	/**
	 * When enter frame event occurs.
	 *<br>
	 * onEnterFrame(enterFrameEventDispatcher:Function)
	 */	
	public static var ON_ENTER_FRAME:String = "onEnterFrame";
	
	/**
	 * Singleton instance.
	 */
	private static var instance:EnterFrameEventDispatcher;
	
	/**
	 * Returns instance of the <code>EnterFrameEventDispatcher</code>
	 */
	private static function getInstance():EnterFrameEventDispatcher {
		if (instance == null) {
			instance = new EnterFrameEventDispatcher();	
		}
		return instance;	
	}
	
	/**
	 * addEventListener(listener:Object)<br>
	 * addEventListener(func:Function, obj:Object)<br>
	 * addEventListener(func:Function)<br>
	 * 
	 * Adds a event listener. If you add event listener by the first way, add the
	 * listener of the parameter and return it. You can remove the listener later.
	 * But if you added by the last two ways, the returned value is a new object of
	 * listener, so when you want to remove it, you should use the returned value.
	 * <p>
	 * @param funcOrLis it can be listener instance have the listener method,
	 * and can be a function which want to handler the event
     * @param obj which context to run in by the func.
	 * @return the listener just be added. If you add event listener by function, the return is a 
	 * new object of listener, if you add event listener by a listener object, the return 
	 * is listener object itself.
	 * 
	 * @see #removeEventListener()
	 */
	public static function addEventListener(funcOrLis, obj:Object):Object {
		return (funcOrLis instanceof Function) ?
			getInstance().dispatcher.addEventListener(ON_ENTER_FRAME, funcOrLis, obj) :
			getInstance().dispatcher.addEventListener(funcOrLis);
	}

	/**
	 * Removes the specified event listener.
	 * @param listener the listener which will be removed.
	 */
	public static function removeEventListener(listener:Object):Void {
		getInstance().dispatcher.removeEventListener(listener);
	}
	
	
	private var tickerMC:MovieClip;
	private var dispatcher:EventDispatcher;	
	
	/**
	 * Private Constructor.
	 */
	private function EnterFrameEventDispatcher() {
		
		// create dispatcher
		dispatcher = new EventDispatcher();
		
		// create ticker
		if (tickerMC == null) {
			tickerMC = ElementCreater.getInstance().createMC(_root);
			tickerMC.onEnterFrame = Delegate.create(this, onEnterFrame);
		}
	}

	/**
	 * Dispatches onEnterFrame event to listeners
	 */	
    private function onEnterFrame():Void {
    	dispatcher.dispatchEvent(new Event(EnterFrameEventDispatcher, ON_ENTER_FRAME));
    }

}