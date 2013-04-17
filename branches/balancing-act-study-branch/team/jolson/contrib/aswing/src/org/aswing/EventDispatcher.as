/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Event;
import org.aswing.IEventDispatcher;
import org.aswing.util.ArrayUtils;
import org.aswing.util.Delegate;
import org.aswing.util.StringUtils;

/**
 *
 * @author iiley
 * @author Igor Sadovskiy
 */
class org.aswing.EventDispatcher implements IEventDispatcher{
	
	/**
	 * When the action performed
	 *<br>
	 * onActionPerformed(target:EventDispatcher)
	 */	
	public static var ON_ACT:String = "onActionPerformed";
	
	/**
	 * createEventListener(listener:Object)<br>
	 * createEventListener(eventName:String, func:Function, obj:Object)<br>
	 * createEventListener(eventName:String, func:Function)<br>
	 * 
	 * Creates event listener object.
	 * 
 	 * @param eventTypeOrLis it can be listener instance have the listener method,
	 * and can be a event type to indicate what type of event it want to handle.
	 * @param func if this param is not empty (undefined), the first param listener must be
	 * a String of event name.
	 * @param func the function which want to handler the event indicated by event name.
     * @param obj which context to run in by the func.
	 * @return the created listener . If you create event listener by event name, the return is a 
	 * new object of listener, if you add event listener by first way (a listener object), the return 
	 * is IT (the first parameter).
 	 */		
	public static function createEventListener(eventTypeOrLis:Object, func:Function, obj:Object):Object{
		if(func != undefined){
			var listenerObj:Object = new Object();
			var eventName:String = StringUtils.castString(eventTypeOrLis);
			if(obj != undefined){
				listenerObj[eventName] = Delegate.create(obj, func);
			}else{
				listenerObj[eventName] = func;
			}
			return listenerObj;
		}else{
			return eventTypeOrLis;
		}
	}
	
	
	private var listeners:Array;
	private var dispatchingEnabled:Boolean;
	
	public function EventDispatcher(){
		listeners = new Array();
		dispatchingEnabled = true;
	}
	
	/**
	 * Allows/forbids event dispatching.
	 */
	public function setEventDispatchingEnabled(e:Boolean):Void {
		dispatchingEnabled = e;	
	}
	
	/**
	 * Checks wherever event dispatching is enabled.
	 */
	public function isEventDispatchingEnabled():Boolean {
		return dispatchingEnabled;	
	}
	
	/**
	 * addEventListener(listener:Object)<br>
	 * addEventListener(eventName:String, func:Function, obj:Object)<br>
	 * addEventListener(eventName:String, func:Function)<br>
	 * Add a event listener, if you add event listener by the first way, add the
	 * listener of the parameter and return it. you can remove the listener later.
	 * But if you added by the last two way, the returned value is a new object of
	 * listener, so when you want to remove it, you should use the returned value.
	 * <p>
	 * Note, because of Flash Function.apply() bugs, if your handler function is in a inherit chain, 
	 * and it called super.method, we suggest it should be a full overrided chain, otherwish there might be issues.
	 * <br>For example:
	 * <pre>
	 * ClassA{
	 *     	function method(){
	 *     		trace("A");
	 *     	}
	 * }
	 * ClassB extends ClassA{
	 *     	function method(){
	 *     		super.method();
	 *     		trace("B");
	 *     	}
	 * }
	 * ClassC extends ClassB{
	 *     	function method(){
	 *     		super.method();
	 *     		trace("C");
	 *     	}
	 * }
	 * </pre>
	 * If you add ClassC method to be the handler like:
	 * <pre>
	 * classC.addEventListener(eventName, method, classC);
	 * or
	 * classC.addEventListener(eventName);//if "method" is a eventName
	 * or
	 * classC.addEventListener(eventName, Delegate.create(classC, classC.method));
	 * </pre>
	 * You must keep method declared in ClassC.
	 * If missed the declaration in ClassC, then issue occur, "B" will be traced twice, "A" once.
	 * You can miss the declaration in ClassB in fact. then "C" once, "A" once.
	 * @param eventTypeOrLis it can be listener instance have the listener method,
	 * and can be a event type to indicate what type of event it want to handle.
	 * @param func if this param is not empty(undefined), the first param listener must be
	 * a String of event name.
	 * @param func the function which want to handler the event indicated by event name.
     * @param obj which context to run in by the func.
	 * @return the listener just be added. If you add event listener by event name, the return is a 
	 * new object of listener, if you add event listener by first way(a listener object), the return 
	 * is IT(the first parameter).
	 * @see #removeEventListener()
	 */
	public function addEventListener(eventTypeOrLis:Object, func:Function, obj:Object):Object{
		var listener:Object = createEventListener(eventTypeOrLis, func, obj); 
		listeners.push(listener);
		return listener;
	}
	
	/**
	 * Remove the specified event listener.
	 * @param listener the listener which will be removed.
	 */
	public function removeEventListener(listener:Object):Void{
		ArrayUtils.removeFromArray(listeners, listener);
	}
		
	/**
	 * dispatchEvent(name:String, event:Event)<br>
	 * dispatchEvent(event:Event) default the the listener's method name to event's type name.
	 * <p>
	 * @param name the listener's method name
	 * @param event the event object, event.target = the component object
	 */
	public function dispatchEvent(name:Object, event:Event):Void{
		if (dispatchingEnabled) {
			var funcName:String;
			if(event == undefined){
				event = Event(name);
				funcName = event.getType();
			}else{
				funcName = String(name);
			}
			var n:Number = listeners.length;
			if(n > 0){
				var i:Number = -1;
				while((++i) < n){
					listeners[i][funcName].apply(listeners[i], event.getArguments());
				}
			}
		}
	}
	
	/**
	 * fireActionEvent()<br>
	 * fireActionEvent(arg1, arg2...)<br>
	 * Fires a ON_ACT event.
	 * @param arg1 arg1, arg2... the additonal params need pass to the handlers
	 */
	public function fireActionEvent():Void{
		dispatchEvent(ON_ACT, new Event(this, ON_ACT, arguments));
	}
	
	/**
	 * createEventObj(type:String)<br>
	 * createEventObj(type:String, arg)<br>
	 * createEventObj(type:String, arg1, arg2, ...)<br>
	 * 
	 * Creates new event object for the current scope.
	 * 
	 * @param type the event name
	 * @param arg the additional arguments to be passed to the event handler.
	 * @return constructed event object
	 */
	private function createEventObj(type:String):Event{
		return ( new Event(this, type, arguments.slice(1)) );
	}
	
}
