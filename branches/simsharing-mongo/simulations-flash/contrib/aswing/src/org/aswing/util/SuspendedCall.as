import org.aswing.util.Delegate;
import org.aswing.util.EnterFrameEventDispatcher;
import org.aswing.util.ObjectUtils;
/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * Allows to schedule method and function calls postponed on the specified period
 * of time (in frames). 
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.util.SuspendedCall {
	
	private static var instance:SuspendedCall;
	
	private static function getInstance():SuspendedCall {
		if (instance == null) instance = new SuspendedCall();
		return instance;	
	}
	
	/**
	 * createCall(func:Function, delay:Number)
	 * createCall(func:Function, obj:Object, delay:Number)
	 * createCall(func:Function, obj:Object, delay:Number)
	 * createCall(func:Function, obj:Object, args:Array, delay:Number)
	 * 
	 * Schedule postponed function call on the specified amount of frames.
	 *  @param func the function to call after the specified amount of frames
	 *  @param obj (optional) the scope to make a suspended call on. Default is <code>null</code>
	 *  @param args (optional) the arguments array to passed to the called function
	 *  @param delay the delay in frames to make suspended call. Default is <code>1</code>
	 */
	public static function createCall(func:Function):Void {
		var obj:Object = null;
		var args:Array = null;
		var delay:Number = 1;
		
		if (arguments.length == 2) {
			delay = (ObjectUtils.isNumber(arguments[1])) ? arguments[1]: delay;	
		} else if (arguments.length == 3) {
			obj = arguments[1];
			delay = (ObjectUtils.isNumber(arguments[2])) ? arguments[2]: delay;	
		} else if (arguments.length == 4) {
			obj = arguments[1];
			args = (arguments[2] instanceof Array) ? arguments[2]: [arguments[2]];
			delay = (ObjectUtils.isNumber(arguments[3])) ? arguments[3]: delay;	
		}
		
		// create a call
		var call:Function = (obj != null || args != null) ?
			Delegate.createWithArgs(obj, func, args) : func;
		call.delay = delay;
		
		// add call to list
		getInstance().calls.push(call);
	}
	
	private var calls:Array;
	
	private function SuspendedCall() {
		calls = new Array();
		EnterFrameEventDispatcher.addEventListener(__onEnterFrame, this);
	}
	
	private function __onEnterFrame(Void):Void {
		var i:Number = 0;
		while (i < calls.length) {
			var call:Function = calls[i];
			if (--call.delay == 0) {
				calls.splice(i, 1);
				call();	
			} else {
				i++;	
			}
		} 
	}
}