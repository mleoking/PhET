/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Event;
import org.aswing.EventDispatcher;

/**
 * Wraps {@link Mouse} class. Provides AsWing-generic events support for.
 * 
 * @author Sadovskiy
 */
class org.aswing.MouseManager {
	
	/**
	 * When mouse is down.
	 *<br>
	 * onMouseDown(mouseManager:Function)
	 */	
	public static var ON_MOUSE_DOWN:String = "onMouseDown";
	/**
	 * When mouse is up.
	 *<br>
	 * onMouseUp(MomouseManager:FunctionuseManager)
	 */	
	public static var ON_MOUSE_UP:String = "onMouseUp";
	/**
	 * When mouse is move.
	 *<br>
	 * onMouseMove(mouseManager:Function)
	 */	
	public static var ON_MOUSE_MOVE:String = "onMouseMove";
	/**
	 * When mouse wheel is scrolled.
	 *<br>
	 * onMouseWheel(mouseManager:Function, delta:Number, target:MovieClip)
	 */	
	public static var ON_MOUSE_WHEEL:String = "onMouseWheel";

	
	private static var instance:MouseManager = getInstance();
	
	/** Constructs singleton <code>MouseManager</code> instance. */
	private static function getInstance():MouseManager {
		if (instance == null) {
			instance = new MouseManager();
		}
		return instance;
	}
	
	/**
	 * addEventListener(listener:Object)<br>
	 * addEventListener(eventName:String, func:Function, obj:Object)<br>
	 * addEventListener(eventName:String, func:Function)<br>
	 * 
	 * Add a event listener, if you add event listener by the first way, add the
	 * listener of the parameter and return it. you can remove the listener later.
	 * But if you added by the last two way, the returned value is a new object of
	 * listener, so when you want to remove it, you should use the returned value.
	 * 
	 * @param eventTypeOrLis it can be listener instance have the listener method,
	 * and can be a event type to indicate what type of event it want to handle.
	 * @param func if this param is not empty(undefined), the first param listener must be
	 * a String of event name.
	 * @param func the function which want to handler the event indicated by event name.
     * @param obj which context to run in by the func.
	 * @return the listener just be added. If you add event listener by event name, the return is a 
	 * new object of listener, if you add event listener by first way(a listener object), the return 
	 * is IT(the first parameter).
	 * @see #removeEventListener
	 */
	public static function addEventListener(eventTypeOrLis:Object, func:Function, obj:Object):Object{
		return getInstance().dispatcher.addEventListener(eventTypeOrLis, func, obj);
	}
	
	/**
	 * Remove the specified event listener.
	 * @param listener the listener which will be removed.
	 */
	public static function removeEventListener(listener:Object):Void{
		getInstance().dispatcher.removeEventListener(listener);
	}
	
	/**
	 * Displays standard mouse pointer.
	 */
	public static function show():Void {
		Mouse.show();	
	}

	/**
	 * Hides standard mouse pointer.
	 */
	public static function hide():Void {
		Mouse.hide();	
	}
	
	public static function isMouseDown():Boolean {
		return getInstance().mouseDown;	
	}
	
	/**
	 * Sets standard mouse pointer's visibility.
	 */
	public static function setVisible(v:Boolean):Void {
		if (v) 
			Mouse.show();
		else
			Mouse.hide();	
	}
	
	
	private var dispatcher:EventDispatcher;
	private var mouseDown:Boolean;
	
	/** Private Constrictor. */
	private function MouseManager() {
		dispatcher = new EventDispatcher();
		mouseDown = false;
		Mouse.addListener(this);
	}
	
	/** Fires {@link #ON_MOUSE_DOWN} event. */
	private function fireMouseDown():Void {
		dispatcher.dispatchEvent(new Event(MouseManager, ON_MOUSE_DOWN));
	}

	/** Fires {@link #ON_MOUSE_UP} event. */
	private function fireMouseUp():Void {
		dispatcher.dispatchEvent(new Event(MouseManager, ON_MOUSE_UP));
	}

	/** Fires {@link #ON_MOUSE_MOVE} event. */
	private function fireMouseMove():Void {
		dispatcher.dispatchEvent(new Event(MouseManager, ON_MOUSE_MOVE));
	}

	/** Fires {@link #ON_MOUSE_WHEEL} event. */
	private function fireMouseWheel(delta:Number, target:String):Void {
		dispatcher.dispatchEvent(new Event(MouseManager, ON_MOUSE_WHEEL, [delta, eval(target)]));
	}
	
	/** Notified when the mouse is pressed. */
	private function onMouseDown():Void {
		mouseDown = true;
		fireMouseDown();
	}
 
 	/** Notified when the mouse is released. */
	private function onMouseUp():Void {
		mouseDown = false;
		fireMouseUp();
	}

 	/** Notified when the mouse moves. */
	private function onMouseMove():Void {
		fireMouseMove();
	}
 
 	/** Notified when the user rolls the mouse wheel. */
	private function onMouseWheel(delta:Number, scrollTarget:String):Void {
		fireMouseWheel(delta, scrollTarget);
	}
	
}