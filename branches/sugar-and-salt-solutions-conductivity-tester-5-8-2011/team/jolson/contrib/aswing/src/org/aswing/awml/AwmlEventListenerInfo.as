import org.aswing.EventDispatcher;
/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * Provides access to the event listener info initialized from the AWML file. Consists of 
 * <code>listener</code> which is listener ID registered within {@link AwmlManager} using
 * {@link AwmlManager#addEventListener} method and <code>method</code> which is belonged 
 * to listener and will be called to handle event.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.AwmlEventListenerInfo {
	
	public var listener:String;
	public var method:String;
	
	public function AwmlEventListenerInfo(listener:String, method:String) {
		this.listener = listener;
		this.method = method;
	} 
	
	public function toString():String {
		return "Listener Info: " + listener + "." + method;		
	}
}